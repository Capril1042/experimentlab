
package com.experimentlab.simulation;

import com.experimentlab.assignment.Assignment;
import com.experimentlab.assignment.AssignmentService;
import com.experimentlab.event.EventGenerationService;
import com.experimentlab.experiment.Experiment;
import com.experimentlab.experiment.ExperimentRepository;
import com.experimentlab.experiment.ExperimentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.experimentlab.variant.VariantRepository;
import com.experimentlab.variant.VariantSimulationStats;
import com.experimentlab.variant.VariantSimulationResult;
import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SimulationService {

    private static final int MAX_TARGET_USERS = 10_000;

    private final SimulationRepository simulationRepository;
    private final ExperimentRepository experimentRepository;
    private final AssignmentService assignmentService;
    private final EventGenerationService eventGenerationService;
    private final VariantRepository variantRepository;

    public SimulationService(
            SimulationRepository simulationRepository,
            ExperimentRepository experimentRepository,
            AssignmentService assignmentService,
            EventGenerationService eventGenerationService,
            VariantRepository variantRepository
    ) {
        this.simulationRepository = simulationRepository;
        this.experimentRepository = experimentRepository;
        this.assignmentService = assignmentService;
        this.eventGenerationService = eventGenerationService;
        this.variantRepository = variantRepository;
    }

    @Transactional
    public Simulation runSimulation(
            UUID experimentId,
            UUID demoSessionId,
            int targetUsers
    ) {
        // Validate the requested simulation size.
        if (targetUsers <= 0 || targetUsers > MAX_TARGET_USERS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Target users must be between 1 and 10,000"
            );
        }

        // Verify that this experiment belongs to the current demo session.
        Experiment experiment = experimentRepository
                .findByDemoSession_IdAndId(demoSessionId, experimentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Experiment not found"
                ));

        // Only running experiments can be simulated.
        if (experiment.getStatus() != ExperimentStatus.RUNNING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Experiment must be running before a simulation can start"
            );
        }

        // Create and persist the simulation record.
        Simulation simulation = new Simulation();
        simulation.setExperiment(experiment);
        simulation.setTargetUsers(targetUsers);
        simulation.setUsersProcessed(0);
        simulation.setStatus(SimulationStatus.PENDING);
        simulation.setCreatedAt(LocalDateTime.now());

        simulation = simulationRepository.save(simulation);

        // Mark the simulation as running.
        simulation.setStatus(SimulationStatus.RUNNING);
        simulation.setStartedAt(LocalDateTime.now());

        simulation = simulationRepository.save(simulation);

        // Assign synthetic users to variants.
        List<Assignment> assignments =
                assignmentService.createAssignments(
                        simulation,
                        demoSessionId
                );

        // Generate and persist exposure, click, and conversion events.
        eventGenerationService.generateEvents(assignments);

        // Mark the simulation as completed.
        simulation.setUsersProcessed(assignments.size());
        simulation.setStatus(SimulationStatus.COMPLETED);
        simulation.setCompletedAt(LocalDateTime.now());

        return simulationRepository.save(simulation);
    }


    @Transactional(readOnly = true)
    public List<SimulationResponse> getSimulationsForExperiment(
            UUID experimentId,
            UUID demoSessionId
    ) {
        // Verify that this experiment belongs to the current demo session.
        experimentRepository
                .findByDemoSession_IdAndId(demoSessionId, experimentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Experiment not found"
                ));

        // Retrieve simulations newest first.
        return simulationRepository
                .findByExperiment_IdOrderByCreatedAtDesc(experimentId)
                .stream()
                .map(SimulationResponse::from)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<VariantSimulationResult> getSimulationStats(
            UUID experimentId,
            UUID simulationId,
            UUID demoSessionId
    ) {
        // Verify that the experiment belongs to this demo session.
        experimentRepository
                .findByDemoSession_IdAndId(demoSessionId, experimentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Experiment not found"
                ));

        // Verify that the simulation belongs to this experiment.
        Simulation simulation = simulationRepository
                .findByIdAndExperiment_Id(simulationId, experimentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Simulation not found"
                ));

        // Only completed simulations have final analytics.
        if (simulation.getStatus() != SimulationStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Simulation must be completed before results are available"
            );
        }

        return variantRepository.getSimulationStatsByVariant(
                        experimentId,
                        simulationId
                )
                .stream()
                .map(stats -> new VariantSimulationResult(
                        stats.variantId(),
                        stats.variantName(),
                        stats.assignedUsers(),
                        stats.exposures(),
                        stats.clicks(),
                        stats.conversions(),
                        calculateRate(stats.clicks(), stats.exposures()),
                        calculateRate(stats.conversions(), stats.exposures()),
                        calculateRate(stats.conversions(), stats.clicks())
                ))
                .toList();
    }

    private BigDecimal calculateRate(long numerator, long denominator) {
        if (denominator == 0) {
            return BigDecimal.ZERO.setScale(2);
        }

        return BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(denominator),
                        2,
                        RoundingMode.HALF_UP
                );
    }

}


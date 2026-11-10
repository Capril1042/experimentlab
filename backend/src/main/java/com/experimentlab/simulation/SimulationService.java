
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

    public SimulationService(
            SimulationRepository simulationRepository,
            ExperimentRepository experimentRepository,
            AssignmentService assignmentService,
            EventGenerationService eventGenerationService
    ) {
        this.simulationRepository = simulationRepository;
        this.experimentRepository = experimentRepository;
        this.assignmentService = assignmentService;
        this.eventGenerationService = eventGenerationService;
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
}


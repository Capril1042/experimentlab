
package com.experimentlab.experiment;

import com.experimentlab.demo.DemoSession;
import com.experimentlab.demo.DemoSessionResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;
import com.experimentlab.simulation.CreateSimulationRequest;
import com.experimentlab.simulation.Simulation;
import com.experimentlab.simulation.SimulationResponse;
import com.experimentlab.simulation.SimulationService;

import java.util.List;

@RestController
@RequestMapping("/api/experiments")
public class ExperimentController {

    private final ExperimentService experimentService;
    private final DemoSessionResolver demoSessionResolver;
    private final SimulationService simulationService;

    public ExperimentController(
            ExperimentService experimentService,
            DemoSessionResolver demoSessionResolver,
            SimulationService simulationService
    ) {
        this.experimentService = experimentService;
        this.demoSessionResolver = demoSessionResolver;
        this.simulationService = simulationService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ExperimentResponse createExperiment(
            @RequestBody CreateExperimentRequest request,
            HttpServletRequest httpRequest) {

        DemoSession demoSession = demoSessionResolver.resolve(httpRequest)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Valid demo session required"
                ));

        Experiment experiment =
                experimentService.createExperiment(request, demoSession);

        return experimentService.toResponse(experiment);
    }

    @GetMapping
    public List<ExperimentResponse> getExperiments(
            HttpServletRequest httpRequest) {

        DemoSession demoSession = demoSessionResolver.resolve(httpRequest)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Valid demo session required"
                ));

        return experimentService.getExperimentsForDemoSession(
                demoSession.getId()
        );
    }


    @GetMapping("/{id}")
    public ExperimentResponse getExperiment(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {

        DemoSession demoSession = demoSessionResolver.resolve(httpRequest)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Valid demo session required"
                ));

        return experimentService.getExperimentForDemoSession(
                id,
                demoSession.getId()
        );
    }

    @PostMapping("/{id}/start")
    public ExperimentResponse startExperiment(
            @PathVariable UUID id,
            HttpServletRequest httpRequest
    ) {
        DemoSession demoSession = demoSessionResolver.resolve(httpRequest)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Valid demo session required"
                ));

        return experimentService.startExperiment(
                id,
                demoSession.getId()
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{experimentId}/simulations")
    public SimulationResponse runSimulation(
            @PathVariable UUID experimentId,
            @RequestBody CreateSimulationRequest request,
            HttpServletRequest httpRequest
    ) {
        DemoSession demoSession = demoSessionResolver.resolve(httpRequest)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Valid demo session required"
                ));

        if (request.targetUsers() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Target users are required"
            );
        }

        Simulation simulation = simulationService.runSimulation(
                experimentId,
                demoSession.getId(),
                request.targetUsers()
        );

        return SimulationResponse.from(simulation);
    }

}

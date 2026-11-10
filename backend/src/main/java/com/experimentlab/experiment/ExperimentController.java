
package com.experimentlab.experiment;

import com.experimentlab.demo.DemoSession;
import com.experimentlab.demo.DemoSessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/experiments")
public class ExperimentController {

    private final ExperimentService experimentService;
    private final DemoSessionService demoSessionService;

    public ExperimentController(
            ExperimentService experimentService,
            DemoSessionService demoSessionService) {
        this.experimentService = experimentService;
        this.demoSessionService = demoSessionService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ExperimentResponse createExperiment(
            @RequestBody CreateExperimentRequest request,
            HttpServletRequest httpRequest) {

        String sessionId = null;

        if (httpRequest.getCookies() != null) {
            for (Cookie cookie : httpRequest.getCookies()) {
                if ("DEMO_SESSION_ID".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }

        if (sessionId == null) {
            throw new RuntimeException("Demo session required");
        }

        UUID id = UUID.fromString(sessionId);

        DemoSession demoSession = demoSessionService
                .findValidSession(id)
                .orElseThrow(() ->
                        new RuntimeException("Invalid demo session"));

        Experiment experiment = experimentService.createExperiment(request, demoSession);

        return experimentService.toResponse(experiment);
    }

    @GetMapping
    public List<ExperimentResponse> getExperiments(
            HttpServletRequest httpRequest) {

        String sessionId = null;

        if (httpRequest.getCookies() != null) {
            for (Cookie cookie : httpRequest.getCookies()) {
                if ("DEMO_SESSION_ID".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }

        if (sessionId == null) {
            throw new RuntimeException("Demo session required");
        }

        UUID id = UUID.fromString(sessionId);

        DemoSession demoSession = demoSessionService
                .findValidSession(id)
                .orElseThrow(() -> new RuntimeException("Invalid demo session"));

        return experimentService.getExperimentsForDemoSession(demoSession.getId());
    }
}

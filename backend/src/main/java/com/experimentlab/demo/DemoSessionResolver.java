package com.experimentlab.demo;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DemoSessionResolver {

    private final DemoSessionService demoSessionService;

    public DemoSessionResolver(DemoSessionService demoSessionService) {
        this.demoSessionService = demoSessionService;
    }

    public Optional<DemoSession> resolve(HttpServletRequest request) {

        String sessionId = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("DEMO_SESSION_ID".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }

        if (sessionId == null) {
            return Optional.empty();
        }

        UUID id;

        try {
            id = UUID.fromString(sessionId);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        return demoSessionService.findValidSession(id);
    }
}

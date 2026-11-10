package com.experimentlab.demo;

import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.UUID;

@RestController
public class DemoSessionController {

    private final DemoSessionService demoSessionService;

    public DemoSessionController(DemoSessionService demoSessionService) {
        this.demoSessionService = demoSessionService;
    }

    @GetMapping("/api/demo/session")
    public DemoSessionResponse getOrCreateSession(
            HttpServletRequest request,
            HttpServletResponse response) {

        UUID sessionId = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("DEMO_SESSION_ID".equals(cookie.getName())) {
                    try {
                        sessionId = UUID.fromString(cookie.getValue());
                    } catch (IllegalArgumentException ignored) {
                        // Invalid cookie; create a new session.
                    }
                    break;
                }
            }
        }

        DemoSession session = sessionId == null
                ? null
                : demoSessionService.findValidSession(sessionId).orElse(null);

        if (session == null) {
            session = demoSessionService.createDemoSession();
        }

        Cookie cookie = new Cookie("DEMO_SESSION_ID", session.getId().toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);

        response.addCookie(cookie);

        return new DemoSessionResponse(true, session.getExpiresAt());
    }
}

package com.experimentlab.demo;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class DemoSessionService {

    private final DemoSessionRepository demoSessionRepository;

    public DemoSessionService(DemoSessionRepository demoSessionRepository) {
        this.demoSessionRepository = demoSessionRepository;
    }

    public DemoSession createDemoSession() {
        LocalDateTime now = LocalDateTime.now();

        DemoSession session = new DemoSession();
        session.setCreatedAt(now);
        session.setExpiresAt(now.plusHours(24));

        return demoSessionRepository.save(session);
    }

    public boolean isSessionValid(DemoSession session) {
        return session.getExpiresAt().isAfter(LocalDateTime.now());
    }

    public Optional<DemoSession> findValidSession(UUID sessionId) {
        return demoSessionRepository.findById(sessionId)
                .filter(this::isSessionValid);
    }
}

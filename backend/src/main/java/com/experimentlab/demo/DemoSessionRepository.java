package com.experimentlab.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DemoSessionRepository
        extends JpaRepository<DemoSession, UUID> {
}

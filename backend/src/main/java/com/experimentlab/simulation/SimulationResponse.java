package com.experimentlab.simulation;

import java.time.LocalDateTime;
import java.util.UUID;

public record SimulationResponse(
        UUID id,
        SimulationStatus status,
        Integer targetUsers,
        Integer usersProcessed,
        LocalDateTime createdAt,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {
    public static SimulationResponse from(Simulation simulation) {
        return new SimulationResponse(
                simulation.getId(),
                simulation.getStatus(),
                simulation.getTargetUsers(),
                simulation.getUsersProcessed(),
                simulation.getCreatedAt(),
                simulation.getStartedAt(),
                simulation.getCompletedAt()
        );
    }
}


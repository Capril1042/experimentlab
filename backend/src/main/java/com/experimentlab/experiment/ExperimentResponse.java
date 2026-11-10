package com.experimentlab.experiment;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExperimentResponse(
        UUID id,
        String name,
        String description,
        String hypothesis,
        PrimaryMetric primaryMetric,
        ExperimentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

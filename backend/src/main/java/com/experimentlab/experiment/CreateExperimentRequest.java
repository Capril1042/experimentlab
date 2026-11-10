package com.experimentlab.experiment;

public record CreateExperimentRequest(
        String name,
        String description,
        String hypothesis,
        PrimaryMetric primaryMetric
) {
}

package com.experimentlab.variant;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record VariantResponse(
        UUID id,
        UUID experimentId,
        String name,
        String description,
        Map<String, Object> configuration,
        BigDecimal trafficPercentage
) {}

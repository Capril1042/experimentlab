package com.experimentlab.variant;

import java.math.BigDecimal;
import java.util.Map;

public record UpdateVariantRequest(
        String name,
        String description,
        Map<String, Object> configuration,
        BigDecimal trafficPercentage
) {}


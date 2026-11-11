
package com.experimentlab.variant;

import java.math.BigDecimal;
import java.util.UUID;

public record VariantSimulationResult(
        UUID variantId,
        String variantName,
        long assignedUsers,
        long exposures,
        long clicks,
        long conversions,
        BigDecimal clickThroughRate,
        BigDecimal conversionRate,
        BigDecimal clickToConversionRate
) {
}


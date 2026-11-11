
package com.experimentlab.variant;

import java.util.UUID;

public record VariantSimulationStats(
        UUID variantId,
        String variantName,
        long assignedUsers,
        long exposures,
        long clicks,
        long conversions
) {
}


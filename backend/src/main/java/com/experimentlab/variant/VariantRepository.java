
package com.experimentlab.variant;

import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VariantRepository extends JpaRepository<Variant, UUID> {

    List<Variant> findByExperiment_IdOrderByNameAsc(UUID experimentId);

    Optional<Variant> findByIdAndExperiment_Id(
            UUID variantId,
            UUID experimentId
    );

    @Query("""
        SELECT COALESCE(SUM(v.trafficPercentage), 0)
        FROM Variant v
        WHERE v.experiment.id = :experimentId
        """)
    BigDecimal getTotalTrafficPercentage(
            @Param("experimentId") UUID experimentId
    );
}

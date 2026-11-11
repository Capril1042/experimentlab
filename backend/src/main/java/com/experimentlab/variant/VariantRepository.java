
package com.experimentlab.variant;

import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.experimentlab.assignment.Assignment;
import com.experimentlab.event.Event;

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


    @Query("""
    SELECT new com.experimentlab.variant.VariantSimulationStats(
        v.id,
        v.name,
        COUNT(DISTINCT a.id),
        COUNT(DISTINCT CASE
            WHEN e.eventType = com.experimentlab.event.EventType.EXPOSURE
            THEN a.id
        END),
        COUNT(DISTINCT CASE
            WHEN e.eventType = com.experimentlab.event.EventType.CLICK
            THEN a.id
        END),
        COUNT(DISTINCT CASE
            WHEN e.eventType = com.experimentlab.event.EventType.CONVERSION
            THEN a.id
        END)
    )
    FROM Variant v
    LEFT JOIN Assignment a
        ON a.variant = v
        AND a.simulation.id = :simulationId
    LEFT JOIN Event e
        ON e.assignment = a
    WHERE v.experiment.id = :experimentId
    GROUP BY v.id, v.name
    ORDER BY v.name
    """)
    List<VariantSimulationStats> getSimulationStatsByVariant(
            @Param("experimentId") UUID experimentId,
            @Param("simulationId") UUID simulationId
    );

}

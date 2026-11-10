
package com.experimentlab.variant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VariantRepository extends JpaRepository<Variant, UUID> {

    List<Variant> findByExperiment_IdOrderByNameAsc(UUID experimentId);

    Optional<Variant> findByIdAndExperiment_Id(
            UUID variantId,
            UUID experimentId
    );
}

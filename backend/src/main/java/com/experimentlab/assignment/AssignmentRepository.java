
package com.experimentlab.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssignmentRepository
        extends JpaRepository<Assignment, UUID> {

    List<Assignment> findBySimulation_Id(UUID simulationId);

    long countBySimulation_Id(UUID simulationId);

    long countBySimulation_IdAndVariant_Id(
            UUID simulationId,
            UUID variantId
    );
}


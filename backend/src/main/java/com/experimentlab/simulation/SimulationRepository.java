
package com.experimentlab.simulation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SimulationRepository
        extends JpaRepository<Simulation, UUID> {

    List<Simulation> findByExperiment_IdOrderByCreatedAtDesc(
            UUID experimentId
    );

    Optional<Simulation> findByIdAndExperiment_Id(
            UUID simulationId,
            UUID experimentId
    );
}


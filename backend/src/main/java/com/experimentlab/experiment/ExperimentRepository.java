
package com.experimentlab.experiment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExperimentRepository
        extends JpaRepository<Experiment, UUID> {

    List<Experiment> findByDemoSession_Id(UUID demoSessionId);

    Optional<Experiment> findByIdAndDemoSession_Id(
            UUID id,
            UUID demoSessionId
    );

    Optional<Experiment> findByDemoSession_IdAndId(
            UUID demoSessionId,
            UUID id
    );
}

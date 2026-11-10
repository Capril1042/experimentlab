package com.experimentlab.experiment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ExperimentRepository
        extends JpaRepository<Experiment, UUID> {

    List<Experiment> findByDemoSession_Id(UUID demoSessionId);
}
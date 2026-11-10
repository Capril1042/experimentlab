package com.experimentlab.experiment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.experimentlab.demo.DemoSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ExperimentService {

    private final ExperimentRepository experimentRepository;

    public ExperimentService(ExperimentRepository experimentRepository) {
        this.experimentRepository = experimentRepository;
    }

    @Transactional(readOnly = true)
    public Experiment getExperimentById(UUID id) {
        return experimentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Experiment not found: " + id));
    }

    @Transactional
    public Experiment createExperiment(
            CreateExperimentRequest request,
            DemoSession demoSession) {
        Experiment experiment = new Experiment();

        experiment.setName(request.name());
        experiment.setDescription(request.description());
        experiment.setHypothesis(request.hypothesis());
        experiment.setPrimaryMetric(request.primaryMetric());
        experiment.setStatus(ExperimentStatus.DRAFT);
        experiment.setDemoSession(demoSession);

        LocalDateTime now = LocalDateTime.now();

        experiment.setCreatedAt(now);
        experiment.setUpdatedAt(now);

        return experimentRepository.save(experiment);
    }

    public ExperimentResponse toResponse(Experiment experiment) {
        return new ExperimentResponse(
                experiment.getId(),
                experiment.getName(),
                experiment.getDescription(),
                experiment.getHypothesis(),
                experiment.getPrimaryMetric(),
                experiment.getStatus(),
                experiment.getCreatedAt(),
                experiment.getUpdatedAt()
        );
    }
    @Transactional(readOnly = true)
    public List<ExperimentResponse> getExperimentsForDemoSession(UUID demoSessionId) {
        return experimentRepository.findByDemoSession_Id(demoSessionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExperimentResponse getExperimentForDemoSession(
            UUID experimentId,
            UUID demoSessionId
    ) {
        Experiment experiment = experimentRepository
                .findByDemoSession_IdAndId(demoSessionId, experimentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Experiment not found"
                ));

        return toResponse(experiment);
    }

}

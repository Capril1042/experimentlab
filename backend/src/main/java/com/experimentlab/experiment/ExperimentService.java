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
import com.experimentlab.variant.VariantRepository;
import java.math.BigDecimal;

@Service
public class ExperimentService {

    private final ExperimentRepository experimentRepository;

    private final VariantRepository variantRepository;

    public ExperimentService(
            ExperimentRepository experimentRepository,
            VariantRepository variantRepository
    ) {
        this.experimentRepository = experimentRepository;
        this.variantRepository = variantRepository;
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

    @Transactional
    public ExperimentResponse startExperiment(
            UUID experimentId,
            UUID demoSessionId
    ) {
        Experiment experiment = experimentRepository
                .findByDemoSession_IdAndId(demoSessionId, experimentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Experiment not found"
                ));

        if (experiment.getStatus() != ExperimentStatus.DRAFT) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only draft experiments can be started"
            );
        }

        BigDecimal totalTraffic = variantRepository
                .getTotalTrafficPercentage(experimentId);

        if (totalTraffic.compareTo(new BigDecimal("100.00")) != 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Variant traffic allocation must total exactly 100%"
            );
        }

        experiment.setStatus(ExperimentStatus.RUNNING);
        experiment.setUpdatedAt(LocalDateTime.now());

        return toResponse(experimentRepository.save(experiment));
    }

}

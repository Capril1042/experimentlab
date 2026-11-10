
package com.experimentlab.variant;

import com.experimentlab.demo.DemoSession;
import com.experimentlab.experiment.Experiment;
import com.experimentlab.experiment.ExperimentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class VariantService {

    private final VariantRepository variantRepository;
    private final ExperimentRepository experimentRepository;

    public VariantService(
            VariantRepository variantRepository,
            ExperimentRepository experimentRepository
    ) {
        this.variantRepository = variantRepository;
        this.experimentRepository = experimentRepository;
    }

    @Transactional(readOnly = true)
    public List<VariantResponse> getVariants(
            UUID experimentId,
            UUID demoSessionId
    ) {
        getOwnedExperiment(experimentId, demoSessionId);

        return variantRepository
                .findByExperiment_IdOrderByNameAsc(experimentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public VariantResponse createVariant(
            UUID experimentId,
            UUID demoSessionId,
            CreateVariantRequest request
    ) {
        Experiment experiment = getOwnedExperiment(
                experimentId,
                demoSessionId
        );

        Variant variant = new Variant();
        variant.setExperiment(experiment);
        variant.setName(request.name());
        variant.setDescription(request.description());
        variant.setConfiguration(
                request.configuration() != null
                        ? request.configuration()
                        : Map.of()
        );
        variant.setTrafficPercentage(request.trafficPercentage());

        return toResponse(variantRepository.save(variant));
    }

    @Transactional
    public VariantResponse updateVariant(
            UUID experimentId,
            UUID variantId,
            UUID demoSessionId,
            UpdateVariantRequest request
    ) {
        getOwnedExperiment(experimentId, demoSessionId);

        Variant variant = getVariant(experimentId, variantId);

        variant.setName(request.name());
        variant.setDescription(request.description());
        variant.setConfiguration(
                request.configuration() != null
                        ? request.configuration()
                        : Map.of()
        );
        variant.setTrafficPercentage(request.trafficPercentage());

        return toResponse(variantRepository.save(variant));
    }

    @Transactional
    public void deleteVariant(
            UUID experimentId,
            UUID variantId,
            UUID demoSessionId
    ) {
        getOwnedExperiment(experimentId, demoSessionId);

        Variant variant = getVariant(experimentId, variantId);

        variantRepository.delete(variant);
    }

    private Experiment getOwnedExperiment(
            UUID experimentId,
            UUID demoSessionId
    ) {
        return experimentRepository
                .findByDemoSession_IdAndId(demoSessionId, experimentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Experiment not found"
                ));
    }

    private Variant getVariant(UUID experimentId, UUID variantId) {
        return variantRepository
                .findByIdAndExperiment_Id(variantId, experimentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Variant not found"
                ));
    }

    private VariantResponse toResponse(Variant variant) {
        return new VariantResponse(
                variant.getId(),
                variant.getExperiment().getId(),
                variant.getName(),
                variant.getDescription(),
                variant.getConfiguration(),
                variant.getTrafficPercentage()
        );
    }
}


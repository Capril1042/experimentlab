
package com.experimentlab.assignment;

import com.experimentlab.experiment.Experiment;
import com.experimentlab.experiment.ExperimentRepository;
import com.experimentlab.experiment.ExperimentStatus;
import com.experimentlab.simulation.Simulation;
import com.experimentlab.variant.Variant;
import com.experimentlab.variant.VariantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class AssignmentService {

    private static final int MAX_TARGET_USERS = 10_000;

    private final AssignmentRepository assignmentRepository;
    private final VariantRepository variantRepository;
    private final ExperimentRepository experimentRepository;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            VariantRepository variantRepository,
            ExperimentRepository experimentRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.variantRepository = variantRepository;
        this.experimentRepository = experimentRepository;
    }

    @Transactional
    public List<Assignment> createAssignments(
            Simulation simulation,
            UUID demoSessionId
    ) {
        UUID experimentId = simulation.getExperiment().getId();

        Experiment experiment = experimentRepository
                .findByDemoSession_IdAndId(demoSessionId, experimentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Experiment not found"
                ));

        if (experiment.getStatus() != ExperimentStatus.RUNNING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Experiment must be running before assignments can be created"
            );
        }

        int targetUsers = simulation.getTargetUsers();

        if (targetUsers <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Target users must be greater than zero"
            );
        }

        if (targetUsers > MAX_TARGET_USERS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A simulation cannot exceed 10,000 users"
            );
        }

        List<Variant> variants = variantRepository
                .findByExperiment_IdOrderByNameAsc(experimentId);

        if (variants.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one variant is required"
            );
        }

        BigDecimal totalTraffic = variants.stream()
                .map(Variant::getTrafficPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalTraffic.compareTo(new BigDecimal("100.00")) != 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Variant traffic allocation must total 100%"
            );
        }

        List<VariantAllocation> allocations =
                calculateAllocations(variants, targetUsers);

        List<Variant> assignmentPlan = new ArrayList<>();

        for (VariantAllocation allocation : allocations) {
            for (int i = 0; i < allocation.userCount(); i++) {
                assignmentPlan.add(allocation.variant());
            }
        }

        java.util.Collections.shuffle(assignmentPlan);

        List<Assignment> assignments = new ArrayList<>();
        LocalDateTime assignedAt = LocalDateTime.now();

        for (Variant variant : assignmentPlan) {
            Assignment assignment = new Assignment();
            assignment.setSimulation(simulation);
            assignment.setExperiment(experiment);
            assignment.setVariant(variant);
            assignment.setSimulatedUserId(UUID.randomUUID());
            assignment.setAssignedAt(assignedAt);

            assignments.add(assignment);
        }

        return assignmentRepository.saveAll(assignments);
    }

    private List<VariantAllocation> calculateAllocations(
            List<Variant> variants,
            int targetUsers
    ) {
        List<VariantAllocation> allocations = new ArrayList<>();

        int assignedUsers = 0;

        for (Variant variant : variants) {
            BigDecimal exactCount = variant.getTrafficPercentage()
                    .multiply(BigDecimal.valueOf(targetUsers))
                    .divide(new BigDecimal("100"), 10, RoundingMode.DOWN);

            int baseCount = exactCount.intValue();

            BigDecimal remainder = exactCount.subtract(
                    BigDecimal.valueOf(baseCount)
            );

            allocations.add(new VariantAllocation(
                    variant,
                    baseCount,
                    remainder
            ));

            assignedUsers += baseCount;
        }

        int remainingUsers = targetUsers - assignedUsers;

        allocations.sort(
                Comparator.comparing(VariantAllocation::remainder)
                        .reversed()
        );

        for (int i = 0; i < remainingUsers; i++) {
            VariantAllocation allocation = allocations.get(i);

            allocations.set(i, new VariantAllocation(
                    allocation.variant(),
                    allocation.userCount() + 1,
                    allocation.remainder()
            ));
        }

        return allocations;
    }

    private record VariantAllocation(
            Variant variant,
            int userCount,
            BigDecimal remainder
    ) {
    }
}


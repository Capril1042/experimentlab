
package com.experimentlab.assignment;

import com.experimentlab.experiment.Experiment;
import com.experimentlab.experiment.ExperimentRepository;
import com.experimentlab.experiment.ExperimentStatus;
import com.experimentlab.simulation.Simulation;
import com.experimentlab.variant.Variant;
import com.experimentlab.variant.VariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    private static final UUID EXPERIMENT_ID = UUID.randomUUID();
    private static final UUID SESSION_ID = UUID.randomUUID();

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private ExperimentRepository experimentRepository;

    @InjectMocks
    private AssignmentService assignmentService;

    private Experiment experiment;
    private Simulation simulation;

    @BeforeEach
    void setUp() {
        experiment = mock(Experiment.class);
        simulation = mock(Simulation.class);

        when(simulation.getExperiment()).thenReturn(experiment);
        when(experiment.getId()).thenReturn(EXPERIMENT_ID);
        when(experiment.getStatus()).thenReturn(ExperimentStatus.RUNNING);

        when(experimentRepository.findByDemoSession_IdAndId(
                SESSION_ID,
                EXPERIMENT_ID
        )).thenReturn(Optional.of(experiment));
    }

    @Test
    void assignsUsersAccordingTo50_50TrafficSplit() {
        Variant control = createVariant("50");
        Variant treatment = createVariant("50");

        List<Assignment> assignments = runAllocation(
                1000, control, treatment
        );

        assertEquals(1000, assignments.size());
        assertEquals(500, countAssignments(assignments, control));
        assertEquals(500, countAssignments(assignments, treatment));
        assertUniqueUserIds(assignments);
    }

    @Test
    void assignsUsersAccordingTo70_30TrafficSplit() {
        Variant control = createVariant("70");
        Variant treatment = createVariant("30");

        List<Assignment> assignments = runAllocation(
                1000, control, treatment
        );

        assertEquals(700, countAssignments(assignments, control));
        assertEquals(300, countAssignments(assignments, treatment));
    }

    @Test
    void distributesRemainderUsingLargestRemainderMethod() {
        Variant control = createVariant("33.33");
        Variant headline = createVariant("33.33");
        Variant layout = createVariant("33.34");

        List<Assignment> assignments = runAllocation(
                1000, control, headline, layout
        );

        assertEquals(333, countAssignments(assignments, control));
        assertEquals(333, countAssignments(assignments, headline));
        assertEquals(334, countAssignments(assignments, layout));
        assertEquals(1000, assignments.size());
    }

    @Test
    void rejectsTrafficAllocationThatDoesNotTotal100Percent() {
        Variant control = createVariant("50");
        Variant treatment = createVariant("40");

        assertThrows(
                ResponseStatusException.class,
                () -> runAllocation(1000, control, treatment)
        );

        verify(assignmentRepository, never()).saveAll(anyList());
    }

    @Test
    void rejectsSimulationWithMoreThan10000Users() {
        when(simulation.getTargetUsers()).thenReturn(10001);

        assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.createAssignments(
                        simulation, SESSION_ID
                )
        );

        verify(variantRepository, never())
                .findByExperiment_IdOrderByNameAsc(EXPERIMENT_ID);
    }

    @Test
    void rejectsSimulationWithZeroUsers() {
        when(simulation.getTargetUsers()).thenReturn(0);

        assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.createAssignments(
                        simulation, SESSION_ID
                )
        );

        verify(assignmentRepository, never()).saveAll(anyList());
    }

    private List<Assignment> runAllocation(
            int targetUsers,
            Variant... variants
    ) {
        when(simulation.getTargetUsers()).thenReturn(targetUsers);

        when(variantRepository.findByExperiment_IdOrderByNameAsc(
                EXPERIMENT_ID
        )).thenReturn(List.of(variants));

        lenient().when(assignmentRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        return assignmentService.createAssignments(
                simulation, SESSION_ID
        );
    }

    private Variant createVariant(String trafficPercentage) {
        Variant variant = mock(Variant.class);

        when(variant.getTrafficPercentage())
                .thenReturn(new BigDecimal(trafficPercentage));

        return variant;
    }

    private long countAssignments(
            List<Assignment> assignments,
            Variant variant
    ) {
        return assignments.stream()
                .filter(assignment -> assignment.getVariant() == variant)
                .count();
    }

    private void assertUniqueUserIds(List<Assignment> assignments) {
        Set<UUID> userIds = assignments.stream()
                .map(Assignment::getSimulatedUserId)
                .collect(Collectors.toSet());

        assertEquals(assignments.size(), userIds.size());
    }
}

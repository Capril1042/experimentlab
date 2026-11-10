
package com.experimentlab.event;

import com.experimentlab.assignment.Assignment;
import com.experimentlab.variant.Variant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventGenerationServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventGenerationService eventGenerationService;

    private Assignment assignment;
    private Variant variant;

    @BeforeEach
    void setUp() {
        assignment = mock(Assignment.class);
        variant = mock(Variant.class);

        when(assignment.getVariant()).thenReturn(variant);
    }

    @Test
    void everyAssignmentReceivesAnExposureEvent() {
        configureProbabilities(0.0, 0.0);

        List<Event> events =
                runGeneration(List.of(assignment));

        assertEquals(1, events.size());
        assertEquals(EventType.EXPOSURE, events.get(0).getEventType());
        assertSame(assignment, events.get(0).getAssignment());
    }

    @Test
    void generatesClickWhenClickProbabilityIs100Percent() {
        configureProbabilities(1.0, 0.0);

        List<Event> events =
                runGeneration(List.of(assignment));

        assertEquals(2, events.size());
        assertEquals(1, countEvents(events, EventType.EXPOSURE));
        assertEquals(1, countEvents(events, EventType.CLICK));
        assertEquals(0, countEvents(events, EventType.CONVERSION));
    }

    @Test
    void generatesConversionWhenBothProbabilitiesAre100Percent() {
        configureProbabilities(1.0, 1.0);

        List<Event> events =
                runGeneration(List.of(assignment));

        assertEquals(3, events.size());
        assertEquals(1, countEvents(events, EventType.EXPOSURE));
        assertEquals(1, countEvents(events, EventType.CLICK));
        assertEquals(1, countEvents(events, EventType.CONVERSION));
    }

    @Test
    void doesNotGenerateConversionWhenClickProbabilityIsZero() {
        configureProbabilities(0.0, 1.0);

        List<Event> events =
                runGeneration(List.of(assignment));

        assertEquals(1, events.size());
        assertEquals(0, countEvents(events, EventType.CLICK));
        assertEquals(0, countEvents(events, EventType.CONVERSION));
    }

    @Test
    void rejectsProbabilityGreaterThanOne() {
        configureProbabilities(1.5, 0.2);

        assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> eventGenerationService.generateEvents(
                        List.of(assignment)
                )
        );

        verify(eventRepository, never()).saveAll(anyList());
    }

    @Test
    void rejectsNegativeProbability() {
        configureProbabilities(-0.1, 0.2);

        assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> eventGenerationService.generateEvents(
                        List.of(assignment)
                )
        );

        verify(eventRepository, never()).saveAll(anyList());
    }

    @Test
    void persistsGeneratedEvents() {
        configureProbabilities(1.0, 1.0);

        List<Event> events =
                runGeneration(List.of(assignment));

        verify(eventRepository).saveAll(events);
        assertEquals(3, events.size());
    }

    @Test
    void usesDefaultProbabilitiesWhenSimulationConfigIsMissing() {
        when(variant.getConfiguration()).thenReturn(new HashMap<>());

        List<Event> events =
                runGeneration(List.of(assignment));

        assertTrue(events.stream()
                .anyMatch(event -> event.getEventType() == EventType.EXPOSURE));

        verify(eventRepository).saveAll(events);
    }

    private List<Event> runGeneration(List<Assignment> assignments) {
        when(eventRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        return eventGenerationService.generateEvents(assignments);
    }

    private void configureProbabilities(
            double clickProbability,
            double conversionProbability
    ) {
        Map<String, Object> simulationConfig = new HashMap<>();
        simulationConfig.put("clickProbability", clickProbability);
        simulationConfig.put("conversionProbability", conversionProbability);

        Map<String, Object> configuration = new HashMap<>();
        configuration.put("simulation", simulationConfig);

        when(variant.getConfiguration()).thenReturn(configuration);
    }

    private long countEvents(List<Event> events, EventType eventType) {
        return events.stream()
                .filter(event -> event.getEventType() == eventType)
                .count();
    }
}

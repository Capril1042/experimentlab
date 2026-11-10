
package com.experimentlab.event;

import com.experimentlab.assignment.Assignment;
import com.experimentlab.variant.Variant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EventGenerationService {

    private static final double DEFAULT_CLICK_PROBABILITY = 0.40;
    private static final double DEFAULT_CONVERSION_PROBABILITY = 0.20;

    private final EventRepository eventRepository;

    public EventGenerationService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public List<Event> generateEvents(List<Assignment> assignments) {
        List<Event> events = new ArrayList<>();

        for (Assignment assignment : assignments) {
            Variant variant = assignment.getVariant();

            double clickProbability = getProbability(
                    variant,
                    "clickProbability",
                    DEFAULT_CLICK_PROBABILITY
            );

            double conversionProbability = getProbability(
                    variant,
                    "conversionProbability",
                    DEFAULT_CONVERSION_PROBABILITY
            );

            LocalDateTime eventTime = LocalDateTime.now();

            // Every assigned user sees their assigned variant.
            events.add(createEvent(
                    assignment,
                    EventType.EXPOSURE,
                    eventTime
            ));

            // Determine whether the user clicks.
            if (ThreadLocalRandom.current().nextDouble()
                    < clickProbability) {

                events.add(createEvent(
                        assignment,
                        EventType.CLICK,
                        eventTime.plusSeconds(1)
                ));

                // Conversion is conditional on clicking.
                if (ThreadLocalRandom.current().nextDouble()
                        < conversionProbability) {

                    events.add(createEvent(
                            assignment,
                            EventType.CONVERSION,
                            eventTime.plusSeconds(2)
                    ));
                }
            }
        }

        return eventRepository.saveAll(events);
    }

    private Event createEvent(
            Assignment assignment,
            EventType eventType,
            LocalDateTime occurredAt
    ) {
        Event event = new Event();
        event.setAssignment(assignment);
        event.setEventType(eventType);
        event.setOccurredAt(occurredAt);

        return event;
    }

    private double getProbability(
            Variant variant,
            String probabilityName,
            double defaultValue
    ) {
        Map<String, Object> configuration =
                variant.getConfiguration();

        if (configuration == null) {
            return defaultValue;
        }

        Object simulationConfig =
                configuration.get("simulation");

        if (!(simulationConfig instanceof Map<?, ?> simulationMap)) {
            return defaultValue;
        }

        Object configuredValue =
                simulationMap.get(probabilityName);

        if (configuredValue == null) {
            return defaultValue;
        }

        if (!(configuredValue instanceof Number number)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    probabilityName + " must be a number"
            );
        }

        double probability = number.doubleValue();

        if (!Double.isFinite(probability)
                || probability < 0.0
                || probability > 1.0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    probabilityName + " must be between 0 and 1"
            );
        }

        return probability;
    }
}


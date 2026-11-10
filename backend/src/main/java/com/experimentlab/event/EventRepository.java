
package com.experimentlab.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByAssignment_Id(UUID assignmentId);

    long countByAssignment_IdAndEventType(
            UUID assignmentId,
            EventType eventType
    );
}


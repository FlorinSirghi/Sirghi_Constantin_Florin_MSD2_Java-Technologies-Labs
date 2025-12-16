package com.example.Lab10.eventsourcing.repository;

import com.example.Lab10.eventsourcing.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByAggregateIdOrderByVersionAsc(String aggregateId);
    List<Event> findByAggregateIdAndVersionGreaterThanOrderByVersionAsc(String aggregateId, Long version);
}





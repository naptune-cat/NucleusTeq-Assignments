package com.zoya.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zoya.backend.entity.Event;
import com.zoya.backend.entity.User;
import com.zoya.backend.enums.EventStatus;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    // for all the events by organizer
    List<Event> findByOrganizer(User organizer);
    
    // for Active and future events 
    List<Event> findByStatusAndEventDateTimeAfter(
        EventStatus status, LocalDateTime dateTime
    );
    
    // Organizer event by status
    List<Event> findByOrganizerAndStatus(User organizer, EventStatus status);
} 

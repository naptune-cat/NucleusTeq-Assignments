package com.zoya.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.zoya.backend.dto.EventRequestDTO;
import com.zoya.backend.dto.EventResponseDTO;
import com.zoya.backend.entity.Event;
import com.zoya.backend.entity.User;
import com.zoya.backend.enums.EventStatus;
import com.zoya.backend.exception.EventNotFoundException;
import com.zoya.backend.exception.UserNotFoundException;
import com.zoya.backend.repository.EventRepository;
import com.zoya.backend.repository.UserRepository;

import com.zoya.backend.mapper.EventMapper;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    // constructor
    
    public EventService(EventRepository eventRepository, UserRepository userRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
    }
    

    // for creating event using event form data and organizer email
    public EventResponseDTO createEvent(EventRequestDTO request, String organizerEmail) {
        Optional<User> organizerExists = userRepository.findByEmail(organizerEmail);
        
        if (organizerExists.isEmpty()) {
            throw new UserNotFoundException("Organizer not found");
        }

        User organizer = organizerExists.get();
        Event event = new Event();

        //filling all the fields
        event.setEventName(request.getEventName());
        event.setDescription(request.getDescription());
        event.setEventDateTime(request.getEventDateTime());
        event.setVenue(request.getVenue());
        event.setTotalSeats(request.getTotalSeats());
        event.setTicketPrice(request.getTicketPrice());
        event.setCategory(request.getCategory());
        event.setStatus(EventStatus.ACTIVE);

        //initially all tickets are available
        event.setAvailableSeats(request.getTotalSeats());
        // and booked seats are 0 initially
        event.setBookedSeats(0);
        event.setOrganizer(organizer);

        event.setCreatedAt(LocalDateTime.now());

        Event saved = eventRepository.save(event);
        return eventMapper.mapToDTO(saved);
    }

    // get event by organizer 
    public List<EventResponseDTO> getOrganizerEvents(String organizerEmail) {
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        //this will only keep events made by specified organizer 
        List<Event> events = eventRepository.findByOrganizer(organizer);
        List<EventResponseDTO> responseList = new ArrayList<>();

        for (Event event : events) {
            responseList.add(eventMapper.mapToDTO(event));
        }
        return responseList;
    }

    // update event 
    public EventResponseDTO updateEvent(Long id, EventRequestDTO dto, String organizerEmail) {
        Optional<Event> existingEvent = eventRepository.findById(id);
        if (existingEvent.isEmpty()) {
            throw new EventNotFoundException("Event does not exists");
        }
        Event event = existingEvent.get();

        // checking Authorization only the organizer of this event can update
       if (!event.getOrganizer().getEmail().equals(organizerEmail))
           throw new RuntimeException("Unauthorized: You don't own this event");

       //cancellation only valid 4 hours before event time
        
        if (event.getEventDateTime().isBefore(LocalDateTime.now().plusHours(4)))
            throw new RuntimeException("Cannot update event within 4 hours of start time");

        if (dto.getTotalSeats() < event.getBookedSeats())
            throw new RuntimeException(
            "Cannot reduce seats below already booked count: " + event.getBookedSeats());

        eventMapper.updateEntityFromDTO(dto, event);

        return eventMapper.mapToDTO(eventRepository.save(event));
    }

    
    // for cancelling an event
    public String cancelEvent(Long id, String organizerEmail) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getOrganizer().getEmail().equals(organizerEmail))
            throw new RuntimeException("Unauthorized: You don't own this event");

        if (event.getStatus() == EventStatus.CANCELLED)
            throw new RuntimeException("Event is already cancelled");

        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
        return "Event cancelled successfully";
    }

    //statistics for organizer
    public java.util.Map<String, Long> getOrganizerStats(String organizerEmail) {
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        List<Event> allEvents = eventRepository.findByOrganizer(organizer);
        LocalDateTime now = LocalDateTime.now();

        long total = allEvents.size();
        long active = allEvents.stream()
                .filter(e -> e.getStatus() == EventStatus.ACTIVE
                        && e.getEventDateTime().isAfter(now))
                .count();
        long past = allEvents.stream()
                .filter(e -> e.getEventDateTime().isBefore(now)
                        && e.getStatus() != EventStatus.CANCELLED)
                .count();
        long cancelled = allEvents.stream()
                .filter(e -> e.getStatus() == EventStatus.CANCELLED)
                .count();

        return java.util.Map.of(
                "total", total,
                "active", active,
                "past", past,
                "cancelled", cancelled);
    }

    // get event by ID (for both organizer and customer)
    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));
        return eventMapper.mapToDTO(event);
    }

}

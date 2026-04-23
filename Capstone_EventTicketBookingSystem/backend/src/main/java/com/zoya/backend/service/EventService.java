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
}

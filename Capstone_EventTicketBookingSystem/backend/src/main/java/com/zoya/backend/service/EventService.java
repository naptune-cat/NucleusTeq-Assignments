package com.zoya.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zoya.backend.dto.EventRequestDTO;
import com.zoya.backend.dto.EventResponseDTO;
import com.zoya.backend.entity.Booking;
import com.zoya.backend.entity.Event;
import com.zoya.backend.entity.User;
import com.zoya.backend.enums.BookingStatus;
import com.zoya.backend.enums.EventStatus;
import com.zoya.backend.exception.EventNotFoundException;
import com.zoya.backend.exception.UserNotFoundException;
import com.zoya.backend.repository.BookingRepository;
import com.zoya.backend.repository.EventRepository;
import com.zoya.backend.repository.UserRepository;

import com.zoya.backend.mapper.EventMapper;

@Service
public class EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventService
        .class);

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final EventMapper eventMapper;
    private final BookingService bookingService;


    // constructor
    
    public EventService(EventRepository eventRepository, UserRepository userRepository, BookingRepository bookingRepository, EventMapper eventMapper, BookingService bookingService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.eventMapper = eventMapper;
        this.bookingService = bookingService;
    }
    

    // for creating event using event form data and organizer email
   public EventResponseDTO createEvent(EventRequestDTO request, String organizerEmail) {

    logger.info("creating a new event '{}' for organizer: {}", request.getEventName(), organizerEmail);

    // checking if the organizer actually exists in our database
    Optional<User> organizerExists = userRepository.findByEmail(organizerEmail);
    
    if (organizerExists.isEmpty()) {
        logger.error("could not find organizer with email: {}", organizerEmail);
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

    logger.info("successfully saved the new event with id: {}", saved.getId());

    return eventMapper.mapToDTO(saved);
}

    // get event by organizer 
    public List<EventResponseDTO> getOrganizerEvents(String organizerEmail) {

        logger.info("grabbing all events for organizer: {}", organizerEmail);

        // checking if organizer exists first
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> {
                    logger.error("organizer not found: {}", organizerEmail);
                    return new RuntimeException("Organizer not found");
                });

        //this will only keep events made by specified organizer 
        List<Event> events = eventRepository.findByOrganizer(organizer);
        List<EventResponseDTO> responseList = new ArrayList<>();

        for (Event event : events) {
            responseList.add(eventMapper.mapToDTO(event));
        }

        logger.info("Total events fetched for organizer={} : {}", organizerEmail, responseList.size());

        return responseList;
    }

    // update event 
    public EventResponseDTO updateEvent(Long id, EventRequestDTO dto, String organizerEmail) {
        logger.info("Update request for eventId={} by {}", id, organizerEmail);

        Optional<Event> existingEvent = eventRepository.findById(id);
        if (existingEvent.isEmpty()) {
            logger.error("Event not found: {}", id);
            throw new EventNotFoundException("Event does not exists");
        }

        Event event = existingEvent.get();

        // checking Authorization. only the organizer of this event can update
        if (!event.getOrganizer().getEmail().equals(organizerEmail)) {
        logger.warn("someone tried to update an event they dont own: eventId={}, user={}", id, organizerEmail);
        throw new RuntimeException("Unauthorized: You don't own this event");
        }

        //cancellation only valid 4 hours before event time
        
        if (event.getEventDateTime().isBefore(LocalDateTime.now().plusHours(4))) {
            logger.warn("Update not allowed within 4 hours for eventId={}", id);
            throw new RuntimeException("Cannot update event within 4 hours of start time");
        }

        // making sure they dont reduce seats below what is already booked
        if (dto.getTotalSeats() < event.getBookedSeats()) {
            logger.warn("Invalid seat reduction attempt for eventId={}", id);
            throw new RuntimeException(
            "Cannot reduce seats below already booked count: " + event.getBookedSeats());
        }

        eventMapper.updateEntityFromDTO(dto, event);

        Event updated = eventRepository.save(event);

        logger.info("Event updated successfully: eventId={}", id);

        return eventMapper.mapToDTO(updated);
    }

    
    // for cancelling an event
    @org.springframework.transaction.annotation.Transactional
    public String cancelEvent(Long id, String organizerEmail) {
        logger.info("Cancel request for eventId={} by {}", id, organizerEmail);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                logger.error("Event not found: {}", id);
                return new EventNotFoundException("Event not found");
            });

        // security check so others cant cancel it
        if (!event.getOrganizer().getEmail().equals(organizerEmail)) {
            logger.warn("Unauthorized cancel attempt for eventId={} by {}", id, organizerEmail);
            throw new RuntimeException("Unauthorized: You don't own this event");
            }
        
        if (event.getStatus() != EventStatus.CANCELLED) {
            event.setStatus(EventStatus.CANCELLED);
            eventRepository.save(event);
            logger.info("Event status updated to CANCELLED for eventId={}", id);
        } else {
            logger.info("Event {} was already cancelled, ensuring bookings are updated.", id);
            throw new RuntimeException("Event is already cancelled");
        }

        List<Booking> bookings = bookingRepository.findByEvent_Id(id);
        int updatedCount = 0;
        for (Booking booking : bookings) {
            if (booking.getBookingStatus() == BookingStatus.CONFIRMED || booking.getBookingStatus() == BookingStatus.PENDING) {
                booking.setBookingStatus(BookingStatus.CANCELLED_BY_ORGANIZER);
                booking.setCancellationTime(LocalDateTime.now());
                bookingRepository.save(booking);
                updatedCount++;
                logger.info("Booking {} updated to CANCELLED_BY_ORGANIZER", booking.getId());
            }
        }

        logger.info("Event cancellation processed successfully: eventId={}, updatedBookings={}", id, updatedCount);
        return "Event cancelled successfully";
    }

    //statistics for organizer
    public java.util.Map<String, Object> getOrganizerStats(String organizerEmail) {
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
        BigDecimal totalEarned = BigDecimal.ZERO;
        for (Event e : allEvents) {
        List<Booking> bookings = bookingRepository.findByEvent_Id(e.getId());
            for (Booking b : bookings) {
                if (b.getBookingStatus() == BookingStatus.CONFIRMED) {
                    totalEarned = totalEarned.add(b.getTotalAmount());
                }
            }
        }

        return java.util.Map.of(
                "total", total,
                "active", active,
                "past", past,
                "cancelled", cancelled,
                "totalEarned",totalEarned);
    }

    // get event by ID (for both organizer and customer)
    public EventResponseDTO getEventById(Long id) {
        logger.info("fetching event details for id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("event not found for id: {}", id);
                    return new EventNotFoundException("Event not found with id: " + id);
                });
        return eventMapper.mapToDTO(event);
    }
    
        //  event listing for customers. this will be shown at home page
    public List<EventResponseDTO> getAllUpcomingEvents() {
    logger.info("fetching all upcoming events to show on home page");
    // making a list to store all the upcoming events 
    List<Event> upcomingEvents = eventRepository.findByStatusAndEventDateTimeAfter(
            EventStatus.ACTIVE, LocalDateTime.now()
    );

    logger.info("Fetched {} upcoming events for public listing", upcomingEvents.size());

    return upcomingEvents.stream()
            .map(eventMapper::mapToDTO)
            .collect(Collectors.toList());
    }
}
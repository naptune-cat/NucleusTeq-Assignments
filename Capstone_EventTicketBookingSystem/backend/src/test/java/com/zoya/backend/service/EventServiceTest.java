package com.zoya.backend.service;

import com.zoya.backend.dto.EventRequestDTO;
import com.zoya.backend.dto.EventResponseDTO;
import com.zoya.backend.entity.Event;
import com.zoya.backend.entity.User;
import com.zoya.backend.enums.EventStatus;
import com.zoya.backend.enums.UserRole;
import com.zoya.backend.exception.EventNotFoundException;
import com.zoya.backend.mapper.EventMapper;
import com.zoya.backend.repository.EventRepository;
import com.zoya.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @Mock private EventMapper eventMapper;

    @InjectMocks private EventService eventService;

    private User organizer;
    private Event event;
    private EventRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        organizer = new User(1L, LocalDateTime.now(), "Zoya",
                "organizer@gmail.com", "hashed", "9876543210", UserRole.ORGANIZER);

        event = new Event();
        event.setId(1L);
        event.setEventName("Tech Summit");
        event.setDescription("Tech event");
        event.setEventDateTime(LocalDateTime.now().plusDays(10));
        event.setVenue("Bangalore Hall 1");
        event.setTotalSeats(100);
        event.setAvailableSeats(100);
        event.setBookedSeats(0);
        event.setTicketPrice(499.0);
        event.setStatus(EventStatus.ACTIVE);
        event.setOrganizer(organizer);

        requestDTO = new EventRequestDTO();
        requestDTO.setEventName("Tech Summit");
        requestDTO.setDescription("Tech event");
        requestDTO.setEventDateTime(LocalDateTime.now().plusDays(10));
        requestDTO.setVenue("Bangalore Hall 1");
        requestDTO.setTotalSeats(100);
        requestDTO.setTicketPrice(499.0);
    }

    //  Create Event Tests 
    @Test
    void createEvent_Success() {
        when(userRepository.findByEmail("organizer@gmail.com"))
                .thenReturn(Optional.of(organizer));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.mapToDTO(any(Event.class)))
                .thenReturn(new EventResponseDTO());

        EventResponseDTO result = eventService.createEvent(requestDTO, "organizer@gmail.com");

        assertNotNull(result);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void createEvent_OrganizerNotFound_ThrowsException() {
        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> eventService.createEvent(requestDTO, "unknown@gmail.com"));
    }

    //  Get Event By ID Tests 

    @Test
    void getEventById_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.mapToDTO(event)).thenReturn(new EventResponseDTO());

        EventResponseDTO result = eventService.getEventById(1L);

        assertNotNull(result);
    }

    @Test
    void getEventById_NotFound_ThrowsException() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class,
                () -> eventService.getEventById(99L));
    }

    //  Update Event Tests 

    @Test
    void updateEvent_Unauthorized_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(RuntimeException.class,
                () -> eventService.updateEvent(1L, requestDTO, "other@gmail.com"));
    }

    @Test
    void updateEvent_ReduceSeatsBelow_BookedSeats_ThrowsException() {
        event.setBookedSeats(50);
        requestDTO.setTotalSeats(30); // less than booked

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(RuntimeException.class,
                () -> eventService.updateEvent(1L, requestDTO, "organizer@gmail.com"));
    }

    //  Cancel Event Tests 

    @Test
    void cancelEvent_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        String result = eventService.cancelEvent(1L, "organizer@gmail.com");

        assertEquals("Event cancelled successfully", result);
        assertEquals(EventStatus.CANCELLED, event.getStatus());
    }

    @Test
    void cancelEvent_AlreadyCancelled_ThrowsException() {
        event.setStatus(EventStatus.CANCELLED);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(RuntimeException.class,
                () -> eventService.cancelEvent(1L, "organizer@gmail.com"));
    }
}
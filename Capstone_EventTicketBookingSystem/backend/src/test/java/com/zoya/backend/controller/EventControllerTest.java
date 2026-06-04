package com.zoya.backend.controller;

import com.zoya.backend.dto.EventRequestDTO;
import com.zoya.backend.dto.EventResponseDTO;
import com.zoya.backend.service.EventService;
import com.zoya.backend.enums.EventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private EventRequestDTO requestDTO;
    private EventResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new EventRequestDTO();
        requestDTO.setEventName("Tech Summit");
        requestDTO.setDescription("Tech event");
        requestDTO.setEventDateTime(LocalDateTime.now().plusDays(10));
        requestDTO.setVenue("Bangalore Hall");
        requestDTO.setTotalSeats(100);
        requestDTO.setTicketPrice(java.math.BigDecimal.valueOf(500.0));
        requestDTO.setCategory("Technology");

        responseDTO = new EventResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setEventName("Tech Summit");
        responseDTO.setStatus(EventStatus.ACTIVE);
        responseDTO.setOrganizerEmail("organizer@gmail.com");
    }

    // Create Event Tests

    @SuppressWarnings("null")
    @Test
    void createEvent_Success() {
        when(eventService.createEvent(any(EventRequestDTO.class), anyString()))
                .thenReturn(responseDTO);

        ResponseEntity<EventResponseDTO> result = eventController.createEvent(requestDTO, "organizer@gmail.com");

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Tech Summit", result.getBody().getEventName());
        verify(eventService, times(1)).createEvent(any(EventRequestDTO.class), anyString());
    }

    @Test
    void createEvent_ReturnsCorrectStatusCode() {
        when(eventService.createEvent(any(EventRequestDTO.class), anyString()))
                .thenReturn(responseDTO);

        ResponseEntity<EventResponseDTO> result = eventController.createEvent(requestDTO, "organizer@gmail.com");

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    // Get My Events Tests

    @SuppressWarnings("null")
    @Test
    void getMyEvents_Success() {
        List<EventResponseDTO> events = List.of(responseDTO);
        when(eventService.getOrganizerEvents("organizer@gmail.com"))
                .thenReturn(events);

        ResponseEntity<List<EventResponseDTO>> result = eventController.getMyEvents("organizer@gmail.com");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("Tech Summit", result.getBody().get(0).getEventName());
    }

    @SuppressWarnings("null")
    @Test
    void getMyEvents_EmptyList() {
        when(eventService.getOrganizerEvents("organizer@gmail.com"))
                .thenReturn(List.of());

        ResponseEntity<List<EventResponseDTO>> result = eventController.getMyEvents("organizer@gmail.com");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());
    }

    @SuppressWarnings("null")
    @Test
    void getMyEvents_MultipleEvents() {
        EventResponseDTO event2 = new EventResponseDTO();
        event2.setId(2L);
        event2.setEventName("Music Festival");
        event2.setStatus(EventStatus.ACTIVE);

        List<EventResponseDTO> events = List.of(responseDTO, event2);
        when(eventService.getOrganizerEvents("organizer@gmail.com"))
                .thenReturn(events);

        ResponseEntity<List<EventResponseDTO>> result = eventController.getMyEvents("organizer@gmail.com");

        assertEquals(2, result.getBody().size());
        assertEquals("Tech Summit", result.getBody().get(0).getEventName());
        assertEquals("Music Festival", result.getBody().get(1).getEventName());
    }

    // Get Stats Tests

    @SuppressWarnings("null")
    @Test
    void getStats_Success() {
        Map<String, Object> stats = Map.of(
                "total", 5L,
                "active", 3L,
                "past", 1L,
                "cancelled", 1L,
                "totalEarned", 5000.0
        );
        when(eventService.getOrganizerStats("organizer@gmail.com"))
                .thenReturn(stats);

        ResponseEntity<Map<String, Object>> result = eventController.getStats("organizer@gmail.com");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(5L, result.getBody().get("total"));
        assertEquals(3L, result.getBody().get("active"));
        assertEquals(5000.0, result.getBody().get("totalEarned"));
    }

    @SuppressWarnings("null")
    @Test
    void getStats_ZeroEvents() {
        Map<String, Object> stats = Map.of(
                "total", 0L,
                "active", 0L,
                "past", 0L,
                "cancelled", 0L,
                "totalEarned", 0.0
        );
        when(eventService.getOrganizerStats("organizer@gmail.com"))
                .thenReturn(stats);

        ResponseEntity<Map<String, Object>> result = eventController.getStats("organizer@gmail.com");

        assertEquals(0L, result.getBody().get("total"));
        assertEquals(0.0, result.getBody().get("totalEarned"));
    }

    // Get Event By ID Tests

    @SuppressWarnings("null")
    @Test
    void getEvent_Success() {
        when(eventService.getEventById(1L))
                .thenReturn(responseDTO);

        ResponseEntity<EventResponseDTO> result = eventController.getEvent(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Tech Summit", result.getBody().getEventName());
    }

    @Test
    void getEvent_Returns200() {
        when(eventService.getEventById(1L))
                .thenReturn(responseDTO);

        ResponseEntity<EventResponseDTO> result = eventController.getEvent(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    // Update Event Tests

    @Test
    void updateEvent_Success() {
        when(eventService.updateEvent(1L, requestDTO, "organizer@gmail.com"))
                .thenReturn(responseDTO);

        ResponseEntity<EventResponseDTO> result = eventController.updateEvent(1L, requestDTO, "organizer@gmail.com");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(eventService, times(1)).updateEvent(1L, requestDTO, "organizer@gmail.com");
    }

    @Test
    void updateEvent_CallsServiceWithCorrectParams() {
        when(eventService.updateEvent(anyLong(), any(EventRequestDTO.class), anyString()))
                .thenReturn(responseDTO);

        eventController.updateEvent(1L, requestDTO, "organizer@gmail.com");

        verify(eventService).updateEvent(1L, requestDTO, "organizer@gmail.com");
    }

    // Cancel Event Tests

    @Test
    void cancelEvent_Success() {
        String successMessage = "Event cancelled successfully";
        when(eventService.cancelEvent(1L, "organizer@gmail.com"))
                .thenReturn(successMessage);

        ResponseEntity<String> result = eventController.cancelEvent(1L, "organizer@gmail.com");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(successMessage, result.getBody());
    }

    @SuppressWarnings("null")
    @Test
    void cancelEvent_ReturnsCorrectMessage() {
        String message = "Event cancelled successfully";
        when(eventService.cancelEvent(1L, "organizer@gmail.com"))
                .thenReturn(message);

        ResponseEntity<String> result = eventController.cancelEvent(1L, "organizer@gmail.com");

        assertTrue(result.getBody().contains("cancelled"));
    }

    @Test
    void cancelEvent_CallsServiceOnce() {
        when(eventService.cancelEvent(1L, "organizer@gmail.com"))
                .thenReturn("Event cancelled successfully");

        eventController.cancelEvent(1L, "organizer@gmail.com");

        verify(eventService, times(1)).cancelEvent(1L, "organizer@gmail.com");
    }

    // Get All Upcoming Events Tests

    @SuppressWarnings("null")
    @Test
    void getAllUpcomingEvents_Success() {
        List<EventResponseDTO> events = List.of(responseDTO);
        when(eventService.getAllUpcomingEvents())
                .thenReturn(events);

        ResponseEntity<List<EventResponseDTO>> result = eventController.getAllUpcomingEvents();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
    }

    @SuppressWarnings("null")
    @Test
    void getAllUpcomingEvents_EmptyList() {
        when(eventService.getAllUpcomingEvents())
                .thenReturn(List.of());

        ResponseEntity<List<EventResponseDTO>> result = eventController.getAllUpcomingEvents();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());
    }

    @SuppressWarnings("null")
    @Test
    void getAllUpcomingEvents_MultipleEvents() {
        EventResponseDTO event2 = new EventResponseDTO();
        event2.setId(2L);
        event2.setEventName("Sports Day");

        List<EventResponseDTO> events = List.of(responseDTO, event2);
        when(eventService.getAllUpcomingEvents())
                .thenReturn(events);

        ResponseEntity<List<EventResponseDTO>> result = eventController.getAllUpcomingEvents();

        assertEquals(2, result.getBody().size());
        verify(eventService, times(1)).getAllUpcomingEvents();
    }

    @Test
    void getAllUpcomingEvents_NoAuthentication() {
        when(eventService.getAllUpcomingEvents())
                .thenReturn(List.of(responseDTO));

        ResponseEntity<List<EventResponseDTO>> result = eventController.getAllUpcomingEvents();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }
}
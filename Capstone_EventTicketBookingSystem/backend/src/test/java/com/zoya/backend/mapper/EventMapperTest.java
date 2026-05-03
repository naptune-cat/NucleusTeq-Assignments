package com.zoya.backend.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.zoya.backend.dto.EventRequestDTO;
import com.zoya.backend.dto.EventResponseDTO;
import com.zoya.backend.entity.Event;
import com.zoya.backend.entity.User;
import com.zoya.backend.enums.EventStatus;


import com.zoya.backend.mapper.EventMapper;

public class EventMapperTest {
    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        eventMapper = new EventMapper();
    }

    @Test
    void mapToDTO_ShouldReturnEventResponseDTO() {
        // creating mock user
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@gmail.com");

        //creating mock event
        Event event = new Event();

        event.setId(1L);
        event.setEventName("Concert");
        event.setDescription("Test description");
        event.setEventDateTime(java.time.LocalDateTime.of(2027, 7, 19, 7, 0));
        event.setVenue("Stadium");
        event.setCategory("Music");
        event.setTotalSeats(100);
        event.setAvailableSeats(100);
        event.setBookedSeats(0);
        event.setTicketPrice(new java.math.BigDecimal("50.0"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(java.time.LocalDateTime.of(2027,6,15,10,0));
        event.setOrganizer(user);
        // mapping event to response DTO
        EventResponseDTO result = eventMapper.mapToDTO(event);
        // assertions
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Concert", result.getEventName());
        assertEquals("Test description", result.getDescription());
        assertEquals(java.time.LocalDateTime.of(2027,7,19,7,0), result.getEventDateTime());
        assertEquals("Stadium", result.getVenue());
        assertEquals("Music", result.getCategory());
        assertEquals(100, result.getTotalSeats());
        assertEquals(100, result.getAvailableSeats());
        assertEquals(0, result.getBookedSeats());
        assertEquals(new java.math.BigDecimal("50.0"), result.getTicketPrice());
        assertEquals(EventStatus.ACTIVE, result.getStatus());
        assertEquals(java.time.LocalDateTime.of(2027,6,15,10,0), result.getCreatedAt());
        assertEquals("John Doe", result.getOrganizerName());
        assertEquals("john@gmail.com", result.getOrganizerEmail());


    }

    @Test
    void mapToEntity_ShouldReturnEvent() {
        // creating mock user
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@gmail.com");
        // creating event request DTO
        EventRequestDTO dto = new EventRequestDTO();
        dto.setEventName("Concert");
        dto.setDescription("Test description");
        dto.setEventDateTime(java.time.LocalDateTime.of(2027, 7, 19, 7, 0));
        dto.setVenue("Stadium");
        dto.setCategory("Music");
        dto.setTotalSeats(100);
        dto.setTicketPrice(new java.math.BigDecimal("50.0"));
        // mapping request DTO to event entity
        Event result = eventMapper.mapToEntity(dto, user);
        // assertions
        assertNotNull(result);
        assertEquals("Concert", result.getEventName());
        assertEquals("Test description", result.getDescription());
        assertEquals(java.time.LocalDateTime.of(2027, 7, 19, 7,
                0), result.getEventDateTime());
        assertEquals("Stadium", result.getVenue());
        assertEquals("Music", result.getCategory());
        assertEquals(100, result.getTotalSeats());
        assertEquals(100, result.getAvailableSeats()); // should be same as total seats
        assertEquals(0, result.getBookedSeats()); // should be 0 initially
        assertEquals(new java.math.BigDecimal("50.0"), result.getTicketPrice());
        assertEquals(EventStatus.ACTIVE, result.getStatus()); // should be ACTIVE initially
        assertEquals(user, result.getOrganizer());
    }
}

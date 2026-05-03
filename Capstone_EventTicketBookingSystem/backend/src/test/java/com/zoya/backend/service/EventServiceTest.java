package com.zoya.backend.service;

import com.zoya.backend.dto.EventRequestDTO;
import com.zoya.backend.dto.EventResponseDTO;
import com.zoya.backend.entity.Booking;
import com.zoya.backend.entity.Event;
import com.zoya.backend.entity.User;
import com.zoya.backend.enums.BookingStatus;
import com.zoya.backend.enums.EventStatus;
import com.zoya.backend.exception.EventNotFoundException;
import com.zoya.backend.exception.UserNotFoundException;
import com.zoya.backend.mapper.EventMapper;
import com.zoya.backend.repository.BookingRepository;
import com.zoya.backend.repository.EventRepository;
import com.zoya.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    private User organizer;
    private Event event;
    private EventRequestDTO requestDTO;
    private EventResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        organizer = new User();
        organizer.setId(1L);
        organizer.setEmail("organizer@test.com");
        organizer.setName("Organizer");

        event = new Event();
        event.setId(1L);
        event.setEventName("Rock Concert");
        event.setDescription("Great event");
        event.setVenue("Mumbai Arena");
        event.setEventDateTime(LocalDateTime.now().plusDays(10));
        event.setTotalSeats(100);
        event.setAvailableSeats(100);
        event.setBookedSeats(0);
        event.setTicketPrice(BigDecimal.valueOf(500));
        event.setStatus(EventStatus.ACTIVE);
        event.setOrganizer(organizer);

        requestDTO = new EventRequestDTO();
        requestDTO.setEventName("Rock Concert");
        requestDTO.setDescription("Great event");
        requestDTO.setVenue("Mumbai Arena");
        requestDTO.setEventDateTime(LocalDateTime.now().plusDays(10));
        requestDTO.setTotalSeats(100);
        requestDTO.setTicketPrice(BigDecimal.valueOf(500));

        responseDTO = new EventResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setEventName("Rock Concert");
    }

    // test for createEvent to ensure it creates event successfully when organizer exists

    @Test
    void createEvent_success_whenOrganizerExists() {
        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.mapToDTO(event)).thenReturn(responseDTO);

        EventResponseDTO result = eventService.createEvent(requestDTO, "organizer@test.com");

        assertThat(result).isNotNull();
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void createEvent_setsStatusToActive() {
        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));
        when(eventMapper.mapToDTO(any())).thenReturn(responseDTO);

        eventService.createEvent(requestDTO, "organizer@test.com");

        verify(eventRepository).save(argThat(e -> e.getStatus() == EventStatus.ACTIVE));
    }

    @Test
    void createEvent_setsAvailableSeatsEqualToTotalSeats() {
        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));
        when(eventMapper.mapToDTO(any())).thenReturn(responseDTO);

        eventService.createEvent(requestDTO, "organizer@test.com");

        verify(eventRepository).save(argThat(e -> e.getAvailableSeats() == requestDTO.getTotalSeats()));
    }

    @Test
    void createEvent_setsBookedSeatsToZero() {
        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));
        when(eventMapper.mapToDTO(any())).thenReturn(responseDTO);

        eventService.createEvent(requestDTO, "organizer@test.com");

        verify(eventRepository).save(argThat(e -> e.getBookedSeats() == 0));
    }

    @Test
    void createEvent_throwsUserNotFoundException_whenOrganizerNotFound() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.createEvent(requestDTO, "missing@test.com"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Organizer not found");
    }

    // test for getOrganizerEvents to ensure it returns list of events for given organizer email
    @Test
    void getOrganizerEvents_returnsEventList_whenOrganizerExists() {
        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventRepository.findByOrganizer(organizer)).thenReturn(Arrays.asList(event));
        when(eventMapper.mapToDTO(event)).thenReturn(responseDTO);

        List<EventResponseDTO> result = eventService.getOrganizerEvents("organizer@test.com");

        assertThat(result).hasSize(1);
    }

    @Test
    void getOrganizerEvents_returnsEmptyList_whenNoEvents() {
        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventRepository.findByOrganizer(organizer)).thenReturn(Collections.emptyList());

        List<EventResponseDTO> result = eventService.getOrganizerEvents("organizer@test.com");

        assertThat(result).isEmpty();
    }

    @Test
    void getOrganizerEvents_throwsRuntimeException_whenOrganizerNotFound() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getOrganizerEvents("missing@test.com"))
                .isInstanceOf(RuntimeException.class);
    }

    // test for updateEvent

    @Test
    void updateEvent_success_whenValidRequest() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.mapToDTO(event)).thenReturn(responseDTO);

        EventResponseDTO result = eventService.updateEvent(1L, requestDTO, "organizer@test.com");

        assertThat(result).isNotNull();
        verify(eventRepository).save(any(Event.class));
    }

    // edge case: updating non-existent event should throw EventNotFoundException
    @Test
    void updateEvent_throwsEventNotFoundException_whenEventNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateEvent(99L, requestDTO, "organizer@test.com"))
                .isInstanceOf(EventNotFoundException.class);
    }

    // edge case: updating event by non-owner should throw exception
    @Test
    void updateEvent_throwsRuntimeException_whenNotOwner() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.updateEvent(1L, requestDTO, "other@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }

    // edge case: updating event within 4 hours of event date should throw exception
    @Test
    void updateEvent_throwsRuntimeException_whenWithin4HoursOfEvent() {
        event.setEventDateTime(LocalDateTime.now().plusHours(2));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.updateEvent(1L, requestDTO, "organizer@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot update event within 4 hours");
    }
    // edge case: reducing total seats below already booked count should throw exception
    @Test
    void updateEvent_throwsRuntimeException_whenReducingSeatsBelowBookedCount() {
        event.setBookedSeats(50);
        requestDTO.setTotalSeats(30);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.updateEvent(1L, requestDTO, "organizer@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot reduce seats below already booked count");
    }

    // tests for cancelEvent to ensure it sets status to CANCELLED and handles edge cases like already cancelled or not owner
    @Test
    void cancelEvent_success_whenValidRequest() {
        Booking confirmedBooking = new Booking();
        confirmedBooking.setId(10L);
        confirmedBooking.setBookingStatus(BookingStatus.CONFIRMED);

        Booking pendingBooking = new Booking();
        pendingBooking.setId(11L);
        pendingBooking.setBookingStatus(BookingStatus.PENDING);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(bookingRepository.findByEvent_Id(1L)).thenReturn(Arrays.asList(confirmedBooking, pendingBooking));

        String result = eventService.cancelEvent(1L, "organizer@test.com");

        assertThat(result).isEqualTo("Event cancelled successfully");
        verify(eventRepository).save(argThat(e -> e.getStatus() == EventStatus.CANCELLED));
        verify(bookingRepository, times(2)).save(any(Booking.class));
        assertThat(confirmedBooking.getBookingStatus()).isEqualTo(BookingStatus.CANCELLED_BY_ORGANIZER);
        assertThat(pendingBooking.getBookingStatus()).isEqualTo(BookingStatus.CANCELLED_BY_ORGANIZER);
        assertThat(confirmedBooking.getCancellationTime()).isNotNull();
        assertThat(pendingBooking.getCancellationTime()).isNotNull();
    }

    @Test
    void cancelEvent_throwsEventNotFoundException_whenNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.cancelEvent(99L, "organizer@test.com"))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void cancelEvent_throwsRuntimeException_whenNotOwner() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.cancelEvent(1L, "hacker@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void cancelEvent_throwsRuntimeException_whenAlreadyCancelled() {
        event.setStatus(EventStatus.CANCELLED);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.cancelEvent(1L, "organizer@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already cancelled");
    }

    // tests for getEventById to ensure it returns event details correctly and handles not found case

    @Test
    void getEventById_returnsEvent_whenFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.mapToDTO(event)).thenReturn(responseDTO);

        EventResponseDTO result = eventService.getEventById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    // test edge case: event exists but is cancelled, should still return event details
    @Test
    void getEventById_throwsEventNotFoundException_whenNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(99L))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Event not found with id: 99");
    }

    // test for getAllUpcomingEvents to ensure it only returns active events with future dates

    @Test
    void getAllUpcomingEvents_returnsActiveUpcomingEvents() {
        when(eventRepository.findByStatusAndEventDateTimeAfter(eq(EventStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(event));
        when(eventMapper.mapToDTO(event)).thenReturn(responseDTO);

        List<EventResponseDTO> result = eventService.getAllUpcomingEvents();

        assertThat(result).hasSize(1);
    }

    // test edge case: no upcoming events should return empty list
    @Test
    void getAllUpcomingEvents_returnsEmptyList_whenNoUpcomingEvents() {
        when(eventRepository.findByStatusAndEventDateTimeAfter(eq(EventStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        List<EventResponseDTO> result = eventService.getAllUpcomingEvents();

        assertThat(result).isEmpty();
    }

    // test for checking if getOrganizerStats returns correct counts for total, active, past, cancelled events
    @Test
    void getOrganizerStats_throwsRuntimeException_whenOrganizerNotFound() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getOrganizerStats("missing@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Organizer not found");
    }

    // edge case: organizer with no events should return zero counts
    @Test
    void getOrganizerStats_returnsCorrectCounts_withMixedEvents() {
        Event activeEvent = buildEvent(2L, EventStatus.ACTIVE, LocalDateTime.now().plusDays(5));
        Event pastEvent   = buildEvent(3L, EventStatus.ACTIVE, LocalDateTime.now().minusDays(2));
        Event cancelledEvent = buildEvent(4L, EventStatus.CANCELLED, LocalDateTime.now().plusDays(1));

        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventRepository.findByOrganizer(organizer))
                .thenReturn(Arrays.asList(activeEvent, pastEvent, cancelledEvent));
        when(bookingRepository.findByEvent_Id(anyLong())).thenReturn(Collections.emptyList());

        Map<String, Object> stats = eventService.getOrganizerStats("organizer@test.com");

        assertThat(stats.get("total")).isEqualTo(3L);
        assertThat(stats.get("active")).isEqualTo(1L);
        assertThat(stats.get("past")).isEqualTo(1L);
        assertThat(stats.get("cancelled")).isEqualTo(1L);
    }

    // edge case: only confirmed bookings should contribute to total earned
    @Test
    void getOrganizerStats_calculatesTotalEarned_onlyFromConfirmedBookings() {
        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventRepository.findByOrganizer(organizer)).thenReturn(Arrays.asList(event));

        Booking confirmed = new Booking();
        confirmed.setBookingStatus(BookingStatus.CONFIRMED);
        confirmed.setTotalAmount(BigDecimal.valueOf(1000));

        Booking cancelled = new Booking();
        cancelled.setBookingStatus(BookingStatus.CANCELLED);
        cancelled.setTotalAmount(BigDecimal.valueOf(500));

        when(bookingRepository.findByEvent_Id(1L)).thenReturn(Arrays.asList(confirmed, cancelled));

        Map<String, Object> stats = eventService.getOrganizerStats("organizer@test.com");

        assertThat(stats.get("totalEarned")).isEqualTo(BigDecimal.valueOf(1000));
    }

    // edge case: no bookings at all, should return zero earned

    @Test
    void getOrganizerStats_returnsZeroEarned_whenNoConfirmedBookings() {
        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventRepository.findByOrganizer(organizer)).thenReturn(Arrays.asList(event));
        when(bookingRepository.findByEvent_Id(1L)).thenReturn(Collections.emptyList());

        Map<String, Object> stats = eventService.getOrganizerStats("organizer@test.com");

        assertThat(stats.get("totalEarned")).isEqualTo(BigDecimal.ZERO);
    }

    // helper method to build events with different statuses and dates 

    private Event buildEvent(Long id, EventStatus status, LocalDateTime dateTime) {
        Event e = new Event();
        e.setId(id);
        e.setStatus(status);
        e.setEventDateTime(dateTime);
        e.setOrganizer(organizer);
        return e;
    }
}
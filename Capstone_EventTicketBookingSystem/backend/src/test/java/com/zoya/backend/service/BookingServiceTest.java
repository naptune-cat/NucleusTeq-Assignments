package com.zoya.backend.service;

import com.zoya.backend.dto.BookingRequestDTO;
import com.zoya.backend.dto.BookingResponseDTO;
import com.zoya.backend.dto.PaymentRequestDTO;
import com.zoya.backend.dto.PaymentResponseDTO;
import com.zoya.backend.entity.*;
import com.zoya.backend.enums.*;
import com.zoya.backend.exception.*;
import com.zoya.backend.mapper.BookingMapper;
import com.zoya.backend.repository.*;
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
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookingMapper bookingMapper;

    @InjectMocks private BookingService bookingService;

    private User customer;
    private User organizer;
    private Event event;
    private Booking booking;
    private BookingRequestDTO bookingRequest;

    @BeforeEach
    void setUp() {
        organizer = new User(1L, LocalDateTime.now(), "Organizer",
                "organizer@gmail.com", "hashed", "9876543210", UserRole.ORGANIZER);

        customer = new User(2L, LocalDateTime.now(), "Customer",
                "customer@gmail.com", "hashed", "9123456780", UserRole.CUSTOMER);

        event = new Event();
        event.setId(1L);
        event.setEventName("Tech Summit");
        event.setEventDateTime(LocalDateTime.now().plusDays(5));
        event.setTotalSeats(100);
        event.setAvailableSeats(50);
        event.setBookedSeats(50);
        event.setTicketPrice(500.0);
        event.setStatus(EventStatus.ACTIVE);
        event.setOrganizer(organizer);

        booking = new Booking();
        booking.setId(1L);
        booking.setUser(customer);
        booking.setEvent(event);
        booking.setNumberOfTickets(2);
        booking.setTotalAmount(1000.0);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setBookingTime(LocalDateTime.now());

        bookingRequest = new BookingRequestDTO();
        bookingRequest.setEventId(1L);
        bookingRequest.setNumberOfTickets(2);

        // lenient because not every test needs event fetch
        lenient().when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));
        lenient().when(eventRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(event));
    }

    // Create Booking Tests

    @Test
    void createBooking_Success() {
        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));
        when(bookingRepository.existsByUserAndEventIdAndBookingStatus(
                customer, 1L, BookingStatus.CONFIRMED)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.mapToBookingResponse(booking))
                .thenReturn(new BookingResponseDTO());

        BookingResponseDTO result = bookingService.createBooking("customer@gmail.com", bookingRequest);

        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_ExceedsAvailableSeats_ThrowsException() {
        // trying to book more tickets than available seats
        bookingRequest.setNumberOfTickets(100);

        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));

        assertThrows(InvalidBookingException.class,
                () -> bookingService.createBooking("customer@gmail.com", bookingRequest));
    }

    @Test
    void createBooking_PastEvent_ThrowsException() {
        // setting event date to past to simulate expired event booking attempt
        event.setEventDateTime(LocalDateTime.now().minusDays(1));

        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));

        assertThrows(InvalidBookingException.class,
                () -> bookingService.createBooking("customer@gmail.com", bookingRequest));
    }

    @Test
    void createBooking_CancelledEvent_ThrowsException() {
        // cancelled event should not allow new bookings
        event.setStatus(EventStatus.CANCELLED);

        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));

        assertThrows(InvalidBookingException.class,
                () -> bookingService.createBooking("customer@gmail.com", bookingRequest));
    }

    @Test
    void createBooking_AlreadyBooked_ThrowsException() {
        // user already has a confirmed booking for this event
        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));
        when(bookingRepository.existsByUserAndEventIdAndBookingStatus(
                customer, 1L, BookingStatus.CONFIRMED)).thenReturn(true);

        assertThrows(InvalidBookingException.class,
                () -> bookingService.createBooking("customer@gmail.com", bookingRequest));
    }

    // Payment Tests

    @Test
    void processPayment_Success() {
        PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
        paymentRequest.setBookingId(1L);
        paymentRequest.setPaymentMethod("CARD");

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setBooking(booking);
        payment.setAmount(1000.0);
        payment.setPaymentStatus(PaymentStatus.SUCCESSFUL);
        payment.setTransactionId("txn-123");
        payment.setPaymentTime(LocalDateTime.now());

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(bookingMapper.mapToPaymentResponse(payment))
                .thenReturn(new PaymentResponseDTO());

        PaymentResponseDTO result = bookingService.processPayment(
                "customer@gmail.com", paymentRequest);

        assertNotNull(result);
        assertEquals(BookingStatus.CONFIRMED, booking.getBookingStatus());
    }

    @Test
    void processPayment_UnauthorizedUser_ThrowsException() {
        // only the user who created the booking can pay for it
        PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
        paymentRequest.setBookingId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(InvalidBookingException.class,
                () -> bookingService.processPayment("other@gmail.com", paymentRequest));
    }

    // Cancel Booking Tests

    @Test
    void cancelBooking_Success() {
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.mapToBookingResponse(booking))
                .thenReturn(new BookingResponseDTO());

        BookingResponseDTO result = bookingService.cancelBooking("customer@gmail.com", 1L);

        assertNotNull(result);
        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
    }

    @Test
    void cancelBooking_Within3Hours_ThrowsException() {
        // cancellation not allowed within 3 hours of event start
        event.setEventDateTime(LocalDateTime.now().plusHours(1));
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(InvalidBookingException.class,
                () -> bookingService.cancelBooking("customer@gmail.com", 1L));
    }

    @Test
    void cancelBooking_PastEvent_ThrowsException() {
        // cannot cancel booking for an event that has already passed
        event.setEventDateTime(LocalDateTime.now().minusDays(1));
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(InvalidBookingException.class,
                () -> bookingService.cancelBooking("customer@gmail.com", 1L));
    }

    @Test
    void cancelBooking_AlreadyCancelled_ThrowsException() {
        // booking already cancelled should not be cancelled again
        booking.setBookingStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(InvalidBookingException.class,
                () -> bookingService.cancelBooking("customer@gmail.com", 1L));
    }

    @Test
    void cancelBooking_Unauthorized_ThrowsException() {
        // only the customer who booked can cancel their booking
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(InvalidBookingException.class,
                () -> bookingService.cancelBooking("other@gmail.com", 1L));
    }
}
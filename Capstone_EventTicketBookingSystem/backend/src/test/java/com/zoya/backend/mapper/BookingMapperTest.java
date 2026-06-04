package com.zoya.backend.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.zoya.backend.dto.BookingResponseDTO;
import com.zoya.backend.dto.PaymentResponseDTO;
import com.zoya.backend.entity.Booking;
import com.zoya.backend.entity.Event;
import com.zoya.backend.entity.Payment;
import com.zoya.backend.entity.User;
import com.zoya.backend.enums.BookingStatus;
import com.zoya.backend.enums.PaymentStatus;

class BookingMapperTest {
    private BookingMapper bookingMapper;
    
    @BeforeEach
    void setUp() {
        bookingMapper = new BookingMapper();
    }

    @Test
    void mapToBookingResponse_ShouldReturnBookingResponseDTO() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@gmail.com");
        
        Event event = new Event();
        event.setId(1L);
        event.setEventName("Concert");
        event.setVenue("Stadium");
        event.setEventDateTime(java.time.LocalDateTime.of(2028, 7, 20, 19, 0));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setEvent(event);
        booking.setUser(user);
        booking.setNumberOfTickets(2);
        booking.setTotalAmount(new java.math.BigDecimal("100.0"));
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setBookingTime(java.time.LocalDateTime.of(2028, 6, 15, 10, 0));
        booking.setCancellationTime(null);

        BookingResponseDTO result = bookingMapper.mapToBookingResponse(booking);

        assertNotNull(result);
        assertEquals(1L, result.getBookingId());
        assertEquals(1L, result.getEventId());
        assertEquals("Concert", result.getEventName());
        assertEquals("Stadium", result.getVenue());
        assertEquals(java.time.LocalDateTime.of(2028, 7, 20, 19, 0), result.getEventDateTime());
        assertEquals(2, result.getNumberOfTickets());
        assertEquals(new java.math.BigDecimal("100.0"), result.getTotalAmount());
        assertEquals(BookingStatus.CONFIRMED, result.getBookingStatus());
        assertEquals(java.time.LocalDateTime.of(2028, 6, 15, 10, 0), result.getBookingTime());
        assertNull(result.getCancellationTime());
        assertEquals("John Doe", result.getUserName());
        assertEquals("john@gmail.com", result.getUserEmail());
    }

    @Test
    void mapToPaymentResponse_ShouldReturnPaymentResponseDTO() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@gmail.com");
        
        Event event = new Event();
        event.setId(1L);
        event.setEventName("Concert");
        event.setVenue("Stadium");
        event.setEventDateTime(java.time.LocalDateTime.of(2028, 7, 20, 19, 0));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setEvent(event);
        booking.setUser(user);
        booking.setNumberOfTickets(2);
        booking.setTotalAmount(new java.math.BigDecimal("100.0"));
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setBookingTime(java.time.LocalDateTime.of(2028, 6, 15, 10, 0));

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setBooking(booking);
        payment.setAmount(new java.math.BigDecimal("100"));
        payment.setPaymentStatus(PaymentStatus.SUCCESSFUL);
        payment.setPaymentMethod("CREDIT_CARD");
        payment.setTransactionId("TXN12345");
        payment.setPaymentTime(java.time.LocalDateTime.of(2028, 6, 15, 10, 0));

        PaymentResponseDTO result = bookingMapper.mapToPaymentResponse(payment);

        assertNotNull(result);
        assertEquals(1L, result.getPaymentId());
        assertEquals(1L, result.getBookingId());
        assertEquals(new java.math.BigDecimal("100"), result.getAmount());
        assertEquals(PaymentStatus.SUCCESSFUL, result.getPaymentStatus());
        assertEquals("CREDIT_CARD", result.getPaymentMethod());
        assertEquals("TXN12345", result.getTransactionId());
        assertEquals(java.time.LocalDateTime.of(2028, 6, 15, 10, 0), result.getPaymentTime());
    }
}
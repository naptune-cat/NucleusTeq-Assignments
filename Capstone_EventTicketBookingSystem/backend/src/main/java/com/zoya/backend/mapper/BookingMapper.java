package com.zoya.backend.mapper;

import org.springframework.stereotype.Component;

import com.zoya.backend.dto.BookingResponseDTO;
import com.zoya.backend.dto.PaymentResponseDTO;
import com.zoya.backend.entity.Booking;
import com.zoya.backend.entity.Payment;

@Component
public class BookingMapper {

    // Booking entity -> ResponseDTO mapping
    public BookingResponseDTO mapToBookingResponse(Booking booking) {

        BookingResponseDTO dto = new BookingResponseDTO();

        dto.setBookingId(booking.getId());
        dto.setEventId(booking.getEvent().getId());
        dto.setEventName(booking.getEvent().getEventName());
        dto.setVenue(booking.getEvent().getVenue());
        dto.setEventDateTime(booking.getEvent().getEventDateTime());
        dto.setNumberOfTickets(booking.getNumberOfTickets());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setBookingStatus(booking.getBookingStatus());
        dto.setBookingTime(booking.getBookingTime());
        dto.setCancellationTime(booking.getCancellationTime());
        return dto;
    }
    
    // payment entity -> responseDTO mapping
    public PaymentResponseDTO mapToPaymentResponse(Payment payment) {

        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setPaymentId(payment.getId());
        dto.setBookingId(payment.getBooking().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentTime(payment.getPaymentTime());
        return dto;
    }
}

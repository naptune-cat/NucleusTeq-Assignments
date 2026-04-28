package com.zoya.backend.controller;

import com.zoya.backend.dto.*;
import com.zoya.backend.service.BookingService;
import com.zoya.backend.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ExportService exportService;

    public BookingController(BookingService bookingService, ExportService exportService) {
        this.bookingService = bookingService;
        this.exportService = exportService;
    }

    // Customer creates a booking which is by default PENDING
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(
            @RequestAttribute("userEmail") String userEmail,
            @Valid @RequestBody BookingRequestDTO request) {

        BookingResponseDTO response = bookingService.createBooking(userEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //  Customer completes mock payment after this method our ticket status will turn to CONFIRMED
    @PostMapping("/payment")
    public ResponseEntity<PaymentResponseDTO> processPayment(
            @RequestAttribute("userEmail") String userEmail,
            @Valid @RequestBody PaymentRequestDTO request) {

        PaymentResponseDTO response = bookingService.processPayment(userEmail, request);
        return ResponseEntity.ok(response);
    }

    // Customer cancels booking
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDTO> cancelBooking(
            @RequestAttribute("userEmail") String userEmail,
            @PathVariable Long id) {

        BookingResponseDTO response = bookingService.cancelBooking(userEmail, id);
        return ResponseEntity.ok(response);
    }

    // Customer can view their booking history
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings(
            @RequestAttribute("userEmail") String userEmail) {

        return ResponseEntity.ok(bookingService.getMyBookings(userEmail));
    }

    // Organizer can view bookings for their event
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsForEvent(
            @RequestAttribute("userEmail") String userEmail,
            @PathVariable Long eventId) {

        return ResponseEntity.ok(bookingService.getBookingsForEvent(userEmail, eventId));
    }

    // Organizer will download attendees list
    @GetMapping("/event/{eventId}/export")
    public void exportAttendees(
            @RequestAttribute("userEmail") String userEmail,
            @PathVariable Long eventId,
            HttpServletResponse response) throws IOException {
        
        List<BookingResponseDTO> attendees = bookingService.getBookingsForEvent(userEmail, eventId);
        
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"attendees_event_" + eventId + ".csv\"");
        
        exportService.exportAttendeesToCsv(response.getWriter(), attendees);
    }
}
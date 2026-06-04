package com.zoya.backend.controller;

import com.zoya.backend.dto.*;
import com.zoya.backend.service.BookingService;
import com.zoya.backend.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

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
        
        logger.info("got a request to create a booking for user: {}", userEmail);
        BookingResponseDTO response = bookingService.createBooking(userEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //  Customer completes mock payment after this method our ticket status will turn to CONFIRMED
    @PostMapping("/payment")
    public ResponseEntity<PaymentResponseDTO> processPayment(
            @RequestAttribute("userEmail") String userEmail,
            @Valid @RequestBody PaymentRequestDTO request) {
        
        logger.info("got a payment request from user: {}", userEmail);
        PaymentResponseDTO response = bookingService.processPayment(userEmail, request);
        return ResponseEntity.ok(response);
    }

    // Customer cancels booking
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDTO> cancelBooking(
            @RequestAttribute("userEmail") String userEmail,
            @PathVariable Long id) {
        
        logger.info("user {} wants to cancel their booking {}", userEmail, id);
        BookingResponseDTO response = bookingService.cancelBooking(userEmail, id);
        return ResponseEntity.ok(response);
    }

    // Customer can view their booking history
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings(
            @RequestAttribute("userEmail") String userEmail) {
        
        logger.info("user {} is checking their booking history", userEmail);
        return ResponseEntity.ok(bookingService.getMyBookings(userEmail));
    }

    // Organizer can view bookings for their event
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsForEvent(
            @RequestAttribute("userEmail") String userEmail,
            @PathVariable Long eventId) {
        
        logger.info("organizer {} is checking bookings for their event {}", userEmail, eventId);
        return ResponseEntity.ok(bookingService.getBookingsForEvent(userEmail, eventId));
    }

  
}
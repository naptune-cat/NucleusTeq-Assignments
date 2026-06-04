package com.zoya.backend.service;

import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zoya.backend.dto.BookingResponseDTO;

@Service
public class ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);

    private final BookingService bookingService;

    // constructor injection
    public ExportService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // exportEventAttendees handles both fetching and exporting
    public void exportEventAttendees(String email, Long eventId, PrintWriter writer) {
        logger.info("Exporting attendees for event {}", eventId);

        List<BookingResponseDTO> attendees =
                bookingService.getBookingsForEvent(email, eventId);

        exportAttendeesToCsv(writer, attendees);
    }

    // CSV writing 
    public void exportAttendeesToCsv(PrintWriter writer, List<BookingResponseDTO> attendees) {
        logger.info("Starting CSV generation");

        // header
        writer.println("Booking ID,Customer Name,Customer Email,Tickets Booked,Status,Booking Time");

        // data rows
        for (BookingResponseDTO attendee : attendees) {
            writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                    attendee.getBookingId(),
                    attendee.getUserName(),
                    attendee.getUserEmail(),
                    attendee.getNumberOfTickets(),
                    attendee.getBookingStatus(),
                    attendee.getBookingTime()
            ));
        }

        writer.flush(); // used to clear any buffered data 
        logger.info("CSV export completed");
    }
}
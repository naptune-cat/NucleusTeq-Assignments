package com.zoya.backend.service;

import java.io.PrintWriter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zoya.backend.dto.BookingResponseDTO;

@Service
public class ExportService {

    public void exportAttendeesToCsv(PrintWriter writer, List<BookingResponseDTO> attendees) {
        // Header 
        writer.println("Booking ID,Customer Name,Customer Email,Tickets Booked,Status,Booking Time");

        // Write Data
        for (BookingResponseDTO attendee : attendees) {
            writer.println(String.format("%s,%s,%s,%s,%s,%s",
                    attendee.getBookingId(),
                    attendee.getUserName(),
                    attendee.getUserEmail(),
                    attendee.getNumberOfTickets(),
                    attendee.getBookingStatus(),
                    attendee.getBookingTime()
            ));
        }
    }
}

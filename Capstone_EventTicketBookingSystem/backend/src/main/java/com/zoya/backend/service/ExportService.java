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

    public void exportAttendeesToCsv(PrintWriter writer, List<BookingResponseDTO> attendees) {
        logger.info("starting to export attendees to csv file");
        // writing the header row first
        writer.println("Booking ID,Customer Name,Customer Email,Tickets Booked,Status,Booking Time");

        // writing the actual data row by row
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
        logger.info("finished writing all attendees to the csv file");
    }
}

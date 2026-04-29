package com.zoya.backend.controller;

import com.zoya.backend.dto.BookingResponseDTO;
import com.zoya.backend.service.BookingService;
import com.zoya.backend.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/export")
public class ExportController {
    private static final Logger logger = LoggerFactory.getLogger(ExportController.class);

    private final BookingService bookingService;
    private final ExportService exportService;

    public ExportController(BookingService bookingService, ExportService exportService) {
        this.bookingService = bookingService;
        this.exportService = exportService;
    }

    // for exporting the attendees list
    @GetMapping("/event/{eventId}/attendees")
    public void exportAttendees(
            @PathVariable Long eventId,
            @RequestAttribute("userEmail") String email,
            HttpServletResponse response) throws IOException {

        logger.info("Organizer {} requested to export attendees for event {}", email, eventId);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=attendees_event_" + eventId + ".csv");

        List<BookingResponseDTO> attendees =
                bookingService.getBookingsForEvent(email, eventId);

        exportService.exportAttendeesToCsv(response.getWriter(), attendees);
    }
}
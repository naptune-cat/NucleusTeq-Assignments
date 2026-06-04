package com.zoya.backend.controller;

import com.zoya.backend.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/export")
public class ExportController {

    private static final Logger logger = LoggerFactory.getLogger(ExportController.class);

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/event/{eventId}/attendees")
    public void exportAttendees(
            @PathVariable Long eventId,
            @RequestAttribute("userEmail") String email,
            HttpServletResponse response) throws IOException {

        logger.info("Organizer {} requested CSV export for event {}", email, eventId);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=attendees_event_" + eventId + ".csv");
        exportService.exportEventAttendees(email, eventId, response.getWriter());
    }
}
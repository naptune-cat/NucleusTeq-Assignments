package com.zoya.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zoya.backend.dto.EventRequestDTO;
import com.zoya.backend.dto.EventResponseDTO;
import com.zoya.backend.service.EventService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController                    
@RequestMapping("/api/events")  
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // for creating event
    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(
            @Valid @RequestBody EventRequestDTO dto,
            @RequestAttribute("userEmail") String email) {   
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(dto, email));
    }

   
    @GetMapping("/organizer")
    public ResponseEntity<List<EventResponseDTO>> getMyEvents(
            @RequestAttribute("userEmail") String email) {
        return ResponseEntity.ok(eventService.getOrganizerEvents(email));
    }

  
    @GetMapping("/organizer/stats")
    public ResponseEntity<Map<String, Long>> getStats(
            @RequestAttribute("userEmail") String email) {
        return ResponseEntity.ok(eventService.getOrganizerStats(email));
    }


    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequestDTO dto,
            @RequestAttribute("userEmail") String email) {
        return ResponseEntity.ok(eventService.updateEvent(id, dto, email));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<String> cancelEvent(
            @PathVariable Long id,
            @RequestAttribute("userEmail") String email) {
        return ResponseEntity.ok(eventService.cancelEvent(id, email));
    }
}
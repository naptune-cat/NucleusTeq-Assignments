package com.zoya.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zoya.backend.dto.EventRequestDTO;
import com.zoya.backend.dto.EventResponseDTO;
import com.zoya.backend.service.EventService;
import com.zoya.backend.service.JwtService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/api")
public class EventController {
        private final EventService eventService;
        private final JwtService jwtService;

        public EventController(EventService eventService, JwtService jwtService) {
                this.eventService = eventService;
                this.jwtService = jwtService;
        }

        //for creating event
        @PostMapping("/events")
        public ResponseEntity<EventResponseDTO> createEvent(
        @RequestBody EventRequestDTO request,
        @RequestHeader("Authorization") String authHeader
        ) {
        String token = authHeader.substring(7); 
        String email = jwtService.extractEmail(token);

        EventResponseDTO response = eventService.createEvent(request, email);
        return ResponseEntity.ok(response);
        }
}
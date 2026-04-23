package com.zoya.backend.dto;

import java.time.LocalDateTime;

import com.zoya.backend.enums.EventStatus;

public class EventResponseDTO {

    private Long id;
    private String eventName;
    private String description;
    private LocalDateTime eventDateTime;
    private String venue;
    private String category;
    private Integer totalSeats;
    private Integer availableSeats;
    private Integer bookedSeats;
    private Double ticketPrice;
    private String organizerName; 
    private String organizerEmail;  
    private EventStatus status;
    private LocalDateTime createdAt;

    // Constructor
    public EventResponseDTO(Long id, String eventName, String description,
            LocalDateTime eventDateTime, String venue, String category,
            Integer totalSeats, Integer availableSeats, Integer bookedSeats,
            Double ticketPrice, String organizerName, String organizerEmail,
            EventStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.eventName = eventName;
        this.description = description;
        this.eventDateTime = eventDateTime;
        this.venue = venue;
        this.category = category;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.bookedSeats = bookedSeats;
        this.ticketPrice = ticketPrice;
        this.organizerName = organizerName;
        this.organizerEmail = organizerEmail;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;        
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
     }

     public String getVenue() {
         return venue;        
    }

    public String getCategory() {
        return category;        
    }

    public Integer getTotalSeats() {
        return totalSeats;        
    }

    public Integer getAvailableSeats() {
        return availableSeats;        
    }

    public Integer getBookedSeats() {
        return bookedSeats;        
    }

    public Double getTicketPrice() {
        return ticketPrice;        
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public EventStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
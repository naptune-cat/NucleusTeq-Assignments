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
  
    public EventResponseDTO(){}

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

    // Setters
    
    public void setId(Long id) {
        this.id = id;        
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;        
    }

    public void setDescription(String description) {
        this.description = description;        
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;        
    }

    public void setVenue(String venue) {
        this.venue = venue;        
    }

    public void setCategory(String category) {
        this.category = category;        
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;        
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;        
    }

    public void setBookedSeats(Integer bookedSeats) {
        this.bookedSeats = bookedSeats;        
    }

    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;        
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;        
    }

    public void setOrganizerEmail(String organizerEmail) {
        this.organizerEmail = organizerEmail;        
    }

    public void setStatus(EventStatus status) {
        this.status = status;        
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;        
    }
}
package com.zoya.backend.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EventRequestDTO {

    @NotBlank(message = "Event name is required")
    @Size(min=2)
    private String eventName;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDateTime;

    @NotBlank(message = "Venue is required")
    @Pattern(regexp = "^[a-zA-Z0-9 ,.-]{2,}$", 
         message = "Venue must be valid")
    private String venue;

    @NotNull(message = "Total seats required")
    @Min(value = 1, message = "Seats must be positive")
    private Integer totalSeats;

    @NotNull(message = "Ticket price required")
    @Min(value = 0, message = "Price cannot be negative")
    private Double ticketPrice;

    private String category;

    // Getters and Setters
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;        
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;        
    }
    public void setEventDateTime(LocalDateTime eventDateTime) { 
        this.eventDateTime = eventDateTime; 
    }

    public String getVenue() {
        return venue;        
    }

    public void setVenue(String venue) {
        this.venue = venue;        
    }

    public Integer getTotalSeats() {
        return totalSeats;        
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;        
    }

    public Double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;        
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
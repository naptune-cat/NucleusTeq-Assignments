package com.zoya.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BookingRequestDTO {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "Number of tickets is required")
    @Min(value = 1, message = "Must book at least 1 ticket")
    private Integer numberOfTickets;

    // getters & setters
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;        
    }

    public Integer getNumberOfTickets() {
        return numberOfTickets;        
    }

    public void setNumberOfTickets(Integer numberOfTickets) {
        this.numberOfTickets = numberOfTickets;        
    }
}
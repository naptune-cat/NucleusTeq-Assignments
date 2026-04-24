package com.zoya.backend.dto;

import com.zoya.backend.enums.BookingStatus;
import java.time.LocalDateTime;

public class BookingResponseDTO {

    private Long bookingId;
    private Long eventId;
    private String eventName;
    private String venue;
    private LocalDateTime eventDateTime;
    private Integer numberOfTickets;
    private Double totalAmount;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingTime;
    private LocalDateTime cancellationTime;

    // getters & setters
    public Long getBookingId() {
        return bookingId;        
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;        
    }

    public Long getEventId() {
        return eventId;        
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;        
    }

    public String getEventName() {
        return eventName;        
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;        
    }

    public String getVenue() {
        return venue;        
    }

    public void setVenue(String venue) {
        this.venue = venue;        
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;        
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public Integer getNumberOfTickets() {
        return numberOfTickets;        
    }

    public void setNumberOfTickets(Integer numberOfTickets) {
        this.numberOfTickets = numberOfTickets;        
    }

    public Double getTotalAmount() {
        return totalAmount;        
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;        
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;        
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;        
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;        
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;        
    }

    public LocalDateTime getCancellationTime() {
        return cancellationTime;        
    }

    public void setCancellationTime(LocalDateTime cancellationTime) {
        this.cancellationTime = cancellationTime;        
    }
}
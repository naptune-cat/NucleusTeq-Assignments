package com.zoya.backend.dto;

import com.zoya.backend.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingResponseDTO {
    private Long id;        
    private String userName;
    private String userEmail;
    private Long bookingId;
    private Long eventId;
    private String eventName;
    private String venue;
    private LocalDateTime eventDateTime;
    private Integer numberOfTickets;
    private BigDecimal totalAmount;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingTime;
    private LocalDateTime cancellationTime;


    // getters & setters
    public Long getBookingId() {
        return bookingId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;        
    }

    public void setTotalAmount(BigDecimal totalAmount) {
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
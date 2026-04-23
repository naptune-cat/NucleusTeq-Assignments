package com.zoya.backend.entity;

import java.time.LocalDateTime;

import com.zoya.backend.enums.EventStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;


@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventName;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer availableSeats;

    @Column(nullable = false)
    private Integer bookedSeats;

    @Column(nullable = false)
    private Double ticketPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @ManyToOne
    @JoinColumn(name = "organizer_id", referencedColumnName = "id",nullable = false)
    private User organizer;
    

    @Column(nullable = false)
    private LocalDateTime eventDateTime;

    @Column
    private String category;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // constructors

    public Event(Long id, String eventName, String description, String venue, Integer totalSeats,
        Integer availableSeats, Integer bookedSeats, Double ticketPrice, EventStatus status,
        User organizer) {
            this.id = id;
            this.eventName = eventName;
            this.description = description;
            this.venue = venue;
            this.totalSeats = totalSeats;
            this.availableSeats = availableSeats;
            this.bookedSeats = bookedSeats;
            this.ticketPrice = ticketPrice;
            this.status = status;
            this.organizer = organizer;
            this.eventDateTime = eventDateTime;
            this.category = category;
            this.createdAt = LocalDateTime.now();
    }

    public Event() {
    }

    // getters

    public Long getId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;
    }

    public String getVenue() {
        return venue;
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

    public EventStatus getStatus() {
        return status;
    }

    public User getOrganizer() {
        return organizer;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public String getCategory() {
        return category;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public void setBookedSeats(Integer bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }
    
    public void setCategory(String category) {
        this.category = category;
        
    }
}


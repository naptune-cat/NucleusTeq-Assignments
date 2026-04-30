package com.zoya.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.zoya.backend.enums.BookingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private Integer numberOfTickets;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus;

    @Column(nullable = false)
    private LocalDateTime bookingTime;

    private LocalDateTime cancellationTime;


    // constructors
    public Booking() {
    }

    public Booking(Long id, User user, Event event, Integer numberOfTickets, BigDecimal totalAmount,
            BookingStatus bookingStatus, LocalDateTime bookingTime, LocalDateTime cancellationTime) {
        this.id = id;
        this.user = user;
        this.event = event;
        this.numberOfTickets = numberOfTickets;
        this.totalAmount = totalAmount;
        this.bookingStatus = bookingStatus;
        this.bookingTime = bookingTime;
        this.cancellationTime = cancellationTime;
    }

    // getters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Event getEvent() {
        return event;
    }

    public Integer getNumberOfTickets() {
        return numberOfTickets;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public LocalDateTime getCancellationTime() {
        return cancellationTime;
    }

    //setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setNumberOfTickets(Integer numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public void setCancellationTime(LocalDateTime cancellationTime) {
        this.cancellationTime = cancellationTime;
    }
}
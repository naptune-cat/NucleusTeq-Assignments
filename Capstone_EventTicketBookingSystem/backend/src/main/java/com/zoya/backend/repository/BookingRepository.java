package com.zoya.backend.repository;

import com.zoya.backend.entity.Booking;
import com.zoya.backend.entity.User;
import com.zoya.backend.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // gets all bookings by user
    List<Booking> findByUser(User user);

    // gets bookings by user and status
    List<Booking> findByUserAndBookingStatus(User user, BookingStatus status);

    // gets all bookings for a specific event organizer view
    List<Booking> findByEvent_Id(Long eventId);

    // Preventing double booking by checking if user already has active booking for event
    boolean existsByUserAndEventIdAndBookingStatus(User user, Long eventId, BookingStatus status);

    // this count confirmed bookings for an event for stats in organizer view
    long countByEventIdAndBookingStatus(Long eventId, BookingStatus status);
}
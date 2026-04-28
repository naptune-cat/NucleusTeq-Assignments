package com.zoya.backend.service;

import com.zoya.backend.dto.*;
import com.zoya.backend.entity.*;
import com.zoya.backend.enums.*;
import com.zoya.backend.exception.*;
import com.zoya.backend.mapper.BookingMapper;
import com.zoya.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    // constructor 
    public BookingService(BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            EventRepository eventRepository,
            UserRepository userRepository,BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.bookingMapper = bookingMapper;
    }

    // Create a booking. by default it will have PENDING status
    @Transactional
    public BookingResponseDTO createBooking(String userEmail, BookingRequestDTO request) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userEmail));

        Event event = eventRepository.findByIdWithLock(request.getEventId())
        .orElseThrow(() -> new EventNotFoundException("Event not found"));

        // Validations
        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new InvalidBookingException("Cannot book tickets for a cancelled event.");
        }

        if (event.getEventDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidBookingException("Cannot book tickets for a past event.");
        }

        if (request.getNumberOfTickets() > event.getAvailableSeats()) {
            throw new InvalidBookingException("Not enough seats available. Only "
                    + event.getAvailableSeats() + " seats left.");
        }

        // Prevent duplicate active booking
        boolean alreadyBooked = bookingRepository.existsByUserAndEventIdAndBookingStatus(
                user, event.getId(), BookingStatus.CONFIRMED);
        if (alreadyBooked) {
            throw new InvalidBookingException("You already have an active booking for this event.");
        }

        // calculation of amount
        double totalAmount = event.getTicketPrice() * request.getNumberOfTickets();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setNumberOfTickets(request.getNumberOfTickets());
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus(BookingStatus.PENDING); // Pending until payment is done
        booking.setBookingTime(LocalDateTime.now());

        Booking saved = bookingRepository.save(booking);
        logger.info("PENDING booking created: bookingId={}, user={}, event={}",
                saved.getId(), userEmail, event.getId());

        return bookingMapper.mapToBookingResponse(saved);
    }

    // Confirm booking after mock payment 
    @Transactional
    public PaymentResponseDTO processPayment(String userEmail, PaymentRequestDTO request) {

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found: " + request.getBookingId()));

        // for Security we will ensure the booking belongs to this user
        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new InvalidBookingException("You are not authorized to pay for this booking.");
        }

        // only allow user to pay if booking in pending state
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new InvalidBookingException("Booking is not in PENDING state.");
        }

        Event event = eventRepository.findByIdWithLock(booking.getEvent().getId())
            .orElseThrow(() -> new EventNotFoundException("Event not found."));


        // Re checking availability again because seats might have changed since PENDING
        // to avoid race condition
        if (booking.getNumberOfTickets() > event.getAvailableSeats()) {
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            throw new InvalidBookingException("Sorry, seats are no longer available.");
        }

        // if number of seats available we will deduct the available seats with the current booked tickets
        event.setAvailableSeats(event.getAvailableSeats() - booking.getNumberOfTickets());
        // adding to booked seats
        event.setBookedSeats(event.getBookedSeats() + booking.getNumberOfTickets());
        eventRepository.save(event);

        // Confirm booking
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // Saving mock payment revord in payment table
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentStatus(PaymentStatus.SUCCESSFUL);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setPaymentTime(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment SUCCESSFUL: transactionId={}, bookingId={}, user={}",
                savedPayment.getTransactionId(), booking.getId(), userEmail);

        return bookingMapper.mapToPaymentResponse(savedPayment);
    }

    // Cancel Booking
    @Transactional
    public BookingResponseDTO cancelBooking(String userEmail, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found: " + bookingId));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new InvalidBookingException("You are not authorized to cancel this booking.");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingException("Booking is already cancelled.");
        }

        Event event = booking.getEvent();

        // Cannot cancel past events
        if (event.getEventDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidBookingException("Cannot cancel booking for a past event.");
        }

        // Must cancel at least 3 hours before event
        if (event.getEventDateTime().isBefore(LocalDateTime.now().plusHours(3))) {
            throw new InvalidBookingException("Cannot cancel booking within 3 hours of the event.");
        }

        // Restore seats (only if booking was CONFIRMED)
        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            event.setAvailableSeats(event.getAvailableSeats() + booking.getNumberOfTickets());
            event.setBookedSeats(event.getBookedSeats() - booking.getNumberOfTickets());
            eventRepository.save(event);
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.setCancellationTime(LocalDateTime.now());
        bookingRepository.save(booking);

        logger.info("Booking CANCELLED: bookingId={}, user={}", bookingId, userEmail);

        return bookingMapper.mapToBookingResponse(booking);
    }

    // Get My Bookings (Customer) 
    public List<BookingResponseDTO> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userEmail));

        return bookingRepository.findByUser(user)
                .stream()
                .map(bookingMapper::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    // method to show organizer details about who booked their event 
    public List<BookingResponseDTO> getBookingsForEvent(String organizerEmail, Long eventId) {
        // checking if such event exists
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + eventId));

        // checking if the event is organized by this organizer or not
        if (!event.getOrganizer().getEmail().equals(organizerEmail)) {
            throw new InvalidBookingException("You are not the organizer of this event.");
        }

        return bookingRepository.findByEventId(eventId)
                .stream()
                .map(bookingMapper::mapToBookingResponse)
                .collect(Collectors.toList());
    }

}
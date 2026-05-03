package com.zoya.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoya.backend.dto.*;
import com.zoya.backend.enums.BookingStatus;
import com.zoya.backend.enums.PaymentStatus;
import com.zoya.backend.service.BookingService;
import com.zoya.backend.service.ExportService;
import com.zoya.backend.service.JwtService;
import com.zoya.backend.security.JwtAuthFilter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ExportService exportService;

    // ADD THESE - Spring Security dependencies
    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("null")
    @Test
    void createBooking_test() throws Exception {

        BookingRequestDTO request = new BookingRequestDTO();
        request.setEventId(1L);
        request.setNumberOfTickets(2);

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingId(100L);
        response.setEventId(1L);
        response.setBookingStatus(BookingStatus.PENDING);

        when(bookingService.createBooking(anyString(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/bookings")
                        .requestAttr("userEmail", "zoya@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(100L))
                .andExpect(jsonPath("$.bookingStatus").value("PENDING"));
    }

    @SuppressWarnings("null")
    @Test
        void processPayment_test() throws Exception {

    PaymentRequestDTO request = new PaymentRequestDTO();
    request.setBookingId(1L);
    request.setPaymentMethod("UPI");

    PaymentResponseDTO response = new PaymentResponseDTO();
    response.setPaymentId(10L);
    response.setPaymentStatus(PaymentStatus.SUCCESSFUL);

    when(bookingService.processPayment(eq("zoya@test.com"), any(PaymentRequestDTO.class)))
            .thenReturn(response);

    mockMvc.perform(post("/api/bookings/payment")
                    .requestAttr("userEmail", "zoya@test.com")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentStatus").value("SUCCESSFUL"));
}

    @Test
    void cancelBooking_test() throws Exception {

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingId(1L);
        response.setBookingStatus(BookingStatus.CANCELLED);

        when(bookingService.cancelBooking(anyString(), eq(1L)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/bookings/1/cancel")
                        .requestAttr("userEmail", "zoya@test.com"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingStatus").value("CANCELLED"));
    }

    @Test
    void getMyBookings_test() throws Exception {

        BookingResponseDTO b1 = new BookingResponseDTO();
        b1.setBookingId(1L);

        BookingResponseDTO b2 = new BookingResponseDTO();
        b2.setBookingId(2L);

        when(bookingService.getMyBookings(anyString()))
                .thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/bookings/my")
                        .requestAttr("userEmail", "zoya@test.com"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getBookingsForEvent_test() throws Exception {

        BookingResponseDTO b = new BookingResponseDTO();
        b.setEventId(5L);

        when(bookingService.getBookingsForEvent(anyString(), eq(5L)))
                .thenReturn(List.of(b));

        mockMvc.perform(get("/api/bookings/event/5")
                        .requestAttr("userEmail", "org@test.com"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(5L));
    }
}
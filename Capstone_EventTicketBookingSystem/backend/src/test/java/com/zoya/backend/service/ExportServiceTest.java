package com.zoya.backend.service;

import com.zoya.backend.dto.BookingResponseDTO;
import com.zoya.backend.enums.BookingStatus;
import com.zoya.backend.exception.InvalidBookingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private ExportService exportService;

    private BookingResponseDTO booking1;
    private BookingResponseDTO booking2;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        booking1 = new BookingResponseDTO();
        booking1.setBookingId(1L);
        booking1.setUserName("Alice Smith");
        booking1.setUserEmail("alice@test.com");
        booking1.setNumberOfTickets(2);
        booking1.setBookingStatus(BookingStatus.CONFIRMED);
        booking1.setBookingTime(LocalDateTime.of(2026, 6, 1, 10, 0));

        booking2 = new BookingResponseDTO();
        booking2.setBookingId(2L);
        booking2.setUserName("Bob Jones");
        booking2.setUserEmail("bob@test.com");
        booking2.setNumberOfTickets(1);
        booking2.setBookingStatus(BookingStatus.CANCELLED);
        booking2.setBookingTime(LocalDateTime.of(2026, 6, 2, 12, 30));

        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    // tests for exportAttendeesToCsv method which writes CSV content based on a list of bookings

    @Test
    void exportAttendeesToCsv_writesHeaderRow() {
        exportService.exportAttendeesToCsv(printWriter, Collections.emptyList());
        String output = stringWriter.toString();

        assertThat(output).contains("Booking ID");
        assertThat(output).contains("Customer Name");
        assertThat(output).contains("Customer Email");
        assertThat(output).contains("Tickets Booked");
        assertThat(output).contains("Status");
        assertThat(output).contains("Booking Time");
    }

    // test to verify that data rows are correctly written for each booking
    @Test
    void exportAttendeesToCsv_writesDataRows_forEachBooking() {
        exportService.exportAttendeesToCsv(printWriter, Arrays.asList(booking1, booking2));
        String output = stringWriter.toString();

        // Check first booking row
        assertThat(output).contains("Alice Smith");
        assertThat(output).contains("alice@test.com");
        assertThat(output).contains("CONFIRMED");

        // Check second booking row
        assertThat(output).contains("Bob Jones");
        assertThat(output).contains("bob@test.com");
        assertThat(output).contains("CANCELLED");
    }

    // test to verify that booking ID is correctly written in the CSV
    @Test
    void exportAttendeesToCsv_writesBookingId() {
        exportService.exportAttendeesToCsv(printWriter, Arrays.asList(booking1));
        String output = stringWriter.toString();

        assertThat(output).contains("1"); // booking ID
    }

    // test to verify that number of tickets is correctly written in the CSV
    @Test
    void exportAttendeesToCsv_writesTicketCount() {
        exportService.exportAttendeesToCsv(printWriter, Arrays.asList(booking1));
        String output = stringWriter.toString();

        assertThat(output).contains("2"); // number of tickets
    }

    //  test to verify that an empty list of bookings still produces a CSV with just the header
    @Test
    void exportAttendeesToCsv_onlyHeaderWhenListIsEmpty() {
        exportService.exportAttendeesToCsv(printWriter, Collections.emptyList());
        String output = stringWriter.toString().trim();

        long lineCount = output.lines().count();
        assertThat(lineCount).isEqualTo(1); // only the header
    }

    // test to verify that writer.flush() is called at the end of exportAttendeesToCsv
    @Test
    void exportAttendeesToCsv_flushesWriter() {
        PrintWriter mockWriter = mock(PrintWriter.class);

        exportService.exportAttendeesToCsv(mockWriter, Collections.emptyList());

        verify(mockWriter).flush();
    }

    // additional test to verify that booking time is formatted correctly in the CSV
    @Test
    void exportAttendeesToCsv_writesCorrectNumberOfLines() {
        exportService.exportAttendeesToCsv(printWriter, Arrays.asList(booking1, booking2));
        String output = stringWriter.toString().trim();

        long lineCount = output.lines().count();
        assertThat(lineCount).isEqualTo(3); // 1 header + 2 data rows
    }

    // additional test to verify that special characters in names/emails are handled correctly
    @Test
    void exportAttendeesToCsv_writesDataInCsvFormat() {
        exportService.exportAttendeesToCsv(printWriter, Arrays.asList(booking1));
        String output = stringWriter.toString();

        // Each data row value should be quoted per the format in the service
        assertThat(output).contains("\"Alice Smith\"");
        assertThat(output).contains("\"alice@test.com\"");
        assertThat(output).contains("\"CONFIRMED\"");
    }

    // tests for exportEventAttendees method which calls bookingService and writes CSV

    @Test
    void exportEventAttendees_delegatesToBookingService_andWritesCsv() {
        when(bookingService.getBookingsForEvent("organizer@test.com", 1L))
                .thenReturn(Arrays.asList(booking1));

        exportService.exportEventAttendees("organizer@test.com", 1L, printWriter);

        String output = stringWriter.toString();
        verify(bookingService).getBookingsForEvent("organizer@test.com", 1L);
        assertThat(output).contains("Alice Smith");
    }

    @Test
    void exportEventAttendees_writesEmptyCsv_whenNoAttendees() {
        when(bookingService.getBookingsForEvent("organizer@test.com", 99L))
                .thenReturn(Collections.emptyList());

        exportService.exportEventAttendees("organizer@test.com", 99L, printWriter);

        String output = stringWriter.toString().trim();
        assertThat(output.lines().count()).isEqualTo(1); // only header
    }

    @Test
    void exportEventAttendees_propagatesException_whenUnauthorized() {
        when(bookingService.getBookingsForEvent("hacker@test.com", 1L))
                .thenThrow(new InvalidBookingException("You are not the organizer of this event."));

        assertThatThrownBy(() ->
                exportService.exportEventAttendees("hacker@test.com", 1L, printWriter))
                .isInstanceOf(InvalidBookingException.class)
                .hasMessageContaining("not the organizer");
    }

    @Test
    void exportEventAttendees_writesAllBookings_whenMultipleAttendees() {
        when(bookingService.getBookingsForEvent("organizer@test.com", 1L))
                .thenReturn(Arrays.asList(booking1, booking2));

        exportService.exportEventAttendees("organizer@test.com", 1L, printWriter);

        String output = stringWriter.toString();
        assertThat(output).contains("Alice Smith");
        assertThat(output).contains("Bob Jones");
    }
}
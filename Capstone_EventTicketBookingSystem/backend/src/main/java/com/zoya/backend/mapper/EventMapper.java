package com.zoya.backend.mapper;

import com.zoya.backend.dto.EventRequestDTO;
import com.zoya.backend.dto.EventResponseDTO;
import com.zoya.backend.entity.Event;
import com.zoya.backend.entity.User;
import com.zoya.backend.enums.EventStatus;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    // Entity → ResponseDTO mapping

    public EventResponseDTO mapToDTO(Event event) {
        if (event == null) return null;

        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setEventName(event.getEventName());
        dto.setDescription(event.getDescription());
        dto.setEventDateTime(event.getEventDateTime());
        dto.setVenue(event.getVenue());
        dto.setCategory(event.getCategory());
        dto.setTotalSeats(event.getTotalSeats());
        dto.setAvailableSeats(event.getAvailableSeats());
        dto.setBookedSeats(event.getBookedSeats());
        dto.setTicketPrice(event.getTicketPrice());
        dto.setStatus(event.getStatus());
        dto.setCreatedAt(event.getCreatedAt());
        
        // Organizer info is useful when we do not wish to expose name email info for organizer
        if (event.getOrganizer() != null) {
            dto.setOrganizerName(event.getOrganizer().getName());
            dto.setOrganizerEmail(event.getOrganizer().getEmail());
        }

        return dto;
    }

    // RequestDTO → Entity mapping

    public Event mapToEntity(EventRequestDTO dto, User organizer) {
        if (dto == null)
            return null;

        Event event = new Event();
        event.setEventName(dto.getEventName());
        event.setDescription(dto.getDescription());
        event.setEventDateTime(dto.getEventDateTime());
        event.setVenue(dto.getVenue());
        event.setCategory(dto.getCategory());
        event.setTotalSeats(dto.getTotalSeats());
        event.setAvailableSeats(dto.getTotalSeats()); // initially all seats are available
        event.setBookedSeats(0);
        event.setTicketPrice(dto.getTicketPrice());
        event.setStatus(EventStatus.ACTIVE);
        event.setOrganizer(organizer);

        return event;
    }

   //  RequestDTO → existing Entity for updation

    public void updateEntityFromDTO(EventRequestDTO dto, Event event) {
        if (dto == null || event == null) return;

        event.setEventName(dto.getEventName());
        event.setDescription(dto.getDescription());
        event.setEventDateTime(dto.getEventDateTime());
        event.setVenue(dto.getVenue());
        event.setCategory(dto.getCategory());
        event.setTicketPrice(dto.getTicketPrice());

        // recalculating available seats if totalSeats changed
        int previouslyBooked = event.getBookedSeats();
        event.setTotalSeats(dto.getTotalSeats());
        event.setAvailableSeats(dto.getTotalSeats() - previouslyBooked);
    }
 
}
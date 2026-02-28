package com.travelmateai.backend.dto.response;

import com.travelmateai.backend.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for booking information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long tripId;
    private String tripTitle;
    private String destination;
    private Integer numberOfSeats;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime bookingDate;
    private LocalDateTime updatedAt;

    /**
     * Convert Booking entity to BookingResponse DTO
     */
    public static BookingResponse fromEntity(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .userEmail(booking.getUser().getEmail())
                .tripId(booking.getTrip().getId())
                .tripTitle(booking.getTrip().getTitle())
                .destination(booking.getTrip().getDestination())
                .numberOfSeats(booking.getNumberOfSeats())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name())
                .bookingDate(booking.getBookingDate())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}

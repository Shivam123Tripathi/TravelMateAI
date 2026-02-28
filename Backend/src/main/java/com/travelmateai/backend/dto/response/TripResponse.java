package com.travelmateai.backend.dto.response;

import com.travelmateai.backend.entity.Trip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for trip information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripResponse {

    private Long id;
    private String title;
    private String destination;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private Integer availableSeats;
    private Integer totalSeats;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert Trip entity to TripResponse DTO
     */
    public static TripResponse fromEntity(Trip trip) {
        return TripResponse.builder()
                .id(trip.getId())
                .title(trip.getTitle())
                .destination(trip.getDestination())
                .description(trip.getDescription())
                .price(trip.getPrice())
                .duration(trip.getDuration())
                .availableSeats(trip.getAvailableSeats())
                .totalSeats(trip.getTotalSeats())
                .imageUrl(trip.getImageUrl())
                .createdAt(trip.getCreatedAt())
                .updatedAt(trip.getUpdatedAt())
                .build();
    }
}

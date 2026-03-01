package com.travelmateai.backend.dto.response;

import com.travelmateai.backend.entity.SeatLock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for seat lock information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatLockResponse {

    private Long id;
    private Long userId;
    private String userEmail;
    private Long tripId;
    private String tripTitle;
    private String destination;
    private Integer numberOfSeats;
    private LocalDateTime lockTime;
    private LocalDateTime expiryTime;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert SeatLock entity to SeatLockResponse DTO
     */
    public static SeatLockResponse fromEntity(SeatLock seatLock) {
        return SeatLockResponse.builder()
                .id(seatLock.getId())
                .userId(seatLock.getUser().getId())
                .userEmail(seatLock.getUser().getEmail())
                .tripId(seatLock.getTrip().getId())
                .tripTitle(seatLock.getTrip().getTitle())
                .destination(seatLock.getTrip().getDestination())
                .numberOfSeats(seatLock.getNumberOfSeats())
                .lockTime(seatLock.getLockTime())
                .expiryTime(seatLock.getExpiryTime())
                .status(seatLock.getStatus().name())
                .createdAt(seatLock.getCreatedAt())
                .updatedAt(seatLock.getUpdatedAt())
                .build();
    }
}

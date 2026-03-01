package com.travelmateai.backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a seat lock.
 * User selects seats and they get locked for a configurable duration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatLockRequest {

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "At least 1 seat must be locked")
    @Max(value = 10, message = "Maximum 10 seats can be locked at once")
    private Integer numberOfSeats;
}

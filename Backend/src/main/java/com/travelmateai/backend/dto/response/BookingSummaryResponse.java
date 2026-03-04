package com.travelmateai.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Typed DTO for booking summary report.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSummaryResponse {

    private Long totalConfirmedBookings;
    private Long totalCancelledBookings;
    private Long totalBookings;
}

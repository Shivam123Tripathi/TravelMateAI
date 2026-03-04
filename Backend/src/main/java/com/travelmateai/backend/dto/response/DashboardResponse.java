package com.travelmateai.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Typed DTO for comprehensive admin dashboard report.
 * Aggregates booking summary, destination stats, and revenue data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private BookingSummaryResponse bookings;
    private DestinationStatsResponse popularDestination;
    private RevenueResponse revenue;
}

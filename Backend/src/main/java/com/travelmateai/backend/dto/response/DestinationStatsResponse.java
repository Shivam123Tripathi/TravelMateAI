package com.travelmateai.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Typed DTO for destination statistics report.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DestinationStatsResponse {

    private String topDestination;
    private Long bookingCount;
    private Map<String, Long> allDestinations;
}

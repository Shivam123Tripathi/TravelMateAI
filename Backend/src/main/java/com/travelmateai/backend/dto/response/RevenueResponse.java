package com.travelmateai.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Typed DTO for revenue report.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueResponse {

    private BigDecimal totalRevenue;
    private String currency;
}

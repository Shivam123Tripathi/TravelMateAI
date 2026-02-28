package com.travelmateai.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for report data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {

    private String reportType;
    private Object data;

    public static ReportResponse of(String reportType, Object data) {
        return ReportResponse.builder()
                .reportType(reportType)
                .data(data)
                .build();
    }
}

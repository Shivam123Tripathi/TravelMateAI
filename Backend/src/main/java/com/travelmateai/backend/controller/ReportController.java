package com.travelmateai.backend.controller;

import com.travelmateai.backend.dto.response.ApiResponse;
import com.travelmateai.backend.dto.response.BookingSummaryResponse;
import com.travelmateai.backend.dto.response.DashboardResponse;
import com.travelmateai.backend.dto.response.DestinationStatsResponse;
import com.travelmateai.backend.dto.response.RevenueResponse;
import com.travelmateai.backend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Report operations.
 * Admin only — provides analytics and reporting data.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Reports", description = "Admin analytics and reporting endpoints")
public class ReportController {

    private final ReportService reportService;

    /**
     * Get total bookings report (confirmed, cancelled, total).
     */
    @GetMapping("/total-bookings")
    @Operation(summary = "Get total bookings", description = "Returns count of confirmed, cancelled, and total bookings")
    public ResponseEntity<ApiResponse<BookingSummaryResponse>> getTotalBookings() {
        BookingSummaryResponse report = reportService.getTotalBookings();
        return ResponseEntity.ok(ApiResponse.success("Total bookings report", report));
    }

    /**
     * Get most popular destination based on booking count.
     */
    @GetMapping("/popular-destination")
    @Operation(summary = "Get popular destination", description = "Returns the most booked destination with stats")
    public ResponseEntity<ApiResponse<DestinationStatsResponse>> getPopularDestination() {
        DestinationStatsResponse report = reportService.getPopularDestination();
        return ResponseEntity.ok(ApiResponse.success("Popular destination report", report));
    }

    /**
     * Get total revenue from confirmed bookings.
     */
    @GetMapping("/revenue")
    @Operation(summary = "Get total revenue", description = "Returns total revenue from all confirmed bookings")
    public ResponseEntity<ApiResponse<RevenueResponse>> getRevenue() {
        RevenueResponse report = reportService.getRevenue();
        return ResponseEntity.ok(ApiResponse.success("Revenue report", report));
    }

    /**
     * Get comprehensive dashboard report with all analytics combined.
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard", description = "Returns comprehensive dashboard with bookings, destinations, and revenue data")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        DashboardResponse report = reportService.getDashboardReport();
        return ResponseEntity.ok(ApiResponse.success("Dashboard report", report));
    }
}

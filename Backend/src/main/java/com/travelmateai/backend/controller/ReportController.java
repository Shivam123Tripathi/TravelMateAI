package com.travelmateai.backend.controller;

import com.travelmateai.backend.dto.response.ApiResponse;
import com.travelmateai.backend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST Controller for Report operations.
 * Admin only - provides analytics and reporting data.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Reports", description = "Admin reporting and analytics APIs")
public class ReportController {

    private final ReportService reportService;

    /**
     * Get total bookings report
     */
    @GetMapping("/total-bookings")
    @Operation(summary = "Total bookings", description = "Get total bookings count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTotalBookings() {

        Map<String, Object> report = reportService.getTotalBookings();
        return ResponseEntity.ok(ApiResponse.success("Total bookings report", report));
    }

    /**
     * Get most popular destination
     */
    @GetMapping("/popular-destination")
    @Operation(summary = "Popular destination", description = "Get most popular destination by bookings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPopularDestination() {

        Map<String, Object> report = reportService.getPopularDestination();
        return ResponseEntity.ok(ApiResponse.success("Popular destination report", report));
    }

    /**
     * Get total revenue
     */
    @GetMapping("/revenue")
    @Operation(summary = "Revenue report", description = "Get total revenue from confirmed bookings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenue() {

        Map<String, Object> report = reportService.getRevenue();
        return ResponseEntity.ok(ApiResponse.success("Revenue report", report));
    }

    /**
     * Get comprehensive dashboard report
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard report", description = "Get comprehensive dashboard analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {

        Map<String, Object> report = reportService.getDashboardReport();
        return ResponseEntity.ok(ApiResponse.success("Dashboard report", report));
    }
}

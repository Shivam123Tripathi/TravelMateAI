package com.travelmateai.backend.controller;

import com.travelmateai.backend.dto.response.ApiResponse;
import com.travelmateai.backend.service.ReportService;
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
public class ReportController {

    private final ReportService reportService;

    /**
     * Get total bookings report
     */
    @GetMapping("/total-bookings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTotalBookings() {

        Map<String, Object> report = reportService.getTotalBookings();
        return ResponseEntity.ok(ApiResponse.success("Total bookings report", report));
    }

    /**
     * Get most popular destination
     */
    @GetMapping("/popular-destination")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPopularDestination() {

        Map<String, Object> report = reportService.getPopularDestination();
        return ResponseEntity.ok(ApiResponse.success("Popular destination report", report));
    }

    /**
     * Get total revenue
     */
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenue() {

        Map<String, Object> report = reportService.getRevenue();
        return ResponseEntity.ok(ApiResponse.success("Revenue report", report));
    }

    /**
     * Get comprehensive dashboard report
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {

        Map<String, Object> report = reportService.getDashboardReport();
        return ResponseEntity.ok(ApiResponse.success("Dashboard report", report));
    }
}

package com.travelmateai.backend.service;

import com.travelmateai.backend.entity.BookingStatus;
import com.travelmateai.backend.repository.BookingRepository;
import com.travelmateai.backend.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating reports.
 * Provides analytics data for admin dashboard.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;

    /**
     * Get total number of confirmed bookings
     */
    public Map<String, Object> getTotalBookings() {
        Long totalConfirmed = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        Long totalCancelled = bookingRepository.countByStatus(BookingStatus.CANCELLED);

        Map<String, Object> report = new HashMap<>();
        report.put("totalConfirmedBookings", totalConfirmed);
        report.put("totalCancelledBookings", totalCancelled);
        report.put("totalBookings", totalConfirmed + totalCancelled);

        log.info("Total bookings report generated");
        return report;
    }

    /**
     * Get most popular destination based on bookings
     */
    public Map<String, Object> getPopularDestination() {
        List<Object[]> destinations = bookingRepository.countBookingsByDestination(BookingStatus.CONFIRMED);

        Map<String, Object> report = new HashMap<>();

        if (!destinations.isEmpty()) {
            // Get top destination
            Object[] topDestination = destinations.get(0);
            report.put("topDestination", topDestination[0]);
            report.put("bookingCount", topDestination[1]);

            // Get all destinations ranking
            Map<String, Long> allDestinations = new HashMap<>();
            for (Object[] dest : destinations) {
                allDestinations.put((String) dest[0], (Long) dest[1]);
            }
            report.put("allDestinations", allDestinations);
        } else {
            report.put("topDestination", "No bookings yet");
            report.put("bookingCount", 0);
        }

        log.info("Popular destination report generated");
        return report;
    }

    /**
     * Get total revenue from confirmed bookings
     */
    public Map<String, Object> getRevenue() {
        BigDecimal totalRevenue = bookingRepository.calculateTotalRevenue(BookingStatus.CONFIRMED);

        Map<String, Object> report = new HashMap<>();
        report.put("totalRevenue", totalRevenue);
        report.put("currency", "INR");

        log.info("Revenue report generated");
        return report;
    }

    /**
     * Get comprehensive dashboard report
     */
    public Map<String, Object> getDashboardReport() {
        Map<String, Object> dashboard = new HashMap<>();

        dashboard.put("bookings", getTotalBookings());
        dashboard.put("popularDestination", getPopularDestination());
        dashboard.put("revenue", getRevenue());

        log.info("Dashboard report generated");
        return dashboard;
    }
}

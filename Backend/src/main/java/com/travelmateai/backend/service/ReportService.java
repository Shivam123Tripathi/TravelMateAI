package com.travelmateai.backend.service;

import com.travelmateai.backend.dto.response.BookingSummaryResponse;
import com.travelmateai.backend.dto.response.DashboardResponse;
import com.travelmateai.backend.dto.response.DestinationStatsResponse;
import com.travelmateai.backend.dto.response.RevenueResponse;
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
 * Provides analytics data for admin dashboard using typed DTOs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;

    /**
     * Get total number of confirmed and cancelled bookings.
     */
    public BookingSummaryResponse getTotalBookings() {
        Long totalConfirmed = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        Long totalCancelled = bookingRepository.countByStatus(BookingStatus.CANCELLED);

        log.info("Total bookings report generated");

        return BookingSummaryResponse.builder()
                .totalConfirmedBookings(totalConfirmed)
                .totalCancelledBookings(totalCancelled)
                .totalBookings(totalConfirmed + totalCancelled)
                .build();
    }

    /**
     * Get most popular destination based on confirmed bookings.
     */
    public DestinationStatsResponse getPopularDestination() {
        List<Object[]> destinations = bookingRepository.countBookingsByDestination(BookingStatus.CONFIRMED);

        DestinationStatsResponse.DestinationStatsResponseBuilder builder = DestinationStatsResponse.builder();

        if (!destinations.isEmpty()) {
            // Get top destination
            Object[] topDestination = destinations.get(0);
            builder.topDestination((String) topDestination[0]);
            builder.bookingCount((Long) topDestination[1]);

            // Get all destinations ranking
            Map<String, Long> allDestinations = new HashMap<>();
            for (Object[] dest : destinations) {
                allDestinations.put((String) dest[0], (Long) dest[1]);
            }
            builder.allDestinations(allDestinations);
        } else {
            builder.topDestination("No bookings yet");
            builder.bookingCount(0L);
            builder.allDestinations(Map.of());
        }

        log.info("Popular destination report generated");
        return builder.build();
    }

    /**
     * Get total revenue from confirmed bookings.
     */
    public RevenueResponse getRevenue() {
        BigDecimal totalRevenue = bookingRepository.calculateTotalRevenue(BookingStatus.CONFIRMED);

        log.info("Revenue report generated");

        return RevenueResponse.builder()
                .totalRevenue(totalRevenue)
                .currency("INR")
                .build();
    }

    /**
     * Get comprehensive dashboard report aggregating all analytics.
     */
    public DashboardResponse getDashboardReport() {
        DashboardResponse dashboard = DashboardResponse.builder()
                .bookings(getTotalBookings())
                .popularDestination(getPopularDestination())
                .revenue(getRevenue())
                .build();

        log.info("Dashboard report generated");
        return dashboard;
    }
}

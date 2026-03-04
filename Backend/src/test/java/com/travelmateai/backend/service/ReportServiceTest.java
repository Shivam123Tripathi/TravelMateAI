package com.travelmateai.backend.service;

import com.travelmateai.backend.dto.response.BookingSummaryResponse;
import com.travelmateai.backend.dto.response.DashboardResponse;
import com.travelmateai.backend.dto.response.DestinationStatsResponse;
import com.travelmateai.backend.dto.response.RevenueResponse;
import com.travelmateai.backend.entity.BookingStatus;
import com.travelmateai.backend.repository.BookingRepository;
import com.travelmateai.backend.repository.TripRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService Tests")
class ReportServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    @DisplayName("Should return total bookings summary")
    void getTotalBookings() {
        when(bookingRepository.countByStatus(BookingStatus.CONFIRMED)).thenReturn(25L);
        when(bookingRepository.countByStatus(BookingStatus.CANCELLED)).thenReturn(5L);

        BookingSummaryResponse response = reportService.getTotalBookings();

        assertThat(response.getTotalConfirmedBookings()).isEqualTo(25L);
        assertThat(response.getTotalCancelledBookings()).isEqualTo(5L);
        assertThat(response.getTotalBookings()).isEqualTo(30L);
    }

    @Test
    @DisplayName("Should return popular destination with stats")
    void getPopularDestination() {
        List<Object[]> destinationData = new ArrayList<>();
        destinationData.add(new Object[] { "Goa", 15L });
        destinationData.add(new Object[] { "Manali", 8L });
        destinationData.add(new Object[] { "Jaipur", 3L });

        when(bookingRepository.countBookingsByDestination(BookingStatus.CONFIRMED))
                .thenReturn(destinationData);

        DestinationStatsResponse response = reportService.getPopularDestination();

        assertThat(response.getTopDestination()).isEqualTo("Goa");
        assertThat(response.getBookingCount()).isEqualTo(15L);
        assertThat(response.getAllDestinations()).hasSize(3);
        assertThat(response.getAllDestinations()).containsEntry("Goa", 15L);
    }

    @Test
    @DisplayName("Should handle no bookings for popular destination")
    void getPopularDestination_NoBookings() {
        when(bookingRepository.countBookingsByDestination(BookingStatus.CONFIRMED))
                .thenReturn(Collections.emptyList());

        DestinationStatsResponse response = reportService.getPopularDestination();

        assertThat(response.getTopDestination()).isEqualTo("No bookings yet");
        assertThat(response.getBookingCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should return revenue with currency")
    void getRevenue() {
        when(bookingRepository.calculateTotalRevenue(BookingStatus.CONFIRMED))
                .thenReturn(BigDecimal.valueOf(150000));

        RevenueResponse response = reportService.getRevenue();

        assertThat(response.getTotalRevenue()).isEqualTo(BigDecimal.valueOf(150000));
        assertThat(response.getCurrency()).isEqualTo("INR");
    }

    @Test
    @DisplayName("Should return complete dashboard report")
    void getDashboardReport() {
        when(bookingRepository.countByStatus(BookingStatus.CONFIRMED)).thenReturn(25L);
        when(bookingRepository.countByStatus(BookingStatus.CANCELLED)).thenReturn(5L);

        List<Object[]> destData = new ArrayList<>();
        destData.add(new Object[]{"Goa", 15L});
        when(bookingRepository.countBookingsByDestination(any())).thenReturn(destData);

        when(bookingRepository.calculateTotalRevenue(any()))
                .thenReturn(BigDecimal.valueOf(150000));

        DashboardResponse dashboard = reportService.getDashboardReport();

        assertThat(dashboard.getBookings()).isNotNull();
        assertThat(dashboard.getPopularDestination()).isNotNull();
        assertThat(dashboard.getRevenue()).isNotNull();
        assertThat(dashboard.getBookings().getTotalBookings()).isEqualTo(30L);
        assertThat(dashboard.getPopularDestination().getTopDestination()).isEqualTo("Goa");
        assertThat(dashboard.getRevenue().getTotalRevenue()).isEqualTo(BigDecimal.valueOf(150000));
    }
}

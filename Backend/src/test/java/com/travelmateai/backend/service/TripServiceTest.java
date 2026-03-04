package com.travelmateai.backend.service;

import com.travelmateai.backend.dto.request.TripRequest;
import com.travelmateai.backend.dto.response.TripResponse;
import com.travelmateai.backend.entity.Booking;
import com.travelmateai.backend.entity.BookingStatus;
import com.travelmateai.backend.entity.Trip;
import com.travelmateai.backend.exception.BadRequestException;
import com.travelmateai.backend.exception.ResourceNotFoundException;
import com.travelmateai.backend.repository.BookingRepository;
import com.travelmateai.backend.repository.SeatLockRepository;
import com.travelmateai.backend.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripService Tests")
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private SeatLockRepository seatLockRepository;

    @InjectMocks
    private TripService tripService;

    private Trip testTrip;
    private TripRequest tripRequest;

    @BeforeEach
    void setUp() {
        testTrip = Trip.builder()
                .id(1L)
                .title("Goa Beach Trip")
                .destination("Goa")
                .description("A beautiful beach trip")
                .price(BigDecimal.valueOf(5000))
                .duration(3)
                .totalSeats(50)
                .availableSeats(50)
                .imageUrl("https://example.com/goa.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        tripRequest = TripRequest.builder()
                .title("Goa Beach Trip")
                .destination("Goa")
                .description("A beautiful beach trip")
                .price(BigDecimal.valueOf(5000))
                .duration(3)
                .totalSeats(50)
                .imageUrl("https://example.com/goa.jpg")
                .build();
    }

    @Nested
    @DisplayName("Create Trip")
    class CreateTripTests {

        @Test
        @DisplayName("Should create trip successfully")
        void createTrip_Success() {
            when(tripRepository.save(any(Trip.class))).thenReturn(testTrip);

            TripResponse response = tripService.createTrip(tripRequest);

            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo("Goa Beach Trip");
            assertThat(response.getDestination()).isEqualTo("Goa");
            assertThat(response.getAvailableSeats()).isEqualTo(50);
            verify(tripRepository).save(any(Trip.class));
        }
    }

    @Nested
    @DisplayName("Get Trips")
    class GetTripsTests {

        @Test
        @DisplayName("Should return paginated trips")
        void getAllTrips_Paginated() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Trip> tripPage = new PageImpl<>(List.of(testTrip));
            when(tripRepository.findAll(pageable)).thenReturn(tripPage);

            Page<TripResponse> response = tripService.getAllTrips(pageable);

            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getTitle()).isEqualTo("Goa Beach Trip");
        }

        @Test
        @DisplayName("Should return trip by ID")
        void getTripById_Success() {
            when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));

            TripResponse response = tripService.getTripById(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo("Goa Beach Trip");
        }

        @Test
        @DisplayName("Should throw exception for non-existent trip")
        void getTripById_NotFound() {
            when(tripRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tripService.getTripById(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should search trips by destination")
        void searchByDestination() {
            when(tripRepository.searchByDestination("Goa")).thenReturn(List.of(testTrip));

            List<TripResponse> results = tripService.searchByDestination("Goa");

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getDestination()).isEqualTo("Goa");
        }
    }

    @Nested
    @DisplayName("Update Trip")
    class UpdateTripTests {

        @Test
        @DisplayName("Should update trip with seat recalculation")
        void updateTrip_Success() {
            TripRequest updateRequest = TripRequest.builder()
                    .title("Updated Goa Trip")
                    .destination("Goa")
                    .description("Updated description")
                    .price(BigDecimal.valueOf(6000))
                    .duration(4)
                    .totalSeats(60)
                    .imageUrl("https://example.com/goa2.jpg")
                    .build();

            when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
            when(tripRepository.save(any(Trip.class))).thenReturn(testTrip);

            TripResponse response = tripService.updateTrip(1L, updateRequest);

            assertThat(response).isNotNull();
            verify(tripRepository).save(any(Trip.class));
        }
    }

    @Nested
    @DisplayName("Delete Trip")
    class DeleteTripTests {

        @Test
        @DisplayName("Should delete trip with no active bookings")
        void deleteTrip_Success() {
            when(tripRepository.existsById(1L)).thenReturn(true);
            when(bookingRepository.findByTripId(1L)).thenReturn(Collections.emptyList());

            tripService.deleteTrip(1L);

            verify(seatLockRepository).deleteByTripId(1L);
            verify(bookingRepository).deleteByTripId(1L);
            verify(tripRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when trip has active bookings")
        void deleteTrip_ActiveBookings() {
            Booking activeBooking = Booking.builder()
                    .id(1L)
                    .status(BookingStatus.CONFIRMED)
                    .build();

            when(tripRepository.existsById(1L)).thenReturn(true);
            when(bookingRepository.findByTripId(1L)).thenReturn(List.of(activeBooking));

            assertThatThrownBy(() -> tripService.deleteTrip(1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("active booking");
        }

        @Test
        @DisplayName("Should throw exception for non-existent trip")
        void deleteTrip_NotFound() {
            when(tripRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> tripService.deleteTrip(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}

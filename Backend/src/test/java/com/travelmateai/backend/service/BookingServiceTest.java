package com.travelmateai.backend.service;

import com.travelmateai.backend.dto.request.BookingRequest;
import com.travelmateai.backend.dto.response.BookingResponse;
import com.travelmateai.backend.entity.*;
import com.travelmateai.backend.exception.BadRequestException;
import com.travelmateai.backend.exception.InsufficientSeatsException;
import com.travelmateai.backend.exception.ResourceNotFoundException;
import com.travelmateai.backend.repository.BookingRepository;
import com.travelmateai.backend.repository.SeatLockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Tests")
class BookingServiceTest {

        @Mock
        private BookingRepository bookingRepository;
        @Mock
        private TripService tripService;
        @Mock
        private UserService userService;
        @Mock
        private EmailService emailService;
        @Mock
        private SeatLockRepository seatLockRepository;

        @InjectMocks
        private BookingService bookingService;

        private User testUser;
        private Trip testTrip;
        private Booking testBooking;

        @BeforeEach
        void setUp() {
                testUser = User.builder()
                                .id(1L)
                                .name("Test User")
                                .email("test@example.com")
                                .password("encoded")
                                .role(Role.USER)
                                .build();

                testTrip = Trip.builder()
                                .id(1L)
                                .title("Goa Trip")
                                .destination("Goa")
                                .price(BigDecimal.valueOf(5000))
                                .totalSeats(50)
                                .availableSeats(10)
                                .build();

                testBooking = Booking.builder()
                                .id(1L)
                                .user(testUser)
                                .trip(testTrip)
                                .numberOfSeats(2)
                                .totalPrice(BigDecimal.valueOf(10000))
                                .status(BookingStatus.CONFIRMED)
                                .bookingDate(LocalDateTime.now())
                                .build();
        }

        private void mockSecurityContext(String email) {
                Authentication auth = mock(Authentication.class);
                when(auth.getName()).thenReturn(email);
                SecurityContext securityContext = mock(SecurityContext.class);
                when(securityContext.getAuthentication()).thenReturn(auth);
                SecurityContextHolder.setContext(securityContext);
        }

        @Nested
        @DisplayName("Create Booking")
        class CreateBookingTests {

                @Test
                @DisplayName("Should create booking successfully (direct path)")
                void createBooking_Success() {
                        BookingRequest request = BookingRequest.builder()
                                        .tripId(1L)
                                        .numberOfSeats(2)
                                        .build();

                        when(userService.getCurrentUser()).thenReturn(testUser);
                        when(tripService.getTripEntityById(1L)).thenReturn(testTrip);
                        when(seatLockRepository.findActiveLockByUserAndTrip(anyLong(), anyLong(), any()))
                                        .thenReturn(Optional.empty());
                        when(bookingRepository.existsByUserIdAndTripIdAndStatus(anyLong(), anyLong(), any()))
                                        .thenReturn(false);
                        when(seatLockRepository.sumLockedSeatsByTrip(anyLong(), any())).thenReturn(0);
                        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

                        BookingResponse response = bookingService.createBooking(request);

                        assertThat(response).isNotNull();
                        assertThat(response.getNumberOfSeats()).isEqualTo(2);
                        assertThat(response.getTotalPrice()).isEqualTo(BigDecimal.valueOf(10000));
                        verify(emailService).sendBookingConfirmationEmail(any(Booking.class));
                }

                @Test
                @DisplayName("Should throw exception for insufficient seats")
                void createBooking_InsufficientSeats() {
                        testTrip.setAvailableSeats(1); // Only 1 seat available

                        BookingRequest request = BookingRequest.builder()
                                        .tripId(1L)
                                        .numberOfSeats(5) // Requesting 5
                                        .build();

                        when(userService.getCurrentUser()).thenReturn(testUser);
                        when(tripService.getTripEntityById(1L)).thenReturn(testTrip);
                        when(seatLockRepository.findActiveLockByUserAndTrip(anyLong(), anyLong(), any()))
                                        .thenReturn(Optional.empty());
                        when(bookingRepository.existsByUserIdAndTripIdAndStatus(anyLong(), anyLong(), any()))
                                        .thenReturn(false);
                        when(seatLockRepository.sumLockedSeatsByTrip(anyLong(), any())).thenReturn(0);

                        assertThatThrownBy(() -> bookingService.createBooking(request))
                                        .isInstanceOf(InsufficientSeatsException.class);
                }

                @Test
                @DisplayName("Should prevent duplicate bookings")
                void createBooking_Duplicate() {
                        BookingRequest request = BookingRequest.builder()
                                        .tripId(1L)
                                        .numberOfSeats(2)
                                        .build();

                        when(userService.getCurrentUser()).thenReturn(testUser);
                        when(tripService.getTripEntityById(1L)).thenReturn(testTrip);
                        when(bookingRepository.existsByUserIdAndTripIdAndStatus(1L, 1L, BookingStatus.CONFIRMED))
                                        .thenReturn(true);

                        assertThatThrownBy(() -> bookingService.createBooking(request))
                                        .isInstanceOf(BadRequestException.class)
                                        .hasMessageContaining("already");
                }
        }

        @Nested
        @DisplayName("Cancel Booking")
        class CancelBookingTests {

                @Test
                @DisplayName("Should cancel booking and restore seats")
                void cancelBooking_Success() {
                        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
                        when(userService.getCurrentUser()).thenReturn(testUser);
                        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

                        bookingService.cancelBooking(1L);

                        verify(tripService).saveTrip(any(Trip.class));
                        verify(emailService).sendBookingCancellationEmail(any(Booking.class));
                }

        @Test
        @DisplayName("Should throw exception for non-existent booking")
        void cancelBooking_NotFound() {
            when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.cancelBooking(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
        }

        @Nested
        @DisplayName("Get Bookings")
        class GetBookingsTests {

        @Test
        @DisplayName("Should return user's bookings")
        void getMyBookings() {
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(bookingRepository.findByUserId(1L)).thenReturn(List.of(testBooking));

            List<BookingResponse> bookings = bookingService.getMyBookings();

            assertThat(bookings).hasSize(1);
            assertThat(bookings.get(0).getTripTitle()).isEqualTo("Goa Trip");
        }
        }
}

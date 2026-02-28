package com.travelmateai.backend.service;

import com.travelmateai.backend.dto.request.BookingRequest;
import com.travelmateai.backend.dto.response.BookingResponse;
import com.travelmateai.backend.entity.Booking;
import com.travelmateai.backend.entity.BookingStatus;
import com.travelmateai.backend.entity.Role;
import com.travelmateai.backend.entity.Trip;
import com.travelmateai.backend.entity.User;
import com.travelmateai.backend.exception.BadRequestException;
import com.travelmateai.backend.exception.InsufficientSeatsException;
import com.travelmateai.backend.exception.ResourceNotFoundException;
import com.travelmateai.backend.exception.UnauthorizedException;
import com.travelmateai.backend.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for booking-related operations.
 * Handles booking creation, cancellation, and retrieval.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripService tripService;
    private final UserService userService;
    private final EmailService emailService;

    /**
     * Create a new booking
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // Get current logged-in user
        User user = userService.getCurrentUser();

        // Get trip
        Trip trip = tripService.getTripEntityById(request.getTripId());

        // Check if user already has an active booking for this trip
        if (bookingRepository.existsByUserIdAndTripIdAndStatus(
                user.getId(), trip.getId(), BookingStatus.CONFIRMED)) {
            throw new BadRequestException("You already have an active booking for this trip");
        }

        // Check seat availability
        if (!trip.hasAvailableSeats(request.getNumberOfSeats())) {
            throw new InsufficientSeatsException(request.getNumberOfSeats(), trip.getAvailableSeats());
        }

        // Create booking
        Booking booking = Booking.builder()
                .user(user)
                .trip(trip)
                .numberOfSeats(request.getNumberOfSeats())
                .status(BookingStatus.CONFIRMED)
                .build();

        // Calculate total price
        booking.calculateTotalPrice();

        // Reduce available seats
        trip.reduceSeats(request.getNumberOfSeats());
        tripService.saveTrip(trip);

        // Save booking
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created: {} by user: {}", savedBooking.getId(), user.getEmail());

        // Send confirmation email (async - won't block)
        emailService.sendBookingConfirmationEmail(savedBooking);

        return BookingResponse.fromEntity(savedBooking);
    }

    /**
     * Get booking by ID
     */
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        // Check authorization
        checkBookingAuthorization(booking);

        return BookingResponse.fromEntity(booking);
    }

    /**
     * Get all bookings for a user
     */
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        // Check authorization
        User currentUser = userService.getCurrentUser();
        if (!currentUser.getId().equals(userId) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You are not authorized to view these bookings");
        }

        return bookingRepository.findByUserId(userId)
                .stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get bookings for current user
     */
    public List<BookingResponse> getMyBookings() {
        User currentUser = userService.getCurrentUser();
        return bookingRepository.findByUserId(currentUser.getId())
                .stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Cancel a booking
     */
    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        // Check authorization
        checkBookingAuthorization(booking);

        // Check if already cancelled
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);

        // Restore available seats
        Trip trip = booking.getTrip();
        trip.increaseSeats(booking.getNumberOfSeats());
        tripService.saveTrip(trip);

        // Save updated booking
        Booking cancelledBooking = bookingRepository.save(booking);
        log.info("Booking cancelled: {}", bookingId);

        // Send cancellation email (async)
        emailService.sendBookingCancellationEmail(cancelledBooking);

        return BookingResponse.fromEntity(cancelledBooking);
    }

    /**
     * Check if current user is authorized to access a booking
     */
    private void checkBookingAuthorization(Booking booking) {
        User currentUser = userService.getCurrentUser();
        if (!booking.getUser().getId().equals(currentUser.getId()) 
                && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You are not authorized to access this booking");
        }
    }
}

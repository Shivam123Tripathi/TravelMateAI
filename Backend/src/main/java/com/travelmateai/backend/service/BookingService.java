package com.travelmateai.backend.service;

import com.travelmateai.backend.dto.request.BookingRequest;
import com.travelmateai.backend.dto.response.BookingResponse;
import com.travelmateai.backend.entity.Booking;
import com.travelmateai.backend.entity.BookingStatus;
import com.travelmateai.backend.entity.Role;
import com.travelmateai.backend.entity.SeatLock;
import com.travelmateai.backend.entity.SeatLockStatus;
import com.travelmateai.backend.entity.Trip;
import com.travelmateai.backend.entity.User;
import com.travelmateai.backend.exception.BadRequestException;
import com.travelmateai.backend.exception.InsufficientSeatsException;
import com.travelmateai.backend.exception.ResourceNotFoundException;
import com.travelmateai.backend.exception.UnauthorizedException;
import com.travelmateai.backend.repository.BookingRepository;
import com.travelmateai.backend.repository.SeatLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for booking-related operations.
 * Handles booking creation, cancellation, and retrieval.
 * Integrates with the seat locking system for temporary seat reservations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripService tripService;
    private final UserService userService;
    private final EmailService emailService;
    private final SeatLockRepository seatLockRepository;

    /**
     * Create a new booking.
     * 
     * This method supports two booking paths:
     * 1. Direct booking (backward compatibility): If no seat lock exists, creates booking directly
     * 2. Seat-locked booking (recommended): If a valid seat lock exists for user and trip,
     *    confirms the lock and converts it to a booking
     * 
     * For production use, it's recommended to:
     * 1. Call /api/seat-locks to lock seats first
     * 2. Call /api/bookings with the same tripId (the method will find the active lock)
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

        // Check if user has an active seat lock for this trip
        Optional<SeatLock> existingLock = seatLockRepository.findActiveLockByUserAndTrip(
                user.getId(), trip.getId(), SeatLockStatus.ACTIVE);

        Booking booking;

        if (existingLock.isPresent()) {
            // Path 1: Use existing seat lock
            SeatLock seatLock = existingLock.get();

            // Verify the lock is still active (not expired)
            if (seatLock.isExpired()) {
                seatLock.setStatus(SeatLockStatus.EXPIRED);
                seatLockRepository.save(seatLock);
                throw new BadRequestException("Your seat lock has expired. Please lock seats again.");
            }

            // Verify the requested seats match the lock or are less
            if (request.getNumberOfSeats() > seatLock.getNumberOfSeats()) {
                throw new BadRequestException(
                        "You requested " + request.getNumberOfSeats() + " seats but only "
                        + seatLock.getNumberOfSeats() + " are locked. Please lock more seats.");
            }

            // Create booking from seat lock
            booking = Booking.builder()
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
            log.info("Booking created from seat lock: {} by user: {} (lock: {})",
                    savedBooking.getId(), user.getEmail(), seatLock.getId());

            // Convert lock status to CONFIRMED
            seatLock.setStatus(SeatLockStatus.CONFIRMED);
            seatLockRepository.save(seatLock);

            // Send confirmation email (async - won't block)
            emailService.sendBookingConfirmationEmail(savedBooking);

            return BookingResponse.fromEntity(savedBooking);
        } else {
            // Path 2: Direct booking (backward compatibility)
            // This allows users to book directly without locking seats first
            // Check seat availability (including locked seats)
            int effectiveAvailableSeats = trip.getAvailableSeats() - 
                    seatLockRepository.sumLockedSeatsByTrip(trip.getId(), SeatLockStatus.ACTIVE);

            if (effectiveAvailableSeats < request.getNumberOfSeats()) {
                throw new InsufficientSeatsException(request.getNumberOfSeats(), effectiveAvailableSeats);
            }

            // Create booking directly
            booking = Booking.builder()
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
            log.info("Direct booking created: {} by user: {}", savedBooking.getId(), user.getEmail());

            // Send confirmation email (async - won't block)
            emailService.sendBookingConfirmationEmail(savedBooking);

            return BookingResponse.fromEntity(savedBooking);
        }
    }

    /**
     * Get booking by ID
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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

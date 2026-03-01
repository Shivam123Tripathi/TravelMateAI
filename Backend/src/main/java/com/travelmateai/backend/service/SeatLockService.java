package com.travelmateai.backend.service;

import com.travelmateai.backend.dto.request.SeatLockRequest;
import com.travelmateai.backend.dto.response.SeatLockResponse;
import com.travelmateai.backend.entity.SeatLock;
import com.travelmateai.backend.entity.SeatLockStatus;
import com.travelmateai.backend.entity.Trip;
import com.travelmateai.backend.entity.User;
import com.travelmateai.backend.exception.BadRequestException;
import com.travelmateai.backend.exception.InsufficientSeatsException;
import com.travelmateai.backend.exception.ResourceNotFoundException;
import com.travelmateai.backend.repository.SeatLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for seat lock operations.
 * Manages temporary seat reservations with automatic expiration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SeatLockService {

    private final SeatLockRepository seatLockRepository;
    private final TripService tripService;
    private final UserService userService;

    @Value("${seat.lock.duration-minutes:5}")
    private Integer lockDurationMinutes;

    /**
     * Lock seats for a user on a specific trip.
     * Prevents other users from booking the same seats within the lock duration.
     *
     * @param request SeatLockRequest containing tripId and numberOfSeats
     * @return SeatLockResponse with lock details
     * @throws BadRequestException if user already has an active lock for this trip
     * @throws InsufficientSeatsException if not enough seats available
     */
    @Transactional
    public SeatLockResponse lockSeats(SeatLockRequest request) {
        User user = userService.getCurrentUser();
        Trip trip = tripService.getTripEntityById(request.getTripId());

        // Check if user already has an active lock for this trip
        Optional<SeatLock> existingLock = seatLockRepository.findActiveLockByUserAndTrip(
                user.getId(), trip.getId(), SeatLockStatus.ACTIVE);

        if (existingLock.isPresent()) {
            throw new BadRequestException(
                    "You already have an active seat lock for this trip. " +
                    "Please confirm the booking or wait for the lock to expire.");
        }

        // Calculate effective available seats (total - confirmed bookings - active locks)
        int effectiveAvailableSeats = calculateEffectiveAvailableSeats(trip);

        // Check if enough seats are available
        if (effectiveAvailableSeats < request.getNumberOfSeats()) {
            throw new InsufficientSeatsException(request.getNumberOfSeats(), effectiveAvailableSeats);
        }

        // Create seat lock
        LocalDateTime lockTime = LocalDateTime.now();
        LocalDateTime expiryTime = lockTime.plusMinutes(lockDurationMinutes);

        SeatLock seatLock = SeatLock.builder()
                .user(user)
                .trip(trip)
                .numberOfSeats(request.getNumberOfSeats())
                .lockTime(lockTime)
                .expiryTime(expiryTime)
                .status(SeatLockStatus.ACTIVE)
                .build();

        SeatLock savedLock = seatLockRepository.save(seatLock);
        log.info("Seat lock created: id={}, user={}, trip={}, seats={}, expiry={}",
                savedLock.getId(), user.getEmail(), trip.getId(),
                request.getNumberOfSeats(), expiryTime);

        return SeatLockResponse.fromEntity(savedLock);
    }

    /**
     * Confirm a seat lock by converting it to a permanent booking.
     * This method is called after the user completes the booking process.
     *
     * @param lockId The ID of the seat lock to confirm
     * @return SeatLockResponse with confirmed lock details
     */
    @Transactional
    public SeatLockResponse confirmLock(Long lockId) {
        SeatLock seatLock = seatLockRepository.findById(lockId)
                .orElseThrow(() -> new ResourceNotFoundException("SeatLock", "id", lockId));

        // Check authorization (only the lock owner can confirm)
        User currentUser = userService.getCurrentUser();
        if (!seatLock.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You are not authorized to confirm this lock");
        }

        // Check if lock is still active
        if (seatLock.getStatus() != SeatLockStatus.ACTIVE) {
            throw new BadRequestException(
                    "This lock is no longer active. Status: " + seatLock.getStatus().name());
        }

        // Check if lock has expired
        if (seatLock.isExpired()) {
            seatLock.setStatus(SeatLockStatus.EXPIRED);
            seatLockRepository.save(seatLock);
            throw new BadRequestException("This lock has expired. Please create a new lock.");
        }

        // Update lock status to CONFIRMED
        seatLock.setStatus(SeatLockStatus.CONFIRMED);
        SeatLock confirmedLock = seatLockRepository.save(seatLock);
        log.info("Seat lock confirmed: id={}", lockId);

        return SeatLockResponse.fromEntity(confirmedLock);
    }

    /**
     * Release expired seat locks.
     * This method is called by the scheduler periodically.
     */
    @Transactional
    public void releaseExpiredLocks() {
        List<SeatLock> expiredLocks = seatLockRepository.findExpiredLocks(
                SeatLockStatus.ACTIVE, LocalDateTime.now());

        if (!expiredLocks.isEmpty()) {
            expiredLocks.forEach(lock -> {
                lock.setStatus(SeatLockStatus.EXPIRED);
            });
            seatLockRepository.saveAll(expiredLocks);
            log.info("Released {} expired seat locks", expiredLocks.size());
        }
    }

    /**
     * Get all seat locks for the current user.
     *
     * @return List of SeatLockResponse objects
     */
    @Transactional(readOnly = true)
    public List<SeatLockResponse> getMyLocks() {
        User currentUser = userService.getCurrentUser();
        return seatLockRepository.findByUserId(currentUser.getId())
                .stream()
                .map(SeatLockResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all active seat locks for the current user.
     *
     * @return List of SeatLockResponse objects with ACTIVE status
     */
    @Transactional(readOnly = true)
    public List<SeatLockResponse> getMyActiveLocks() {
        User currentUser = userService.getCurrentUser();
        return seatLockRepository.findByUserIdAndStatus(currentUser.getId(), SeatLockStatus.ACTIVE)
                .stream()
                .filter(SeatLock::isActive)
                .map(SeatLockResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific seat lock by ID.
     *
     * @param lockId The ID of the seat lock
     * @return SeatLockResponse
     */
    @Transactional(readOnly = true)
    public SeatLockResponse getLockById(Long lockId) {
        SeatLock seatLock = seatLockRepository.findById(lockId)
                .orElseThrow(() -> new ResourceNotFoundException("SeatLock", "id", lockId));

        // Check authorization
        User currentUser = userService.getCurrentUser();
        if (!seatLock.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You are not authorized to view this lock");
        }

        return SeatLockResponse.fromEntity(seatLock);
    }

    /**
     * Calculate effective available seats for a trip.
     * Formula: trip.availableSeats - sum(active locks for trip)
     *
     * @param trip The trip to calculate for
     * @return Number of effectively available seats
     */
    public int calculateEffectiveAvailableSeats(Trip trip) {
        Integer lockedSeats = seatLockRepository.sumLockedSeatsByTrip(trip.getId(), SeatLockStatus.ACTIVE);
        if (lockedSeats == null) {
            lockedSeats = 0;
        }
        return trip.getAvailableSeats() - lockedSeats;
    }

    /**
     * Release a specific seat lock (mark as expired).
     * Internal method used by scheduler or booking service.
     *
     * @param lockId The ID of the seat lock to release
     */
    @Transactional
    public void releaseLock(Long lockId) {
        seatLockRepository.findById(lockId).ifPresent(lock -> {
            lock.setStatus(SeatLockStatus.EXPIRED);
            seatLockRepository.save(lock);
            log.info("Seat lock released: id={}", lockId);
        });
    }

    /**
     * Get all active locks for a specific trip.
     * Used internally to check seat availability.
     *
     * @param tripId The trip ID
     * @return List of active SeatLock objects
     */
    public List<SeatLock> getActiveLocksByTrip(Long tripId) {
        return seatLockRepository.findActiveLocksByTrip(tripId, SeatLockStatus.ACTIVE);
    }
}

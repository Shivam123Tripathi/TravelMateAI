package com.travelmateai.backend.repository;

import com.travelmateai.backend.entity.SeatLock;
import com.travelmateai.backend.entity.SeatLockStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for SeatLock entity.
 * Provides database operations for seat locks.
 */
@Repository
public interface SeatLockRepository extends JpaRepository<SeatLock, Long> {

    /**
     * Find all active locks for a specific trip.
     * Used to calculate effective available seats.
     */
    @Query("SELECT sl FROM SeatLock sl WHERE sl.trip.id = :tripId AND sl.status = :status")
    List<SeatLock> findActiveLocksByTrip(@Param("tripId") Long tripId, @Param("status") SeatLockStatus status);

    /**
     * Find all expired locks that need to be released.
     */
    @Query("SELECT sl FROM SeatLock sl WHERE sl.status = :status AND sl.expiryTime <= :now")
    List<SeatLock> findExpiredLocks(@Param("status") SeatLockStatus status, @Param("now") LocalDateTime now);

    /**
     * Find active lock for a specific user and trip combination.
     */
    @Query("SELECT sl FROM SeatLock sl WHERE sl.user.id = :userId AND sl.trip.id = :tripId AND sl.status = :status")
    Optional<SeatLock> findActiveLockByUserAndTrip(
            @Param("userId") Long userId,
            @Param("tripId") Long tripId,
            @Param("status") SeatLockStatus status);

    /**
     * Find all locks for a user.
     */
    List<SeatLock> findByUserId(Long userId);

    /**
     * Find all locks for a user with specific status.
     */
    List<SeatLock> findByUserIdAndStatus(Long userId, SeatLockStatus status);

    /**
     * Delete all seat locks for a user (used during account deletion).
     */
    void deleteByUserId(Long userId);

    /**
     * Count active locks for a trip.
     */
    @Query("SELECT COUNT(sl) FROM SeatLock sl WHERE sl.trip.id = :tripId AND sl.status = :status")
    Long countActiveLocksByTrip(@Param("tripId") Long tripId, @Param("status") SeatLockStatus status);

    /**
     * Sum of all seats locked for a trip.
     */
    @Query("SELECT COALESCE(SUM(sl.numberOfSeats), 0) FROM SeatLock sl WHERE sl.trip.id = :tripId AND sl.status = :status")
    Integer sumLockedSeatsByTrip(@Param("tripId") Long tripId, @Param("status") SeatLockStatus status);

    /**
     * Delete all seat locks for a trip (used during trip deletion after safety
     * checks).
     */
    void deleteByTripId(Long tripId);
}

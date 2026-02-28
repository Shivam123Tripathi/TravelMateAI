package com.travelmateai.backend.repository;

import com.travelmateai.backend.entity.Booking;
import com.travelmateai.backend.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for Booking entity.
 * Provides database operations for bookings table.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Find all bookings by user ID.
     */
    List<Booking> findByUserId(Long userId);

    /**
     * Find all bookings by trip ID.
     */
    List<Booking> findByTripId(Long tripId);

    /**
     * Find bookings by user ID and status.
     */
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

    /**
     * Count total bookings (confirmed only).
     */
    Long countByStatus(BookingStatus status);

    /**
     * Calculate total revenue from confirmed bookings.
     */
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status = :status")
    BigDecimal calculateTotalRevenue(@Param("status") BookingStatus status);

    /**
     * Count bookings per destination for reports.
     */
    @Query("SELECT b.trip.destination, COUNT(b) FROM Booking b WHERE b.status = :status GROUP BY b.trip.destination ORDER BY COUNT(b) DESC")
    List<Object[]> countBookingsByDestination(@Param("status") BookingStatus status);

    /**
     * Check if user has already booked a specific trip.
     */
    boolean existsByUserIdAndTripIdAndStatus(Long userId, Long tripId, BookingStatus status);
}

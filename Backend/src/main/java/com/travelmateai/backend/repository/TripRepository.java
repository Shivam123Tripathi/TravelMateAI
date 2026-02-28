package com.travelmateai.backend.repository;

import com.travelmateai.backend.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Trip entity.
 * Provides database operations for trips table.
 */
@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    /**
     * Find trips by destination (case-insensitive partial match).
     */
    @Query("SELECT t FROM Trip t WHERE LOWER(t.destination) LIKE LOWER(CONCAT('%', :destination, '%'))")
    List<Trip> searchByDestination(@Param("destination") String destination);

    /**
     * Find trips with pagination and sorting.
     */
    Page<Trip> findAll(Pageable pageable);

    /**
     * Find trips by destination with pagination.
     */
    @Query("SELECT t FROM Trip t WHERE LOWER(t.destination) LIKE LOWER(CONCAT('%', :destination, '%'))")
    Page<Trip> searchByDestinationPaginated(@Param("destination") String destination, Pageable pageable);

    /**
     * Find trips with available seats.
     */
    List<Trip> findByAvailableSeatsGreaterThan(Integer seats);

    /**
     * Get most popular destination (most booked).
     */
    @Query("SELECT t.destination, COUNT(b) as bookingCount FROM Trip t JOIN Booking b ON b.trip.id = t.id GROUP BY t.destination ORDER BY bookingCount DESC")
    List<Object[]> findMostPopularDestinations();
}

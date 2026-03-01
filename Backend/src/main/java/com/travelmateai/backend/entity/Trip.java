package com.travelmateai.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Trip Entity - Represents the 'trips' table in MySQL database.
 * Contains travel package information managed by admins.
 */
@Entity
@Table(name = "trips", indexes = {
    @Index(name = "idx_trip_destination", columnList = "destination"),
    @Index(name = "idx_trip_price", columnList = "price")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "destination", nullable = false, length = 100)
    private String destination;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration", nullable = false)
    private Integer duration; // Duration in days

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Version
    @Column(name = "version")
    @Builder.Default
    private Long version = 0L;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Check if seats are available for booking
     */
    public boolean hasAvailableSeats(int requestedSeats) {
        return this.availableSeats >= requestedSeats;
    }

    /**
     * Reduce available seats after booking.
     * Throws IllegalStateException if insufficient seats.
     */
    public void reduceSeats(int seats) {
        if (this.availableSeats < seats) {
            throw new IllegalStateException(
                    "Cannot reduce seats: requested " + seats + ", available " + this.availableSeats);
        }
        this.availableSeats -= seats;
    }

    /**
     * Increase available seats after cancellation.
     * Throws IllegalStateException if result exceeds total seats.
     */
    public void increaseSeats(int seats) {
        if (this.availableSeats + seats > this.totalSeats) {
            throw new IllegalStateException(
                    "Cannot increase seats: would exceed total seats (" + this.totalSeats + ")");
        }
        this.availableSeats += seats;
    }
}

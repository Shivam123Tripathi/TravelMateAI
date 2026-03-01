package com.travelmateai.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * SeatLock Entity - Represents temporary seat reservations.
 * Seats are locked for a configurable duration before being released or confirmed as bookings.
 */
@Entity
@Table(name = "seat_locks", indexes = {
    @Index(name = "idx_seat_lock_user", columnList = "user_id"),
    @Index(name = "idx_seat_lock_trip", columnList = "trip_id"),
    @Index(name = "idx_seat_lock_status", columnList = "status"),
    @Index(name = "idx_seat_lock_expiry", columnList = "expiry_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many locks can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many locks can be for one trip
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "number_of_seats", nullable = false)
    private Integer numberOfSeats;

    @Column(name = "lock_time", nullable = false, updatable = false)
    private LocalDateTime lockTime;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private SeatLockStatus status = SeatLockStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Check if this lock is still active (not expired)
     */
    public boolean isActive() {
        return this.status == SeatLockStatus.ACTIVE && LocalDateTime.now().isBefore(this.expiryTime);
    }

    /**
     * Check if this lock has expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryTime);
    }
}

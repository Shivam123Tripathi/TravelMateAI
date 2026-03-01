package com.travelmateai.backend.entity;

/**
 * Enum representing the status of a seat lock.
 */
public enum SeatLockStatus {
    ACTIVE,      // Lock is active and seats are temporarily reserved
    EXPIRED,     // Lock has expired and seats have been released
    CONFIRMED    // Lock has been converted to a confirmed booking
}

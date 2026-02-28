package com.travelmateai.backend.entity;

/**
 * Enum representing user roles in the system.
 * Used for role-based access control (RBAC).
 */
public enum Role {
    USER,   // Regular user - can book trips, view profile
    ADMIN   // Admin user - can manage trips, view reports
}

package com.travelmateai.backend.repository;

import com.travelmateai.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity.
 * Provides database operations for users table.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address.
     * Used for authentication and profile management.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email already exists in database.
     * Used during registration to prevent duplicates.
     */
    boolean existsByEmail(String email);
}

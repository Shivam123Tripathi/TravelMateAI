package com.travelmateai.backend.service;

import com.travelmateai.backend.dto.request.LoginRequest;
import com.travelmateai.backend.dto.request.RegisterRequest;
import com.travelmateai.backend.dto.request.UpdateUserRequest;
import com.travelmateai.backend.dto.response.AuthResponse;
import com.travelmateai.backend.dto.response.UserResponse;
import com.travelmateai.backend.entity.Role;
import com.travelmateai.backend.entity.User;
import com.travelmateai.backend.exception.DuplicateResourceException;
import com.travelmateai.backend.exception.ResourceNotFoundException;
import com.travelmateai.backend.exception.UnauthorizedException;
import com.travelmateai.backend.repository.BookingRepository;
import com.travelmateai.backend.repository.SeatLockRepository;
import com.travelmateai.backend.repository.UserRepository;
import com.travelmateai.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user-related operations.
 * Handles registration, authentication, and profile management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final SeatLockRepository seatLockRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user
     */
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Create new user entity
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.USER)
                .build();

        // Save to database
        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());

        return UserResponse.fromEntity(savedUser);
    }

    /**
     * Authenticate user and return JWT token
     */
    public AuthResponse loginUser(LoginRequest request) {
        // Authenticate using Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Get authenticated user details
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Find user entity
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        // Generate JWT token
        String token = jwtUtil.generateToken(userDetails);

        log.info("User logged in: {}", user.getEmail());

        return AuthResponse.of(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    /**
     * Get user profile by ID
     */
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if current user is authorized to view this profile
        checkUserAuthorization(userId);

        return UserResponse.fromEntity(user);
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        // Check authorization
        checkUserAuthorization(userId);

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Update fields if provided
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Save updated user
        User updatedUser = userRepository.save(user);
        log.info("User profile updated: {}", updatedUser.getEmail());

        return UserResponse.fromEntity(updatedUser);
    }

    /**
     * Delete user account
     */
    @Transactional
    public void deleteUser(Long userId) {
        // Check authorization
        checkUserAuthorization(userId);

        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        // Delete child records first to avoid FK constraint violations
        seatLockRepository.deleteByUserId(userId);
        bookingRepository.deleteByUserId(userId);

        userRepository.deleteById(userId);
        log.info("User deleted: {}", userId);
    }

    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    /**
     * Check if current user is authorized to access/modify a resource
     */
    private void checkUserAuthorization(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        // Allow if user is accessing their own data OR if user is ADMIN
        if (!currentUser.getId().equals(userId) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You are not authorized to access this resource");
        }
    }
}

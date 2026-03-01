package com.travelmateai.backend.controller;

import com.travelmateai.backend.dto.request.BookingRequest;
import com.travelmateai.backend.dto.response.ApiResponse;
import com.travelmateai.backend.dto.response.BookingResponse;
import com.travelmateai.backend.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Booking operations.
 * Handles trip bookings for authenticated users.
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Create a new booking
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request) {

        BookingResponse booking = bookingService.createBooking(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created successfully", booking));
    }

    /**
     * Get booking by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id) {

        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success("Booking retrieved successfully", booking));
    }

    /**
     * Get all bookings for a specific user (Admin or own bookings)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByUserId(
            @PathVariable Long userId) {

        List<BookingResponse> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Bookings retrieved successfully", bookings));
    }

    /**
     * Get my bookings (for current logged-in user)
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings() {

        List<BookingResponse> bookings = bookingService.getMyBookings();
        return ResponseEntity.ok(ApiResponse.success("Your bookings retrieved successfully", bookings));
    }

    /**
     * Cancel a booking
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(@PathVariable Long id) {

        BookingResponse booking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", booking));
    }
}

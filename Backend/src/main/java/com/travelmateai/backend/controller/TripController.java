package com.travelmateai.backend.controller;

import com.travelmateai.backend.dto.request.TripRequest;
import com.travelmateai.backend.dto.response.ApiResponse;
import com.travelmateai.backend.dto.response.TripResponse;
import com.travelmateai.backend.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Trip operations.
 * Public: GET operations
 * Admin only: POST, PUT, DELETE operations
 */
@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Tag(name = "Trip", description = "Trip management APIs")
public class TripController {

    private final TripService tripService;

    /**
     * Create a new trip (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create trip", description = "Create a new trip (Admin only)")
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(
            @Valid @RequestBody TripRequest request) {

        TripResponse trip = tripService.createTrip(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Trip created successfully", trip));
    }

    /**
     * Get all trips with pagination (Public)
     */
    @GetMapping
    @Operation(summary = "Get all trips", description = "Get all trips with pagination")
    public ResponseEntity<ApiResponse<Page<TripResponse>>> getAllTrips(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        Page<TripResponse> trips = tripService.getAllTrips(pageable);
        return ResponseEntity.ok(ApiResponse.success("Trips retrieved successfully", trips));
    }

    /**
     * Get trip by ID (Public)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get trip by ID", description = "Get trip details by ID")
    public ResponseEntity<ApiResponse<TripResponse>> getTripById(@PathVariable Long id) {

        TripResponse trip = tripService.getTripById(id);
        return ResponseEntity.ok(ApiResponse.success("Trip retrieved successfully", trip));
    }

    /**
     * Search trips by destination (Public)
     */
    @GetMapping("/search")
    @Operation(summary = "Search trips", description = "Search trips by destination")
    public ResponseEntity<ApiResponse<List<TripResponse>>> searchTrips(
            @RequestParam String destination) {

        List<TripResponse> trips = tripService.searchByDestination(destination);
        return ResponseEntity.ok(ApiResponse.success("Search results", trips));
    }

    /**
     * Update trip (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update trip", description = "Update trip details (Admin only)")
    public ResponseEntity<ApiResponse<TripResponse>> updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody TripRequest request) {

        TripResponse trip = tripService.updateTrip(id, request);
        return ResponseEntity.ok(ApiResponse.success("Trip updated successfully", trip));
    }

    /**
     * Delete trip (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete trip", description = "Delete a trip (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteTrip(@PathVariable Long id) {

        tripService.deleteTrip(id);
        return ResponseEntity.ok(ApiResponse.success("Trip deleted successfully"));
    }
}

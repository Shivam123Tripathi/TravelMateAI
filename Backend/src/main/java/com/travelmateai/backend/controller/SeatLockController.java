package com.travelmateai.backend.controller;

import com.travelmateai.backend.dto.request.SeatLockRequest;
import com.travelmateai.backend.dto.response.ApiResponse;
import com.travelmateai.backend.dto.response.SeatLockResponse;
import com.travelmateai.backend.service.SeatLockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Seat Lock operations.
 * Handles temporary seat reservations before booking confirmation.
 */
@RestController
@RequestMapping("/api/seat-locks")
@RequiredArgsConstructor
public class SeatLockController {

    private final SeatLockService seatLockService;

    /**
     * Lock seats for a trip
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SeatLockResponse>> lockSeats(
            @Valid @RequestBody SeatLockRequest request) {

        SeatLockResponse lock = seatLockService.lockSeats(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Seats locked successfully", lock));
    }

    /**
     * Confirm a seat lock by converting it to a permanent booking
     */
    @PostMapping("/{lockId}/confirm")
    public ResponseEntity<ApiResponse<SeatLockResponse>> confirmLock(@PathVariable Long lockId) {

        SeatLockResponse lock = seatLockService.confirmLock(lockId);
        return ResponseEntity
                .ok(ApiResponse.success("Seat lock confirmed successfully", lock));
    }

    /**
     * Get my seat locks
     */
    @GetMapping("/my-locks")
    public ResponseEntity<ApiResponse<List<SeatLockResponse>>> getMyLocks() {

        List<SeatLockResponse> locks = seatLockService.getMyLocks();
        return ResponseEntity
                .ok(ApiResponse.success("Your seat locks retrieved successfully", locks));
    }

    /**
     * Get my active seat locks
     */
    @GetMapping("/my-active-locks")
    public ResponseEntity<ApiResponse<List<SeatLockResponse>>> getMyActiveLocks() {

        List<SeatLockResponse> locks = seatLockService.getMyActiveLocks();
        return ResponseEntity
                .ok(ApiResponse.success("Your active seat locks retrieved successfully", locks));
    }

    /**
     * Get a specific seat lock by ID
     */
    @GetMapping("/{lockId}")
    public ResponseEntity<ApiResponse<SeatLockResponse>> getLockById(@PathVariable Long lockId) {

        SeatLockResponse lock = seatLockService.getLockById(lockId);
        return ResponseEntity
                .ok(ApiResponse.success("Seat lock retrieved successfully", lock));
    }
}

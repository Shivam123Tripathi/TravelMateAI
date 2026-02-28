package com.travelmateai.backend.service;

import com.travelmateai.backend.dto.request.TripRequest;
import com.travelmateai.backend.dto.response.TripResponse;
import com.travelmateai.backend.entity.Trip;
import com.travelmateai.backend.exception.ResourceNotFoundException;
import com.travelmateai.backend.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for trip-related operations.
 * Handles CRUD operations for travel packages.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final TripRepository tripRepository;

    /**
     * Create a new trip (Admin only)
     */
    @Transactional
    public TripResponse createTrip(TripRequest request) {
        Trip trip = Trip.builder()
                .title(request.getTitle())
                .destination(request.getDestination())
                .description(request.getDescription())
                .price(request.getPrice())
                .duration(request.getDuration())
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats()) // Initially all seats are available
                .imageUrl(request.getImageUrl())
                .build();

        Trip savedTrip = tripRepository.save(trip);
        log.info("New trip created: {}", savedTrip.getTitle());

        return TripResponse.fromEntity(savedTrip);
    }

    /**
     * Get all trips with pagination
     */
    public Page<TripResponse> getAllTrips(Pageable pageable) {
        return tripRepository.findAll(pageable)
                .map(TripResponse::fromEntity);
    }

    /**
     * Get trip by ID
     */
    public TripResponse getTripById(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));
        return TripResponse.fromEntity(trip);
    }

    /**
     * Search trips by destination
     */
    public List<TripResponse> searchByDestination(String destination) {
        return tripRepository.searchByDestination(destination)
                .stream()
                .map(TripResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Search trips by destination with pagination
     */
    public Page<TripResponse> searchByDestinationPaginated(String destination, Pageable pageable) {
        return tripRepository.searchByDestinationPaginated(destination, pageable)
                .map(TripResponse::fromEntity);
    }

    /**
     * Update trip (Admin only)
     */
    @Transactional
    public TripResponse updateTrip(Long tripId, TripRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));

        // Calculate available seats adjustment if total seats changed
        int seatsDifference = request.getTotalSeats() - trip.getTotalSeats();
        int newAvailableSeats = trip.getAvailableSeats() + seatsDifference;

        // Ensure available seats doesn't go negative
        newAvailableSeats = Math.max(0, newAvailableSeats);

        trip.setTitle(request.getTitle());
        trip.setDestination(request.getDestination());
        trip.setDescription(request.getDescription());
        trip.setPrice(request.getPrice());
        trip.setDuration(request.getDuration());
        trip.setTotalSeats(request.getTotalSeats());
        trip.setAvailableSeats(newAvailableSeats);
        trip.setImageUrl(request.getImageUrl());

        Trip updatedTrip = tripRepository.save(trip);
        log.info("Trip updated: {}", updatedTrip.getTitle());

        return TripResponse.fromEntity(updatedTrip);
    }

    /**
     * Delete trip (Admin only)
     */
    @Transactional
    public void deleteTrip(Long tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new ResourceNotFoundException("Trip", "id", tripId);
        }
        tripRepository.deleteById(tripId);
        log.info("Trip deleted: {}", tripId);
    }

    /**
     * Get trip entity by ID (internal use)
     */
    public Trip getTripEntityById(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));
    }

    /**
     * Save trip entity (internal use)
     */
    @Transactional
    public Trip saveTrip(Trip trip) {
        return tripRepository.save(trip);
    }
}

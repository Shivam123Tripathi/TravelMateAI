package com.travelmateai.backend.service;

import com.travelmateai.backend.dto.response.RecommendationResponse;
import com.travelmateai.backend.dto.response.TripResponse;
import com.travelmateai.backend.entity.Trip;
import com.travelmateai.backend.entity.User;
import com.travelmateai.backend.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for fetching trip recommendations from the ML microservice.
 *
 * Calls the Python Flask recommendation service, receives recommended trip IDs,
 * and enriches them with full trip details from the database.
 *
 * If the ML service is unavailable, returns an empty recommendation list
 * instead of throwing an exception (graceful degradation).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final RestTemplate restTemplate;
    private final TripRepository tripRepository;
    private final UserService userService;

    @Value("${recommendation.service.url:http://localhost:5000}")
    private String recommendationServiceUrl;

    /**
     * Get personalized trip recommendations for the currently logged-in user.
     *
     * @param topN Number of recommendations to return (default 3)
     * @return RecommendationResponse containing enriched trip details
     */
    public RecommendationResponse getRecommendations(int topN) {
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        List<Long> recommendedTripIds = fetchRecommendedTripIds(userId, topN);

        // If no recommendations available, return empty response
        if (recommendedTripIds.isEmpty()) {
            log.info("No recommendations available for user: {}", currentUser.getEmail());
            return RecommendationResponse.builder()
                    .userId(userId)
                    .recommendedTrips(Collections.emptyList())
                    .build();
        }

        // Fetch full trip details from database
        List<TripResponse> recommendedTrips = recommendedTripIds.stream()
                .map(tripId -> tripRepository.findById(tripId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(TripResponse::fromEntity)
                .collect(Collectors.toList());

        log.info("Returning {} recommendations for user: {}", recommendedTrips.size(), currentUser.getEmail());

        return RecommendationResponse.builder()
                .userId(userId)
                .recommendedTrips(recommendedTrips)
                .build();
    }

    /**
     * Call the ML microservice to get recommended trip IDs.
     * Returns an empty list if the service is unavailable (graceful degradation).
     */
    @SuppressWarnings("unchecked")
    private List<Long> fetchRecommendedTripIds(Long userId, int topN) {
        String url = recommendationServiceUrl + "/recommend";

        Map<String, Object> requestBody = Map.of(
                "user_id", userId,
                "top_n", topN);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Number> recommendations = (List<Number>) response.getBody().get("recommendations");

                if (recommendations != null) {
                    return recommendations.stream()
                            .map(Number::longValue)
                            .collect(Collectors.toList());
                }
            }

            log.warn("Unexpected response from ML service: status={}", response.getStatusCode());
            return Collections.emptyList();

        } catch (RestClientException ex) {
            log.warn("ML recommendation service unavailable: {}. Returning empty recommendations.", ex.getMessage());
            return Collections.emptyList();
        }
    }
}

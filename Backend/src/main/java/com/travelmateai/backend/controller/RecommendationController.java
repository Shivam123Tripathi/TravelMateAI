package com.travelmateai.backend.controller;

import com.travelmateai.backend.dto.response.ApiResponse;
import com.travelmateai.backend.dto.response.RecommendationResponse;
import com.travelmateai.backend.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for AI-powered trip recommendations.
 * Returns personalized trip suggestions for the authenticated user
 * based on collaborative filtering from the ML recommendation service.
 */
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Get personalized trip recommendations for the current user.
     *
     * @param topN Number of recommendations to return (default 3, max determined by
     *             ML model)
     * @return List of recommended trips with full details
     */
    @GetMapping
    public ResponseEntity<ApiResponse<RecommendationResponse>> getRecommendations(
            @RequestParam(defaultValue = "3") int topN) {

        RecommendationResponse recommendations = recommendationService.getRecommendations(topN);
        return ResponseEntity.ok(
                ApiResponse.success("Recommendations retrieved successfully", recommendations));
    }
}

package com.travelmateai.backend.service;

import com.travelmateai.backend.dto.response.RecommendationResponse;
import com.travelmateai.backend.entity.Role;
import com.travelmateai.backend.entity.Trip;
import com.travelmateai.backend.entity.User;
import com.travelmateai.backend.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecommendationService Tests")
class RecommendationServiceTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private RecommendationService recommendationService;

    private User testUser;
    private Trip tripGoa;
    private Trip tripManali;

    @BeforeEach
    void setUp() {
        // Set the ML service URL via reflection (same as @Value injection)
        ReflectionTestUtils.setField(recommendationService, "recommendationServiceUrl", "http://localhost:5000");

        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("encoded")
                .role(Role.USER)
                .build();

        tripGoa = Trip.builder()
                .id(1L)
                .title("Goa Beach Trip")
                .destination("Goa")
                .price(BigDecimal.valueOf(5000))
                .totalSeats(50)
                .availableSeats(30)
                .build();

        tripManali = Trip.builder()
                .id(3L)
                .title("Manali Adventure")
                .destination("Manali")
                .price(BigDecimal.valueOf(8000))
                .totalSeats(30)
                .availableSeats(15)
                .build();
    }

    @Nested
    @DisplayName("Get Recommendations")
    class GetRecommendationsTests {

        @Test
        @DisplayName("Should return recommendations when ML service responds successfully")
        void getRecommendations_Success() {
            // Arrange
            when(userService.getCurrentUser()).thenReturn(testUser);

            Map<String, Object> mlResponse = Map.of(
                    "user_id", 1,
                    "recommendations", List.of(1, 3)
            );
            when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenReturn(new ResponseEntity<>(mlResponse, HttpStatus.OK));

            when(tripRepository.findById(1L)).thenReturn(Optional.of(tripGoa));
            when(tripRepository.findById(3L)).thenReturn(Optional.of(tripManali));

            // Act
            RecommendationResponse response = recommendationService.getRecommendations(3);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getUserId()).isEqualTo(1L);
            assertThat(response.getRecommendedTrips()).hasSize(2);
            assertThat(response.getRecommendedTrips().get(0).getTitle()).isEqualTo("Goa Beach Trip");
            assertThat(response.getRecommendedTrips().get(1).getTitle()).isEqualTo("Manali Adventure");
        }

        @Test
        @DisplayName("Should return empty list when ML service is down (graceful degradation)")
        void getRecommendations_ServiceDown() {
            // Arrange
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenThrow(new RestClientException("Connection refused"));

            // Act
            RecommendationResponse response = recommendationService.getRecommendations(3);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getUserId()).isEqualTo(1L);
            assertThat(response.getRecommendedTrips()).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list when ML service returns null body")
        void getRecommendations_NullResponse() {
            // Arrange
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

            // Act
            RecommendationResponse response = recommendationService.getRecommendations(3);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getRecommendedTrips()).isEmpty();
        }

        @Test
        @DisplayName("Should skip trips not found in database")
        void getRecommendations_SkipsMissingTrips() {
            // Arrange
            when(userService.getCurrentUser()).thenReturn(testUser);

            Map<String, Object> mlResponse = Map.of(
                    "user_id", 1,
                    "recommendations", List.of(1, 999)  // trip 999 doesn't exist
            );
            when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenReturn(new ResponseEntity<>(mlResponse, HttpStatus.OK));

            when(tripRepository.findById(1L)).thenReturn(Optional.of(tripGoa));
            when(tripRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            RecommendationResponse response = recommendationService.getRecommendations(3);

            // Assert
            assertThat(response.getRecommendedTrips()).hasSize(1);
            assertThat(response.getRecommendedTrips().get(0).getTitle()).isEqualTo("Goa Beach Trip");
        }
    }
}

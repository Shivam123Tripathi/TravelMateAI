package com.travelmateai.backend.security;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtUtil Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Generate a proper 256-bit secret key for testing
        String secret = Base64.getEncoder().encodeToString(
                Keys.hmacShaKeyFor(new byte[32]).getEncoded());
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 7200000L); // 2 hours

        testUserDetails = new User("test@example.com", "password", Collections.emptyList());
    }

    @Nested
    @DisplayName("Token Generation")
    class TokenGenerationTests {

        @Test
        @DisplayName("Should generate a valid token")
        void generateToken_Success() {
            String token = jwtUtil.generateToken(testUserDetails);

            assertThat(token).isNotNull();
            assertThat(token).isNotBlank();
            assertThat(token.split("\\.")).hasSize(3); // Header.Payload.Signature
        }

        @Test
        @DisplayName("Should extract correct username from token")
        void extractUsername() {
            String token = jwtUtil.generateToken(testUserDetails);

            String username = jwtUtil.extractUsername(token);

            assertThat(username).isEqualTo("test@example.com");
        }
    }

    @Nested
    @DisplayName("Token Validation")
    class TokenValidationTests {

        @Test
        @DisplayName("Should validate correct token")
        void validateToken_Valid() {
            String token = jwtUtil.generateToken(testUserDetails);

            Boolean isValid = jwtUtil.validateToken(token, testUserDetails);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Should reject token with wrong username")
        void validateToken_WrongUser() {
            String token = jwtUtil.generateToken(testUserDetails);
            UserDetails otherUser = new User("other@example.com", "password", Collections.emptyList());

            Boolean isValid = jwtUtil.validateToken(token, otherUser);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should reject expired token")
        void validateToken_Expired() throws InterruptedException {
            // Set expiration to 1ms to force near-immediate expiry
            ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 1L);
            String token = jwtUtil.generateToken(testUserDetails);
            Thread.sleep(50); // Wait for token to expire

            // Expired tokens throw ExpiredJwtException when parsed
            Boolean isValid = jwtUtil.validateToken(token);
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should validate token structure and signature")
        void validateToken_StructureValid() {
            String token = jwtUtil.generateToken(testUserDetails);

            Boolean isValid = jwtUtil.validateToken(token);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Should reject invalid token format")
        void validateToken_InvalidFormat() {
            Boolean isValid = jwtUtil.validateToken("not-a-valid-token");

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should reject tampered token")
        void validateToken_TamperedToken() {
            String token = jwtUtil.generateToken(testUserDetails);
            // Tamper with the signature
            String tampered = token.substring(0, token.length() - 5) + "XXXXX";

            Boolean isValid = jwtUtil.validateToken(tampered);

            assertThat(isValid).isFalse();
        }
    }
}

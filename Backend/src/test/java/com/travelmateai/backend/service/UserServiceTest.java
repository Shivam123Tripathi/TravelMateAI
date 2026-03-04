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
import com.travelmateai.backend.repository.BookingRepository;
import com.travelmateai.backend.repository.SeatLockRepository;
import com.travelmateai.backend.repository.UserRepository;
import com.travelmateai.backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private SeatLockRepository seatLockRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .phone("9876543210")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        registerRequest = RegisterRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .phone("9876543210")
                .build();
    }

    /**
     * Helper to mock SecurityContext with a specific email.
     */
    private void mockSecurityContext(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register user successfully")
        void registerUser_Success() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            UserResponse response = userService.registerUser(registerRequest);

            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo("test@example.com");
            assertThat(response.getName()).isEqualTo("Test User");
            assertThat(response.getRole()).isEqualTo("USER");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception for duplicate email")
        void registerUser_DuplicateEmail() {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.registerUser(registerRequest))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully and return JWT token")
        void loginUser_Success() {
            LoginRequest loginRequest = LoginRequest.builder()
                    .email("test@example.com")
                    .password("password123")
                    .build();

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    "test@example.com", "encodedPassword",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

            Authentication auth = mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn(userDetails);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(auth);
            when(userRepository.findByEmail("test@example.com"))
                    .thenReturn(Optional.of(testUser));
            when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt-token-123");

            AuthResponse response = userService.loginUser(loginRequest);

            assertThat(response).isNotNull();
            assertThat(response.getToken()).isEqualTo("jwt-token-123");
            assertThat(response.getEmail()).isEqualTo("test@example.com");
            assertThat(response.getRole()).isEqualTo("USER");
        }

        @Test
        @DisplayName("Should throw exception for invalid credentials")
        void loginUser_InvalidCredentials() {
            LoginRequest loginRequest = LoginRequest.builder()
                    .email("test@example.com")
                    .password("wrongpassword")
                    .build();

            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> userService.loginUser(loginRequest))
                    .isInstanceOf(BadCredentialsException.class);
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should return user by ID")
        void getUserById_Success() {
            // Mock the security context so checkUserAuthorization passes
            mockSecurityContext("test@example.com");
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            UserResponse response = userService.getUserById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("Should throw exception for non-existent user")
        void getUserById_NotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user name successfully")
        void updateUser_Success() {
            UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                    .name("Updated Name")
                    .build();

            // Mock security context so checkUserAuthorization passes
            mockSecurityContext("test@example.com");
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            UserResponse response = userService.updateUser(1L, updateRequest);

            assertThat(response).isNotNull();
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user and related data")
        void deleteUser_Success() {
            mockSecurityContext("test@example.com");
            when(userRepository.existsById(1L)).thenReturn(true);
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            userService.deleteUser(1L);

            verify(seatLockRepository).deleteByUserId(1L);
            verify(bookingRepository).deleteByUserId(1L);
            verify(userRepository).deleteById(1L);
        }
    }
}

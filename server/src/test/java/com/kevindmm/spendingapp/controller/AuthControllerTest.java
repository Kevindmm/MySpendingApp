package com.kevindmm.spendingapp.controller;

import com.kevindmm.spendingapp.dto.LoginRequestDTO;
import com.kevindmm.spendingapp.dto.LoginResponseDTO;
import com.kevindmm.spendingapp.dto.RefreshTokenRequestDTO;
import com.kevindmm.spendingapp.dto.RefreshTokenResponseDTO;
import com.kevindmm.spendingapp.model.RefreshToken;
import com.kevindmm.spendingapp.model.User;
import com.kevindmm.spendingapp.repository.UserRepository;
import com.kevindmm.spendingapp.security.JwtTokenProvider;
import com.kevindmm.spendingapp.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RefreshTokenService refreshTokenService;

    private User createMockUser() {
        User user = new User();
        user.setName("test");
        user.setEmail("example@test.com");
        user.setPasswordHash("hashedPassword");
        return user;
    }

    // ========================================
    // Login Endpoint Tests
    // ========================================

    @Test
    void login_withValidCredentials_returnsToken() {
        User userMock = createMockUser();

        Mockito.when(userRepository.findByEmail("example@test.com")).thenReturn(Optional.of(userMock));
        Mockito.when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        Mockito.when(jwtTokenProvider.generateJwtToken("example@test.com")).thenReturn("mocked-jwt-token");

        RefreshToken mockRefreshToken = new RefreshToken("mocked-refresh-token", userMock, Instant.now().plusSeconds(604800));
        Mockito.when(refreshTokenService.createRefreshToken(userMock)).thenReturn(mockRefreshToken);

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("example@test.com", "password123");

        ResponseEntity<LoginResponseDTO> responseEntity = authController.login(loginRequestDTO);
        LoginResponseDTO response = responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("mocked-refresh-token", response.getRefreshToken());
        assertEquals("example@test.com", response.getUsername());
    }

    @Test
    void login_withInvalidCredentials_returnsBadRequest() {
        Mockito.when(userRepository.findByEmail("nonExistingUser@test.com")).thenReturn(Optional.empty());

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("nonExistingUser@test.com", "wrongPassword");

        ResponseEntity<LoginResponseDTO> responseEntity = authController.login(loginRequestDTO);
        LoginResponseDTO response = responseEntity.getBody();

        assertEquals(401, responseEntity.getStatusCodeValue());
        assertNull(response.getToken());
        assertNull(response.getRefreshToken());
        assertNull(response.getUsername());
    }

    @ParameterizedTest(name = "login with {1} returns BadRequest")
    @MethodSource("invalidLoginRequests")
    void login_withInvalidInputs_returnsBadRequest(LoginRequestDTO request, String description) {
        ResponseEntity<LoginResponseDTO> responseEntity = authController.login(request);
        LoginResponseDTO response = responseEntity.getBody();

        assertEquals(401, responseEntity.getStatusCodeValue());
        assertNull(response.getToken());
        assertNull(response.getRefreshToken());
        assertNull(response.getUsername());
    }

    static Stream<Arguments> invalidLoginRequests() {
        return Stream.of(
                Arguments.of(new LoginRequestDTO("", "password123"), "empty email"),
                Arguments.of(new LoginRequestDTO("example@test.com", null), "null password"),
                Arguments.of(new LoginRequestDTO("example@test.com", ""), "empty password"),
                Arguments.of(new LoginRequestDTO("invalid-email", "password123"), "invalid email format"),
                Arguments.of(new LoginRequestDTO("", null), "both fields invalid")
        );
    }

    @Test
    void login_withWrongPassword_returnsBadRequest() {
        User userMock = createMockUser();

        Mockito.when(userRepository.findByEmail("example@test.com")).thenReturn(Optional.of(userMock));
        Mockito.when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("example@test.com", "wrongPassword");

        ResponseEntity<LoginResponseDTO> responseEntity = authController.login(loginRequestDTO);
        LoginResponseDTO response = responseEntity.getBody();

        assertEquals(401, responseEntity.getStatusCodeValue());
        assertNotNull(response);
        assertNull(response.getToken());
        assertNull(response.getRefreshToken());
        assertNull(response.getUsername());
    }

    // ========================================
    // Refresh Token Endpoint Tests
    // ========================================

    @Test
    void refreshToken_withValidToken_returnsNewAccessToken() {
        User userMock = createMockUser();
        RefreshToken mockRefreshToken = new RefreshToken("valid-refresh-token", userMock, Instant.now().plusSeconds(604800));
        
        Mockito.when(jwtTokenProvider.validateRefreshToken("valid-refresh-token")).thenReturn(true);
        Mockito.when(refreshTokenService.findByToken("valid-refresh-token")).thenReturn(mockRefreshToken);
        Mockito.when(refreshTokenService.verifyExpiration(mockRefreshToken)).thenReturn(mockRefreshToken);
        Mockito.when(jwtTokenProvider.generateJwtToken("example@test.com")).thenReturn("new-access-token");

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("valid-refresh-token");

        ResponseEntity<RefreshTokenResponseDTO> responseEntity = authController.refreshToken(request);
        RefreshTokenResponseDTO response = responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
    }

    @Test
    void refreshToken_withInvalidFormat_returns401() {
        Mockito.when(jwtTokenProvider.validateRefreshToken("invalid-format-token")).thenReturn(false);

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("invalid-format-token");

        ResponseEntity<RefreshTokenResponseDTO> responseEntity = authController.refreshToken(request);

        assertEquals(401, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    void refreshToken_withNonExistingToken_returns401() {
        Mockito.when(jwtTokenProvider.validateRefreshToken("non-existing-token")).thenReturn(true);
        Mockito.when(refreshTokenService.findByToken("non-existing-token"))
                .thenThrow(new RuntimeException("Token not found"));

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("non-existing-token");

        ResponseEntity<RefreshTokenResponseDTO> responseEntity = authController.refreshToken(request);

        assertEquals(400, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    void refreshToken_withRevokedToken_returns400() {
        User userMock = createMockUser();
        RefreshToken revokedToken = new RefreshToken("revoked-token", userMock, Instant.now().plusSeconds(604800));
        revokedToken.setRevoked(true);

        Mockito.when(jwtTokenProvider.validateRefreshToken("revoked-token")).thenReturn(true);
        Mockito.when(refreshTokenService.findByToken("revoked-token")).thenReturn(revokedToken);

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("revoked-token");

        ResponseEntity<RefreshTokenResponseDTO> responseEntity = authController.refreshToken(request);

        assertEquals(400, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    void refreshToken_withExpiredToken_returns401() {
        User userMock = createMockUser();
        RefreshToken expiredToken = new RefreshToken("expired-token", userMock, Instant.now().minusSeconds(3600));

        Mockito.when(jwtTokenProvider.validateRefreshToken("expired-token")).thenReturn(true);
        Mockito.when(refreshTokenService.findByToken("expired-token")).thenReturn(expiredToken);
        Mockito.when(refreshTokenService.verifyExpiration(expiredToken))
                .thenThrow(new RuntimeException("Token expired"));

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("expired-token");

        ResponseEntity<RefreshTokenResponseDTO> responseEntity = authController.refreshToken(request);

        assertEquals(400, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    void refreshToken_withAccessToken_returns401() {
        Mockito.when(jwtTokenProvider.validateRefreshToken("access-token-not-refresh")).thenReturn(false);

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("access-token-not-refresh");

        ResponseEntity<RefreshTokenResponseDTO> responseEntity = authController.refreshToken(request);

        assertEquals(401, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }
    // ==================== LOGOUT TESTS ====================

    @Test
    void logout_withValidToken_returnsSuccess() {
        User userMock = createMockUser();
        RefreshToken validToken = new RefreshToken("valid-refresh-token", userMock, Instant.now().plusSeconds(3600));

        Mockito.when(jwtTokenProvider.validateRefreshToken("valid-refresh-token")).thenReturn(true);
        Mockito.when(refreshTokenService.findByToken("valid-refresh-token")).thenReturn(validToken);
        Mockito.doNothing().when(refreshTokenService).revokeRefreshToken("valid-refresh-token");

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("valid-refresh-token");

        ResponseEntity<?> responseEntity = authController.logout(request);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    void logout_withInvalidFormat_returns401() {
        Mockito.when(jwtTokenProvider.validateRefreshToken("invalid-format-token")).thenReturn(false);

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("invalid-format-token");

        ResponseEntity<?> responseEntity = authController.logout(request);

        assertEquals(401, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    void logout_withNonExistingToken_returns400() {
        Mockito.when(jwtTokenProvider.validateRefreshToken("non-existing-token")).thenReturn(true);
        Mockito.when(refreshTokenService.findByToken("non-existing-token"))
                .thenThrow(new RuntimeException("Token not found"));

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("non-existing-token");

        ResponseEntity<?> responseEntity = authController.logout(request);

        assertEquals(400, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    void logout_withRevokedToken_returns400() {
        User userMock = createMockUser();
        RefreshToken revokedToken = new RefreshToken("revoked-token", userMock, Instant.now().plusSeconds(3600));
        revokedToken.setRevoked(true);

        Mockito.when(jwtTokenProvider.validateRefreshToken("revoked-token")).thenReturn(true);
        Mockito.when(refreshTokenService.findByToken("revoked-token")).thenReturn(revokedToken);

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("revoked-token");

        ResponseEntity<?> responseEntity = authController.logout(request);

        assertEquals(400, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    void logout_withServiceException_returns400() {
        User userMock = createMockUser();
        RefreshToken validToken = new RefreshToken("valid-token", userMock, Instant.now().plusSeconds(3600));

        Mockito.when(jwtTokenProvider.validateRefreshToken("valid-token")).thenReturn(true);
        Mockito.when(refreshTokenService.findByToken("valid-token")).thenReturn(validToken);
        Mockito.doThrow(new RuntimeException("Database error"))
                .when(refreshTokenService).revokeRefreshToken("valid-token");

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("valid-token");

        ResponseEntity<?> responseEntity = authController.logout(request);

        assertEquals(400, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }


}
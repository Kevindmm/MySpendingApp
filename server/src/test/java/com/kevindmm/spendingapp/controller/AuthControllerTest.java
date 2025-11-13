package com.kevindmm.spendingapp.controller;

import com.kevindmm.spendingapp.dto.LoginRequestDTO;
import com.kevindmm.spendingapp.dto.LoginResponseDTO;
import com.kevindmm.spendingapp.model.User;
import com.kevindmm.spendingapp.repository.UserRepository;

import com.kevindmm.spendingapp.security.JwtTokenProvider;
import org.junit.jupiter.api.Assertions;
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

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @MockBean
    private PasswordEncoder passwordEncoder; // For encoding passwords during test setup

    @MockBean
    private JwtTokenProvider jwtTokenProvider; // Needed to validate JWT tokens generated during tests

    @MockBean
    private UserRepository userRepository;

    private User createMockUser() {
        User user = new User();
        user.setName("test");
        user.setEmail("example@test.com");
        user.setPasswordHash("hashedPassword");
        return user;
    }

    @Test
    void login_withValidCredentials_returnsToken() {
        User userMock = createMockUser();

        //Mock repository and encoder behavior
        Mockito.when(userRepository.findByEmail("example@test.com")).thenReturn(Optional.of(userMock));

        //Mock password encoder to return true when matching the raw password with the hashed one
        Mockito.when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        //Mock JWT token generation
        Mockito.when(jwtTokenProvider.generateJwtToken("example@test.com")).thenReturn("mocked-jwt-token");

        //Create login request DTO
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("example@test.com", "password123");

        ResponseEntity<LoginResponseDTO> responseEntity = authController.login(loginRequestDTO);
        LoginResponseDTO response = responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCodeValue());
        Assertions.assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("example@test.com", response.getUsername());
    }

    @Test
    void login_withInvalidCredentials_returnsBadRequest() {
        //Mock repository behavior
        Mockito.when(userRepository.findByEmail("nonExistingUser@test.com")).thenReturn(Optional.empty());

        //Create login request DTO with invalid password
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("nonExistingUser@test.com", "wrongPassword");

        ResponseEntity<LoginResponseDTO> responseEntity = authController.login(loginRequestDTO);
        LoginResponseDTO response = responseEntity.getBody();

        assertEquals(401, responseEntity.getStatusCodeValue());
        assertNull(response.getToken());
        assertNull(response.getUsername());
    }

    @ParameterizedTest(name = "login with {1} returns BadRequest")
    @MethodSource("invalidLoginRequests")
    void login_withInvalidInputs_returnsBadRequest(LoginRequestDTO request, String description) {
        ResponseEntity<LoginResponseDTO> responseEntity = authController.login(request);
        LoginResponseDTO response = responseEntity.getBody();

        assertEquals(401, responseEntity.getStatusCodeValue());
        assertNull(response.getToken());
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

        //Mock repository to return existing user
        Mockito.when(userRepository.findByEmail("example@test.com")).thenReturn(Optional.of(userMock));

        //Mock password encoder to return false when password does not match
        Mockito.when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        //Create login request DTO with wrong password
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("example@test.com", "wrongPassword");

        ResponseEntity<LoginResponseDTO> responseEntity = authController.login(loginRequestDTO);
        LoginResponseDTO response = responseEntity.getBody();

        assertEquals(401, responseEntity.getStatusCodeValue());
        Assertions.assertNotNull(response);
        assertNull(response.getToken());
        assertNull(response.getUsername());
    }

}

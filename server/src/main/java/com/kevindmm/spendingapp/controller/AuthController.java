package com.kevindmm.spendingapp.controller;

import com.kevindmm.spendingapp.dto.*;
import com.kevindmm.spendingapp.model.RefreshToken;
import com.kevindmm.spendingapp.model.User;
import com.kevindmm.spendingapp.repository.UserRepository;
import com.kevindmm.spendingapp.security.JwtTokenProvider;
import com.kevindmm.spendingapp.service.RefreshTokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
         JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        logger.info("Login attempt received");

        // Find user by email (using email as username)
        var userOptional = userRepository.findByEmail(loginRequestDTO.username());

        // Validate credentials
        if(userOptional.isEmpty() || !passwordEncoder.matches(loginRequestDTO.password(), userOptional.get().getPasswordHash())){
            logger.warn("Invalid login attempt - authentication failed");
            return ResponseEntity.status(401).body(new LoginResponseDTO(null, null, null));
        }

        var user = userOptional.get();

        // Generate access token
        String accessToken = jwtTokenProvider.generateJwtToken(user.getEmail());
        
        // Create and save refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        logger.info("User logged in successfully");
        return ResponseEntity.ok(new LoginResponseDTO(accessToken, refreshToken.getToken(), user.getEmail()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken (@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        String requestRefreshToken = refreshTokenRequestDTO.refreshToken();
        
        try {
            // Validate token format
            if (!jwtTokenProvider.validateRefreshToken(requestRefreshToken)) {
                logger.warn("Invalid refresh token format");
                return ResponseEntity.status(401).body(null);
            }
            
            // Find token in database (throws exception if not found)
            RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken);
            
            // Check if revoked
            if (refreshToken.isRevoked()) {
                logger.warn("Attempted to use revoked refresh token");
                return ResponseEntity.status(400).body(null);
            }
            
            // Verify expiration (throws exception if expired)
            refreshToken = refreshTokenService.verifyExpiration(refreshToken);
            
            // Generate new access token
            String newAccessToken = jwtTokenProvider.generateJwtToken(refreshToken.getUser().getEmail());
            
            logger.info("Access token refreshed successfully");
            return ResponseEntity.ok(new RefreshTokenResponseDTO(newAccessToken));
            
        } catch (RuntimeException e) {
            logger.error("Error refreshing token: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogOutResponseDTO> logout(@Valid @RequestBody RefreshTokenRequestDTO requestDTO){
        String refreshToken = requestDTO.refreshToken();

        try{
            if(!jwtTokenProvider.validateRefreshToken(refreshToken)){
                logger.warn("Invalid refresh token format.");
                return ResponseEntity.status(401).body(null);
            }

            RefreshToken refreshTokenDB = refreshTokenService.findByToken(refreshToken);

            if(refreshTokenDB.isRevoked()){
                logger.warn("Attempted to use a revoked token");
                return ResponseEntity.status(400).body(null);
            }

            //Revoke the token
            refreshTokenService.revokeRefreshToken(refreshToken);
            logger.info("User {} logged out successfully", refreshTokenDB.getUser().getEmail());
            return ResponseEntity.status(200).body(new LogOutResponseDTO("Logged out successfully.", refreshTokenDB.getUser().getEmail()));

        }catch (RuntimeException e){
            logger.error("Error logging out a user: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body(null);
        }
    }

    /**
     * Registers a new user in the system.
     *
     * @param requestDTO the registration request containing email, password, and name
     * @return ResponseEntity with RegisterResponseDTO containing:
     *         <ul>
     *           <li>201 - User registered successfully</li>
     *           <li>409 - Email already exists</li>
     *           <li>422 - Validation errors</li>
     *           <li>500 - Registration failed due to server error</li>
     *         </ul>
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        logger.info("Attempting to register a new user");

        String emailChecked = requestDTO.email().toLowerCase().trim();

        try {
            var existingUser = userRepository.findByEmail(emailChecked);
            if(existingUser.isPresent()){
                logger.warn("Registration failed - email already exists");
                return ResponseEntity.status(409).body(new RegisterResponseDTO("Email already exists", null));
            }

            var newUser = new User();
            newUser.setEmail(emailChecked);
            newUser.setPasswordHash(passwordEncoder.encode(requestDTO.password()));
            newUser.setName(requestDTO.name().trim());

            var savedUser = userRepository.save(newUser);

            logger.info("User registered successfully");

            return ResponseEntity.status(201).body(new RegisterResponseDTO("User registered successfully", savedUser.getId().toString()));
        } catch(DataAccessException e) {
            logger.error("Registration failed: {}", e.getMessage());
            logger.debug("Registration failed with stack trace", e);
            return ResponseEntity.status(500).body(new RegisterResponseDTO("Registration failed", null));
        }
    }
}

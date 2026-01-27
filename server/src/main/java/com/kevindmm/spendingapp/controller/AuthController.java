package com.kevindmm.spendingapp.controller;

import com.kevindmm.spendingapp.dto.LoginRequestDTO;
import com.kevindmm.spendingapp.dto.LoginResponseDTO;
import com.kevindmm.spendingapp.dto.RefreshTokenRequestDTO;
import com.kevindmm.spendingapp.dto.RefreshTokenResponseDTO;
import com.kevindmm.spendingapp.model.RefreshToken;
import com.kevindmm.spendingapp.repository.UserRepository;
import com.kevindmm.spendingapp.security.JwtTokenProvider;
import com.kevindmm.spendingapp.service.RefreshTokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                return ResponseEntity.status(401).body(null);
            }
            
            // Verify expiration (throws exception if expired)
            refreshToken = refreshTokenService.verifyExpiration(refreshToken);
            
            // Generate new access token
            String newAccessToken = jwtTokenProvider.generateJwtToken(refreshToken.getUser().getEmail());
            
            logger.info("Access token refreshed successfully");
            return ResponseEntity.ok(new RefreshTokenResponseDTO(newAccessToken));
            
        } catch (RuntimeException e) {
            logger.error("Error refreshing token: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body(null);
        }
    }
    
}

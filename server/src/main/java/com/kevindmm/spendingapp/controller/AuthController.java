package com.kevindmm.spendingapp.controller;

import com.kevindmm.spendingapp.dto.LoginRequestDTO;
import com.kevindmm.spendingapp.dto.LoginResponseDTO;
import com.kevindmm.spendingapp.repository.UserRepository;
import com.kevindmm.spendingapp.security.JwtTokenProvider;

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

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        logger.info("Login attempt for user: {}", loginRequestDTO.username());

        var userOpt = userRepository.findByEmail(loginRequestDTO.username()).orElse(null); // Using email as username

        if(userOpt == null || !passwordEncoder.matches(loginRequestDTO.password(), userOpt.getPasswordHash())){ // Validate password
            logger.warn("Invalid login attempt for user: {}", loginRequestDTO.username());
            return ResponseEntity.status(401).body(new LoginResponseDTO(null, null)); // Return 401 -> Invalid credentials
        }

        String token = jwtTokenProvider.generateJwtToken(userOpt.getEmail()); // Generate JWT token using email
        logger.info("User {} logged in successfully", loginRequestDTO.username());
        return ResponseEntity.ok(new LoginResponseDTO(token, userOpt.getEmail())); // Return 200 -> token and username (email)
    }
}

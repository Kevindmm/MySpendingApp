package com.kevindmm.spendingapp.service;

import com.kevindmm.spendingapp.model.RefreshToken;
import com.kevindmm.spendingapp.model.User;
import com.kevindmm.spendingapp.repository.RefreshTokenRepository;
import com.kevindmm.spendingapp.security.JwtTokenProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RefreshTokenService {
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenDurationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtTokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        String tokenString = jwtTokenProvider.generateRefreshToken(user.getEmail());
        Instant expiryDate = Instant.now().plusMillis(refreshTokenDurationMs);

        RefreshToken refreshToken = new RefreshToken(tokenString, user, expiryDate);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        logger.info("Refresh token created for user: {}", user.getEmail());

        return savedRefreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.isExpired()){
            refreshTokenRepository.delete(token);
            logger.warn("Refresh token expired and deleted for user: {}", token.getUser().getEmail());
            throw new RuntimeException("Refresh Token expired. Please login again.");
        }
        return token;
    }
    
    @Transactional
    public void revokeRefreshToken(String tokenString) {
        refreshTokenRepository.findByToken( tokenString)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                    logger.info("Revoked refresh token for user: {}",
                             token.getUser().getEmail());
                });
                            
    }

    @Transactional
    public RefreshToken findByToken(String tokenString) {
        return refreshTokenRepository.findByToken(tokenString)
                .orElseThrow(() -> {
                    logger.warn("Refresh token not found in database");
                    return new RuntimeException("Refresh token not found");
                });
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
        logger.info("Revoked all refresh tokens for user: {}", user.getEmail());
    }
}

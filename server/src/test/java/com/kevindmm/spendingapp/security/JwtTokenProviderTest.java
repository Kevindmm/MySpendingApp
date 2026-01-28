package com.kevindmm.spendingapp.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // ========================================
    // Access Token Tests
    // ========================================

    @Test
    void generateJwtToken_withValidEmail_returnsToken() {
        String token = jwtTokenProvider.generateJwtToken("test@example.com");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateJwtToken_withNullEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> jwtTokenProvider.generateJwtToken(null));
    }

    @Test
    void generateJwtToken_withBlankEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> jwtTokenProvider.generateJwtToken(""));
    }

    @Test
    void validateJwtToken_withValidToken_returnsTrue() {
        String token = jwtTokenProvider.generateJwtToken("test@example.com");
        assertTrue(jwtTokenProvider.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_withInvalidToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateJwtToken("invalidToken"));
    }

    @Test
    void validateJwtToken_withNullToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateJwtToken(null));
    }

    @Test
    void validateJwtToken_withBlankToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateJwtToken(""));
    }

    @Test
    void getClaimsFromJwtToken_withValidToken_returnsClaims() {
        String token = jwtTokenProvider.generateJwtToken("test@example.com");
        Claims claims = jwtTokenProvider.getClaimsFromJwtToken(token);
        assertNotNull(claims);
        assertEquals("test@example.com", claims.getSubject());
    }

    // ========================================
    // Refresh Token Tests
    // ========================================

    @Test
    void generateRefreshToken_withValidEmail_returnsToken() {
        String refreshToken = jwtTokenProvider.generateRefreshToken("test@example.com");
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
    }

    @Test
    void generateRefreshToken_withNullEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> jwtTokenProvider.generateRefreshToken(null));
    }

    @Test
    void generateRefreshToken_withBlankEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> jwtTokenProvider.generateRefreshToken(""));
    }

    @Test
    void validateRefreshToken_withValidRefreshToken_returnsTrue() {
        String refreshToken = jwtTokenProvider.generateRefreshToken("test@example.com");
        assertTrue(jwtTokenProvider.validateRefreshToken(refreshToken));
    }

    @Test
    void validateRefreshToken_withAccessToken_returnsFalse() {
        // Access token doesn't have "type": "refresh" claim
        String accessToken = jwtTokenProvider.generateJwtToken("test@example.com");
        assertFalse(jwtTokenProvider.validateRefreshToken(accessToken));
    }

    @Test
    void validateRefreshToken_withInvalidToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateRefreshToken("invalidToken"));
    }

    @Test
    void validateRefreshToken_withNullToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateRefreshToken(null));
    }

    @Test
    void validateRefreshToken_withBlankToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateRefreshToken(""));
    }

    @Test
    void getEmailFromRefreshToken_withValidToken_returnsEmail() {
        String refreshToken = jwtTokenProvider.generateRefreshToken("test@example.com");
        String email = jwtTokenProvider.getEmailFromRefreshToken(refreshToken);
        assertEquals("test@example.com", email);
    }

    @Test
    void refreshToken_containsTypeClaim() {
        String refreshToken = jwtTokenProvider.generateRefreshToken("test@example.com");
        Claims claims = jwtTokenProvider.getClaimsFromJwtToken(refreshToken);
        assertEquals("refresh", claims.get("type", String.class));
    }

    @Test
    void accessToken_doesNotContainTypeClaim() {
        String accessToken = jwtTokenProvider.generateJwtToken("test@example.com");
        Claims claims = jwtTokenProvider.getClaimsFromJwtToken(accessToken);
        assertNull(claims.get("type", String.class));
    }
}

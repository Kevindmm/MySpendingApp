package com.kevindmm.spendingapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    private User testUser;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setName("Test");
        testUser.setLastName("User");
    }

    // ========================================
    // Constructor Tests
    // ========================================

    @Test
    void constructor_withValidParameters_createsRefreshToken() {
        Instant expiryDate = Instant.now().plusSeconds(604800);
        
        refreshToken = new RefreshToken("test-token", testUser, expiryDate);

        assertNotNull(refreshToken);
        assertEquals("test-token", refreshToken.getToken());
        assertEquals(testUser, refreshToken.getUser());
        assertEquals(expiryDate, refreshToken.getExpiryDate());
        assertFalse(refreshToken.isRevoked());
        assertNotNull(refreshToken.getCreatedAt());
    }

    @Test
    void emptyConstructor_createsRefreshToken() {
        refreshToken = new RefreshToken();

        assertNotNull(refreshToken);
        assertNull(refreshToken.getToken());
        assertNull(refreshToken.getUser());
        assertNull(refreshToken.getExpiryDate());
        assertFalse(refreshToken.isRevoked());
        assertNotNull(refreshToken.getCreatedAt());
    }

    // ========================================
    // isExpired Tests
    // ========================================

    @Test
    void isExpired_withFutureExpiryDate_returnsFalse() {
        Instant futureDate = Instant.now().plusSeconds(3600); // 1 hour in future
        refreshToken = new RefreshToken("test-token", testUser, futureDate);

        assertFalse(refreshToken.isExpired());
    }

    @Test
    void isExpired_withPastExpiryDate_returnsTrue() {
        Instant pastDate = Instant.now().minusSeconds(3600); // 1 hour in past
        refreshToken = new RefreshToken("test-token", testUser, pastDate);

        assertTrue(refreshToken.isExpired());
    }

    @Test
    void isExpired_withCurrentTime_returnsTrue() {
        Instant now = Instant.now();
        refreshToken = new RefreshToken("test-token", testUser, now);

        // Sleep a tiny bit to ensure time has passed
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(refreshToken.isExpired());
    }

    // ========================================
    // Revoked Flag Tests
    // ========================================

    @Test
    void setRevoked_changesRevokedStatus() {
        refreshToken = new RefreshToken("test-token", testUser, Instant.now().plusSeconds(604800));
        
        assertFalse(refreshToken.isRevoked());
        
        refreshToken.setRevoked(true);
        
        assertTrue(refreshToken.isRevoked());
    }

    @Test
    void isRevoked_defaultsToFalse() {
        refreshToken = new RefreshToken("test-token", testUser, Instant.now().plusSeconds(604800));
        
        assertFalse(refreshToken.isRevoked());
    }

    // ========================================
    // Getters and Setters Tests
    // ========================================

    @Test
    void getToken_returnsCorrectToken() {
        refreshToken = new RefreshToken("my-token", testUser, Instant.now().plusSeconds(604800));
        
        assertEquals("my-token", refreshToken.getToken());
    }

    @Test
    void getUser_returnsCorrectUser() {
        refreshToken = new RefreshToken("test-token", testUser, Instant.now().plusSeconds(604800));
        
        assertEquals(testUser, refreshToken.getUser());
        assertEquals("test@example.com", refreshToken.getUser().getEmail());
    }

    @Test
    void getExpiryDate_returnsCorrectDate() {
        Instant expiryDate = Instant.now().plusSeconds(604800);
        refreshToken = new RefreshToken("test-token", testUser, expiryDate);
        
        assertEquals(expiryDate, refreshToken.getExpiryDate());
    }

    @Test
    void getCreatedAt_isSetAutomatically() {
        Instant before = Instant.now();
        refreshToken = new RefreshToken("test-token", testUser, Instant.now().plusSeconds(604800));
        Instant after = Instant.now();

        assertNotNull(refreshToken.getCreatedAt());
        assertTrue(refreshToken.getCreatedAt().isAfter(before) || refreshToken.getCreatedAt().equals(before));
        assertTrue(refreshToken.getCreatedAt().isBefore(after) || refreshToken.getCreatedAt().equals(after));
    }

    @Test
    void setToken_updatesToken() {
        refreshToken = new RefreshToken("initial-token", testUser, Instant.now().plusSeconds(604800));
        String newToken = "updated-token";

        refreshToken.setToken(newToken);

        assertEquals(newToken, refreshToken.getToken());
    }

    @Test
    void setUser_updatesUser() {
        refreshToken = new RefreshToken("test-token", testUser, Instant.now().plusSeconds(604800));
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setName("New");
        newUser.setLastName("User");

        refreshToken.setUser(newUser);

        assertEquals(newUser, refreshToken.getUser());
        assertEquals("new@example.com", refreshToken.getUser().getEmail());
    }

    @Test
    void setExpiryDate_updatesExpiryDate() {
        Instant initialExpiry = Instant.now().plusSeconds(604800);
        refreshToken = new RefreshToken("test-token", testUser, initialExpiry);
        Instant newExpiry = Instant.now().plusSeconds(1209600); // 14 days

        refreshToken.setExpiryDate(newExpiry);

        assertEquals(newExpiry, refreshToken.getExpiryDate());
    }

    @Test
    void getId_returnsId() {
        refreshToken = new RefreshToken("test-token", testUser, Instant.now().plusSeconds(604800));

        // ID is null until persisted by JPA
        assertNull(refreshToken.getId());
    }

    // ========================================
    // Edge Cases
    // ========================================

    @Test
    void refreshToken_withLongToken_handlesCorrectly() {
        String longToken = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE3Njk0NDEyNDIsImV4cCI6MTc2OTgwMTI0Mn0.LfSczqs0ZPdeEnEn85fdWFhYKfkeHDhtj_9GNfbLfo5BLc6XQfw-fRJCaIu7fyFv";
        refreshToken = new RefreshToken(longToken, testUser, Instant.now().plusSeconds(604800));

        assertEquals(longToken, refreshToken.getToken());
        assertTrue(refreshToken.getToken().length() > 100);
    }

    @Test
    void refreshToken_canBeRevokedMultipleTimes() {
        refreshToken = new RefreshToken("test-token", testUser, Instant.now().plusSeconds(604800));

        refreshToken.setRevoked(true);
        assertTrue(refreshToken.isRevoked());

        refreshToken.setRevoked(false);
        assertFalse(refreshToken.isRevoked());

        refreshToken.setRevoked(true);
        assertTrue(refreshToken.isRevoked());
    }
}

// Made with Bob

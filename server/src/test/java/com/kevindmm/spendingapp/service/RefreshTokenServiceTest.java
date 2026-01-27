package com.kevindmm.spendingapp.service;

import com.kevindmm.spendingapp.model.RefreshToken;
import com.kevindmm.spendingapp.model.User;
import com.kevindmm.spendingapp.repository.RefreshTokenRepository;
import com.kevindmm.spendingapp.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setName("Test");
        testUser.setLastName("User");

        testRefreshToken = new RefreshToken(
                "mock-refresh-token-jwt",
                testUser,
                Instant.now().plusSeconds(604800) // 7 days
        );
    }

    // ========================================
    // createRefreshToken Tests
    // ========================================

    @Test
    void createRefreshToken_withValidUser_returnsRefreshToken() {
        // Arrange
        when(jwtTokenProvider.generateRefreshToken(testUser.getEmail()))
                .thenReturn("generated-refresh-token");
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenReturn(testRefreshToken);

        // Act
        RefreshToken result = refreshTokenService.createRefreshToken(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        verify(jwtTokenProvider).generateRefreshToken(testUser.getEmail());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void createRefreshToken_savesTokenToDatabase() {
        // Arrange
        when(jwtTokenProvider.generateRefreshToken(testUser.getEmail()))
                .thenReturn("generated-refresh-token");
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenReturn(testRefreshToken);

        // Act
        refreshTokenService.createRefreshToken(testUser);

        // Assert
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    // ========================================
    // findByToken Tests
    // ========================================

    @Test
    void findByToken_withExistingToken_returnsRefreshToken() {
        // Arrange
        when(refreshTokenRepository.findByToken("existing-token"))
                .thenReturn(Optional.of(testRefreshToken));

        // Act
        RefreshToken result = refreshTokenService.findByToken("existing-token");

        // Assert
        assertNotNull(result);
        assertEquals(testRefreshToken, result);
        verify(refreshTokenRepository).findByToken("existing-token");
    }

    @Test
    void findByToken_withNonExistingToken_throwsRuntimeException() {
        // Arrange
        when(refreshTokenRepository.findByToken("non-existing-token"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            refreshTokenService.findByToken("non-existing-token")
        );
        verify(refreshTokenRepository).findByToken("non-existing-token");
    }

    // ========================================
    // verifyExpiration Tests
    // ========================================

    @Test
    void verifyExpiration_withValidToken_returnsToken() {
        // Arrange
        RefreshToken validToken = new RefreshToken(
                "valid-token",
                testUser,
                Instant.now().plusSeconds(3600) // 1 hour in future
        );

        // Act
        RefreshToken result = refreshTokenService.verifyExpiration(validToken);

        // Assert
        assertNotNull(result);
        assertEquals(validToken, result);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void verifyExpiration_withExpiredToken_deletesAndThrowsException() {
        // Arrange
        RefreshToken expiredToken = new RefreshToken(
                "expired-token",
                testUser,
                Instant.now().minusSeconds(3600) // 1 hour in past
        );

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            refreshTokenService.verifyExpiration(expiredToken)
        );
        
        assertTrue(exception.getMessage().contains("expired"));
        verify(refreshTokenRepository).delete(expiredToken);
    }

    // ========================================
    // revokeToken Tests
    // ========================================

    @Test
    void revokeToken_withExistingToken_revokesAndSaves() {
        // Arrange
        when(refreshTokenRepository.findByToken("token-to-revoke"))
                .thenReturn(Optional.of(testRefreshToken));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenReturn(testRefreshToken);

        // Act
        refreshTokenService.revokeRefreshToken("token-to-revoke");

        // Assert
        assertTrue(testRefreshToken.isRevoked());
        verify(refreshTokenRepository).findByToken("token-to-revoke");
        verify(refreshTokenRepository).save(testRefreshToken);
    }

    @Test
    void revokeToken_withNonExistingToken_doesNotThrowException() {
        // Arrange
        when(refreshTokenRepository.findByToken("non-existing-token"))
                .thenReturn(Optional.empty());

        // Act - should not throw exception, just do nothing
        refreshTokenService.revokeRefreshToken("non-existing-token");

        // Assert
        verify(refreshTokenRepository).findByToken("non-existing-token");
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void revokeToken_alreadyRevokedToken_stillSaves() {
        // Arrange
        testRefreshToken.setRevoked(true);
        when(refreshTokenRepository.findByToken("already-revoked"))
                .thenReturn(Optional.of(testRefreshToken));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenReturn(testRefreshToken);

        // Act
        refreshTokenService.revokeRefreshToken("already-revoked");

        // Assert
        assertTrue(testRefreshToken.isRevoked());
        verify(refreshTokenRepository).save(testRefreshToken);
    }

    // ========================================
    // revokeAllUserTokens Tests
    // ========================================

    @Test
    void revokeAllUserTokens_withValidUser_callsRepository() {
        // Arrange
        doNothing().when(refreshTokenRepository).revokeAllUserTokens(testUser);

        // Act
        refreshTokenService.revokeAllUserTokens(testUser);

        // Assert
        verify(refreshTokenRepository).revokeAllUserTokens(testUser);
    }

    @Test
    void revokeAllUserTokens_withDifferentUser_callsRepositoryWithCorrectUser() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setName("Another");
        anotherUser.setLastName("User");
        
        doNothing().when(refreshTokenRepository).revokeAllUserTokens(anotherUser);

        // Act
        refreshTokenService.revokeAllUserTokens(anotherUser);

        // Assert
        verify(refreshTokenRepository).revokeAllUserTokens(anotherUser);
        verify(refreshTokenRepository, never()).revokeAllUserTokens(testUser);
    }
}

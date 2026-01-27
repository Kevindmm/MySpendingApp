package com.kevindmm.spendingapp.dto;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenRequestDTOTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    // ========================================
    // Valid DTO Tests
    // ========================================

    @Test
    void validRefreshTokenRequestDTO_passesValidation() {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("valid-refresh-token");

        Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void refreshTokenRequestDTO_withLongToken_passesValidation() {
        String longToken = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE3Njk0NDEyNDIsImV4cCI6MTc2OTgwMTI0Mn0.LfSczqs0ZPdeEnEn85fdWFhYKfkeHDhtj_9GNfbLfo5BLc6XQfw-fRJCaIu7fyFv";
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO(longToken);

        Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
        assertEquals(longToken, dto.refreshToken());
    }

    // ========================================
    // Invalid DTO Tests
    // ========================================

    @Test
    void refreshTokenRequestDTO_withNullToken_failsValidation() {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO(null);

        Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("blank"));
    }

    @Test
    void refreshTokenRequestDTO_withEmptyToken_failsValidation() {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("");

        Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void refreshTokenRequestDTO_withBlankToken_failsValidation() {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("   ");

        Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    // ========================================
    // Getter Tests
    // ========================================

    @Test
    void refreshToken_returnsCorrectValue() {
        String token = "test-refresh-token";
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO(token);

        assertEquals(token, dto.refreshToken());
    }

    // ========================================
    // Record Behavior Tests
    // ========================================

    @Test
    void twoRefreshTokenRequestDTOs_withSameToken_areEqual() {
        RefreshTokenRequestDTO dto1 = new RefreshTokenRequestDTO("same-token");
        RefreshTokenRequestDTO dto2 = new RefreshTokenRequestDTO("same-token");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void twoRefreshTokenRequestDTOs_withDifferentTokens_areNotEqual() {
        RefreshTokenRequestDTO dto1 = new RefreshTokenRequestDTO("token-1");
        RefreshTokenRequestDTO dto2 = new RefreshTokenRequestDTO("token-2");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void toString_containsRefreshToken() {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("my-token");

        String toString = dto.toString();

        assertTrue(toString.contains("my-token"));
        assertTrue(toString.contains("RefreshTokenRequestDTO"));
    }
}

// Made with Bob

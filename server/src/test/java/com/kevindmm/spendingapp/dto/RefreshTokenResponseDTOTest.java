package com.kevindmm.spendingapp.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenResponseDTOTest {

    // ========================================
    // Constructor and Getter Tests
    // ========================================

    @Test
    void constructor_withValidAccessToken_createsDTO() {
        String accessToken = "new-access-token";
        
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(accessToken);

        assertNotNull(dto);
        assertEquals(accessToken, dto.getAccessToken());
    }

    @Test
    void constructor_withLongAccessToken_createsDTO() {
        String longToken = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzY5NDQxMjQyLCJleHAiOjE3Njk4MDEyNDJ9.5G5fkFq3tMauUxCitEvX_-e9S3Z3pXSMgUKjDsL7JoJ7Gpno4ryBI76DBEG82d8F";
        
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(longToken);

        assertEquals(longToken, dto.getAccessToken());
        assertTrue(dto.getAccessToken().length() > 100);
    }

    @Test
    void constructor_withNullAccessToken_createsDTO() {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(null);

        assertNotNull(dto);
        assertNull(dto.getAccessToken());
    }

    @Test
    void constructor_withEmptyAccessToken_createsDTO() {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("");

        assertNotNull(dto);
        assertEquals("", dto.getAccessToken());
    }

    @Test
    void emptyConstructor_createsDTO() {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO();

        assertNotNull(dto);
        assertNull(dto.getAccessToken());
        assertEquals("Bearer", dto.getTokenType()); // Default value
    }

    // ========================================
    // Getter and Setter Tests
    // ========================================

    @Test
    void getAccessToken_returnsCorrectValue() {
        String token = "test-access-token";
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(token);

        assertEquals(token, dto.getAccessToken());
    }

    @Test
    void setAccessToken_updatesValue() {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("initial-token");
        String newToken = "updated-token";

        dto.setAccessToken(newToken);

        assertEquals(newToken, dto.getAccessToken());
    }

    @Test
    void setAccessToken_withNull_updatesValue() {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("initial-token");

        dto.setAccessToken(null);

        assertNull(dto.getAccessToken());
    }

    @Test
    void getTokenType_returnsDefaultValue() {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("token");

        assertEquals("Bearer", dto.getTokenType());
    }

    @Test
    void setTokenType_updatesValue() {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("token");
        String newType = "Custom";

        dto.setTokenType(newType);

        assertEquals(newType, dto.getTokenType());
    }

    @Test
    void setTokenType_withNull_updatesValue() {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("token");

        dto.setTokenType(null);

        assertNull(dto.getTokenType());
    }

    // ========================================
    // Equality Tests
    // ========================================

    @Test
    void twoRefreshTokenResponseDTOs_withSameToken_areEqual() {
        RefreshTokenResponseDTO dto1 = new RefreshTokenResponseDTO("same-token");
        RefreshTokenResponseDTO dto2 = new RefreshTokenResponseDTO("same-token");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void twoRefreshTokenResponseDTOs_withDifferentTokens_areNotEqual() {
        RefreshTokenResponseDTO dto1 = new RefreshTokenResponseDTO("token-1");
        RefreshTokenResponseDTO dto2 = new RefreshTokenResponseDTO("token-2");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void twoRefreshTokenResponseDTOs_oneWithNull_areNotEqual() {
        RefreshTokenResponseDTO dto1 = new RefreshTokenResponseDTO("token");
        RefreshTokenResponseDTO dto2 = new RefreshTokenResponseDTO(null);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void twoRefreshTokenResponseDTOs_bothWithNull_areEqual() {
        RefreshTokenResponseDTO dto1 = new RefreshTokenResponseDTO(null);
        RefreshTokenResponseDTO dto2 = new RefreshTokenResponseDTO(null);

        assertEquals(dto1, dto2);
    }

    @Test
    void equals_withSameObject_returnsTrue() {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("token");

        assertEquals(dto, dto);
    }

    @Test
    void equals_withNull_returnsFalse() {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("token");

        assertNotEquals(dto, null);
    }

    @Test
    void equals_withDifferentClass_returnsFalse() {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("token");
        String differentClass = "token";

        assertNotEquals(dto, differentClass);
    }

    @Test
    void equals_withDifferentTokenType_areNotEqual() {
        RefreshTokenResponseDTO dto1 = new RefreshTokenResponseDTO("token");
        RefreshTokenResponseDTO dto2 = new RefreshTokenResponseDTO("token");
        dto2.setTokenType("Custom");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void hashCode_withSameValues_returnsSameHash() {
        RefreshTokenResponseDTO dto1 = new RefreshTokenResponseDTO("token");
        RefreshTokenResponseDTO dto2 = new RefreshTokenResponseDTO("token");

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void hashCode_withDifferentValues_returnsDifferentHash() {
        RefreshTokenResponseDTO dto1 = new RefreshTokenResponseDTO("token1");
        RefreshTokenResponseDTO dto2 = new RefreshTokenResponseDTO("token2");

        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    // ========================================
    // toString Tests
    // ========================================

    @Test
    void toString_containsAccessToken() {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO("my-access-token");

        String toString = dto.toString();

        // Token is redacted for security in toString()
        assertTrue(toString.contains("***REDACTED***"));
        assertTrue(toString.contains("Bearer"));
        assertFalse(toString.contains("my-access-token")); // Should NOT contain actual token
    }

    @Test
    void toString_withNullToken_doesNotThrowException() {
        RefreshTokenResponseDTO dto = new RefreshTokenResponseDTO(null);

        assertDoesNotThrow(() -> dto.toString());
    }

    // ========================================
    // Use Case Tests
    // ========================================

    @Test
    void refreshTokenResponseDTO_canBeUsedInSuccessResponse() {
        // Simulate successful refresh
        String newAccessToken = "eyJhbGciOiJIUzM4NCJ9.newtoken.signature";
        RefreshTokenResponseDTO response = new RefreshTokenResponseDTO(newAccessToken);

        assertNotNull(response.getAccessToken());
        assertFalse(response.getAccessToken().isEmpty());
    }

    @Test
    void refreshTokenResponseDTO_canBeUsedInErrorResponse() {
        // Simulate error response with null token
        RefreshTokenResponseDTO response = new RefreshTokenResponseDTO(null);

        assertNull(response.getAccessToken());
    }
}

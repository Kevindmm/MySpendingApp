package com.kevindmm.spendingapp.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseDTOTest {

    @Test
    void testEquals() {
        LoginResponseDTO dto1 = new LoginResponseDTO("token123", "user@example.com");
        LoginResponseDTO dto2 = new LoginResponseDTO("token123", "user@example.com");
        LoginResponseDTO dto3 = new LoginResponseDTO("token456", "user@example.com");
        LoginResponseDTO dto4 = new LoginResponseDTO("token123", "other@example.com");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, dto4);
        assertNotEquals(null, dto1);
        assertNotEquals("not a dto", dto1);
    }

    @Test
    void testHashCode() {
        LoginResponseDTO dto1 = new LoginResponseDTO("token123", "user@example.com");
        LoginResponseDTO dto2 = new LoginResponseDTO("token123", "user@example.com");

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        LoginResponseDTO dto = new LoginResponseDTO("token123", "user@example.com");
        String expected = "LoginResponseDTO{token='***REDACTED***', username='user@example.com'}";

        assertEquals(expected, dto.toString());
    }

    @Test
    void testGetters() {
        LoginResponseDTO dto = new LoginResponseDTO("token123", "user@example.com");

        assertEquals("token123", dto.getToken());
        assertEquals("user@example.com", dto.getUsername());
    }

    @Test
    void testDefaultConstructor() {
        LoginResponseDTO dto = new LoginResponseDTO();

        assertNull(dto.getToken());
        assertNull(dto.getUsername());
    }
}

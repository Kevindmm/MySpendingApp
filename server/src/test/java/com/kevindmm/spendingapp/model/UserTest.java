package com.kevindmm.spendingapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void setEmail_andGetEmail_worksCorrectly() {
        User user = new User();
        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void setName_andGetName_worksCorrectly() {
        User user = new User();
        user.setName("John");
        assertEquals("John", user.getName());
    }

    @Test
    void setLastName_andGetLastName_worksCorrectly() {
        User user = new User();
        user.setLastName("Doe");
        assertEquals("Doe", user.getLastName());
    }

    @Test
    void setPasswordHash_andGetPasswordHash_worksCorrectly() {
        User user = new User();
        user.setPasswordHash("hashedPassword123");
        assertEquals("hashedPassword123", user.getPasswordHash());
    }

    @Test
    void getId_returnsNullBeforePersistence() {
        User user = new User();
        assertNull(user.getId());
    }

    @Test
    void getCreatedAt_returnsNullBeforePersistence() {
        User user = new User();
        assertNull(user.getCreatedAt());
    }
}

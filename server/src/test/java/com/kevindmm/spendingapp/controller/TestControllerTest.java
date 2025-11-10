package com.kevindmm.spendingapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "testuser@example.com")
    void testProtectedEndpoint_withAuthenticatedUser_returnsSuccess() throws Exception {
        mockMvc.perform(get("/api/test/protected"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Protected endpoint accessed successfully"))
                .andExpect(jsonPath("$.user").value("testuser@example.com"));
    }

    @Test
    void testProtectedEndpoint_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/test/protected"))
                .andExpect(status().isForbidden());
    }

}
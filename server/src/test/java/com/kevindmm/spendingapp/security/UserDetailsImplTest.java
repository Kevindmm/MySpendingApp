package com.kevindmm.spendingapp.security;

import com.kevindmm.spendingapp.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void testGetUsername() {
        User user = new User();
        user.setEmail("test@example.com");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        assertEquals("test@example.com", userDetails.getUsername());
    }

    @Test
    void testGetPassword() {
        User user = new User();
        user.setPasswordHash("hashedPassword");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        assertEquals("hashedPassword", userDetails.getPassword());
    }

    @Test
    void testGetAuthorities() {
        User user = new User();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void testIsAccountNonExpired() {
        User user = new User();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        User user = new User();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        User user = new User();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        User user = new User();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        assertTrue(userDetails.isEnabled());
    }
}

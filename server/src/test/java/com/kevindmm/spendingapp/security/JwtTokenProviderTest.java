package com.kevindmm.spendingapp.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=supersecretkeythatisatleastthirtytwocharacterslongforjwttesting",
        "jwt.expiration=3600000"
})
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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
}

package com.kevindmm.spendingapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    //Logger
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    private SecretKey key;

    // Preventing the repeated creation of the key and enhancing performance
    @PostConstruct // Initialize the secret key after the bean is constructed
    public void init() {
        if(jwtSecret == null || jwtSecret.isBlank()){
            throw new IllegalStateException("Property 'jwt.secret' is not set or is blank");
        }

        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if(keyBytes.length < 32){ // 256 bits
            throw new IllegalArgumentException("The JWT secret key must be at least 256 bits (32 bytes) long");
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        logger.debug("JWT secret key initialized ({} bytes)", keyBytes.length);
    }

    //Generate JWT token per username
    public String generateJwtToken(String email){ // Using Email on purpose, tradeoff between privacy and usability. Migrate to userId asap
        if(email == null || email.isBlank()){
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        if(key == null){
            throw new IllegalStateException("JWT secret key is not initialized");
        }

        Instant now = Instant.now();
        Date issuedDate = Date.from(now);
        Date expiryDate = Date.from(now.plusMillis(jwtExpirationInMs));

        return Jwts.builder()
                .claim(Claims.SUBJECT, email)
                .issuedAt(issuedDate)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    //Get Claims(Email) JWT Token
    public Claims getClaimsFromJwtToken(String token){
        var jwsParser = Jwts.parser()// Create a JWT parser because we need to verify the signature
                .verifyWith(key)
                .build()
                .parseSignedClaims(token); // Parse the token and verify its signature
        Claims claims = jwsParser.getPayload(); // Extract the claims from the parsed token
        if (claims.getSubject() == null) {
            logger.warn("JWT token claims missing subject");
        }

        return claims;
    }


    //Validate JWT Token
    public boolean validateJwtToken(String authToken) {
        if (authToken == null || authToken.isBlank()) {
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken); // If parsing is successful, the token is valid

            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.info("JWT expired: {}", e.getMessage());
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
        }

        return false;
    }
}

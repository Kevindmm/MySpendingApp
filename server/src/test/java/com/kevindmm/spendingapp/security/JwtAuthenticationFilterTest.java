package com.kevindmm.spendingapp.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilterInternal_skipsPermitAllEndpoints() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenProvider, userDetailsService);
    }

    @Test
    void doFilterInternal_withValidJwt_setsAuthentication() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/test/someEndpoint");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenProvider.validateJwtToken("validToken")).thenReturn(true);
        when(jwtTokenProvider.getClaimsFromJwtToken("validToken")).thenReturn(mock(io.jsonwebtoken.Claims.class));
        when(jwtTokenProvider.getClaimsFromJwtToken("validToken").getSubject()).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(userDetailsService).loadUserByUsername("user@example.com");
    }

    @Test
    void doFilterInternal_withInvalidJwt_doesNotSetAuthentication() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/test/someEndpoint");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtTokenProvider.validateJwtToken("invalidToken")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilterInternal_withNoJwt_doesNotSetAuthentication() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/test/someEndpoint");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenProvider, userDetailsService);
    }

    @Test
    void doFilterInternal_withJwtNoSubject_doesNotSetAuthentication() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/test/someEndpoint");
        when(request.getHeader("Authorization")).thenReturn("Bearer tokenNoSubject");
        when(jwtTokenProvider.validateJwtToken("tokenNoSubject")).thenReturn(true);
        when(jwtTokenProvider.getClaimsFromJwtToken("tokenNoSubject")).thenReturn(mock(io.jsonwebtoken.Claims.class));
        when(jwtTokenProvider.getClaimsFromJwtToken("tokenNoSubject").getSubject()).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }
}

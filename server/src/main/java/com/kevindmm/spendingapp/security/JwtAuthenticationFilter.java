package com.kevindmm.spendingapp.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger loggerAuthFilter = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService){
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Skip JWT processing for permitAll endpoints like /actuator/** and /api/auth/**
        if (request.getRequestURI().startsWith("/actuator/")
                || request.getRequestURI().startsWith("/api/auth/")
                || request.getRequestURI().startsWith("/api/spendings")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = getJwtFromRequest(request);

        if(StringUtils.hasText(jwt)  && jwtTokenProvider.validateJwtToken(jwt)) {
            String email = jwtTokenProvider.getClaimsFromJwtToken(jwt).getSubject(); // Assuming email is stored as subject
            if (email != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email); // Load user details using email

                UsernamePasswordAuthenticationToken authenticationToken = // Create authentication token
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Set details from request

                SecurityContextHolder.getContext().setAuthentication(authenticationToken); // Set authentication in security context
                loggerAuthFilter.info("Successful authentication for user: {}", email);
            }else{
                loggerAuthFilter.warn("JWT token has no subject claim");
            }
        }else {
            loggerAuthFilter.warn("Invalid or missing JWT token in request to {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response); // Continue the filter chain, why? Because other filters might need to process the request
    }

    private String getJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7); // Extract the token after "Bearer "
        }

        return null;
    }
}

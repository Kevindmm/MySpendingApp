package com.kevindmm.spendingapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityAuthConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityAuthConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http    // CSRF disabled: Stateless JWT API without session cookies.
                // Tokens are sent via Authorization header, not vulnerable to CSRF attacks.
            .csrf(AbstractHttpConfigurer::disable) //NOSONAR
            .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No session will be created or used by Spring Security
            .authorizeHttpRequests(
                    auth  -> auth
                        .antMatchers("/actuator/**").permitAll() // Allow unauthenticated access to actuator endpoints
                        .antMatchers("/api/auth/**").permitAll() // Allow unauthenticated access to auth endpoints
                        .antMatchers("/api/spendings").permitAll() // Allow unauthenticated access to spendings GET endpoint for Now...
                        .antMatchers("/api/test/**").authenticated() // for testing purposes
                        .anyRequest().authenticated() // All other requests require authentication
                    )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before the username/password authentication filter

        return http.build(); // Build the SecurityFilterChain
    }

    @Bean
    public PasswordEncoder passwordEncoder(){ // Password encoder bean
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager(); // Expose AuthenticationManager bean
    }
}

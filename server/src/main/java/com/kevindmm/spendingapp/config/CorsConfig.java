package com.kevindmm.spendingapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                     // every endpoint
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://127.0.0.1:3000",
                        "http://host.docker.internal:3000"
                )
                .allowedMethods("*")                   // GET, POST, etc.
                .allowedHeaders("*")
                .allowCredentials(true);               // allow cookies / auth headers
    }
}

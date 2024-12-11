package com.deeppurple.backend.config; // Adjust the package as necessary

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow CORS for all endpoints that start with /api/
                .allowedOrigins("http://localhost:3000") // The URL where your React app runs
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow specific HTTP methods
                .allowedHeaders("*")
                .allowCredentials(true); // Allow credentials (e.g., cookies or authorization headers)
    }
}

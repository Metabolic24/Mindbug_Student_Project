package com.mindbug;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Autorise tous les endpoints
                        .allowedOrigins("*") // Autorise toutes les origines (⚠ à limiter en prod)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Méthodes HTTP autorisées
                        .allowedHeaders("*"); // Autorise tous les headers
            }
        };
    }
}

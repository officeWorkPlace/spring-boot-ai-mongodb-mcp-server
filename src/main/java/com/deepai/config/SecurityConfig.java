package com.deepai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Security configuration for MongoDB MCP Server API.
 * Configures basic authentication with CSRF disabled for API testing.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for API endpoints to allow POST/PUT/DELETE operations
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
                .ignoringRequestMatchers("/actuator/**")
            )
            // Configure authorization
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/mongo/ping").permitAll()  // Allow ping without auth for health checks
                .requestMatchers("/actuator/health").permitAll() // Allow health check without auth
                .requestMatchers("/api/**").authenticated()      // Require auth for all other API endpoints
                .anyRequest().authenticated()
            )
            // Enable HTTP Basic authentication
            .httpBasic(withDefaults());

        return http.build();
    }
}

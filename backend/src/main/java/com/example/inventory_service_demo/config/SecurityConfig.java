package com.example.inventory_service_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Inventory Service Demo.
 * Provides basic authentication for API endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * INTENTIONAL VULNERABILITY: Hardcoded Credentials
     * 
     * This method contains hardcoded usernames and passwords directly in the source code.
     * This is a BLOCKER severity security vulnerability that should be detected by SonarQube.
     * 
     * SonarQube Rule: java:S2068 - Credentials should not be hard-coded
     * Severity: BLOCKER
     * 
     * Proper fix: Move credentials to environment variables or secure configuration
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // VULNERABILITY: Hardcoded admin credentials
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))  // BLOCKER: Hardcoded password
            .roles("ADMIN")
            .build();
        
        // VULNERABILITY: Hardcoded user credentials
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("password123"))  // BLOCKER: Hardcoded password
            .roles("USER")
            .build();
        
        // VULNERABILITY: Hardcoded API service account
        UserDetails apiService = User.builder()
            .username("api-service")
            .password(passwordEncoder().encode("SuperSecret2024!"))  // BLOCKER: Hardcoded password
            .roles("SERVICE")
            .build();
        
        return new InMemoryUserDetailsManager(admin, user, apiService);
    }
    
    /**
     * Password encoder bean for encrypting passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Configure HTTP security for the application.
     * Secures all /api/** endpoints with basic authentication.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disabled for demo purposes
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/service/**").hasRole("SERVICE")
                .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN", "SERVICE")
                .anyRequest().permitAll()
            )
            .httpBasic(basic -> {});
        
        return http.build();
    }
}

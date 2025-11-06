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
import org.springframework.beans.factory.annotation.Value;

/**
 * Security configuration for the Inventory Service Demo.
 * Provides basic authentication for API endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";
    private static final String ROLE_SERVICE = "SERVICE";
    
    @Value("${security.admin.password}")
    private String adminPassword;
    
    @Value("${security.user.password}")
    private String userPassword;
    
    @Value("${security.service.password}")
    private String servicePassword;
    
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
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode(adminPassword))
            .roles(ROLE_ADMIN)
            .build();
        
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode(userPassword))
            .roles(ROLE_USER)
            .build();
        
        UserDetails apiService = User.builder()
            .username("api-service")
            .password(passwordEncoder().encode(servicePassword))
            .roles(ROLE_SERVICE)
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
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").hasRole(ROLE_ADMIN)
                .requestMatchers("/api/service/**").hasRole(ROLE_SERVICE)
                .requestMatchers("/api/**").hasAnyRole(ROLE_USER, ROLE_ADMIN, ROLE_SERVICE)
                .anyRequest().permitAll()
            )
            .httpBasic(basic -> {});
        
        return http.build();
    }
}

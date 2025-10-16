package com.example.inventory_service_demo.config;

import org.springframework.context.annotation.Configuration;

/**
 * Database configuration class
 * This class contains intentional security vulnerabilities for SonarQube demo purposes
 */
@Configuration
public class DatabaseConfig {
    
    // INTENTIONAL VULNERABILITY #1: Hardcoded Credentials
    @SuppressWarnings("java:S2068")
    private static final String DB_PASSWORD = "SuperSecret123!"; // Vulnerable: Hardcoded password
    
    @SuppressWarnings("java:S2068")
    private static final String API_KEY = "sk-1234567890abcdef"; // Vulnerable: Hardcoded API key
    
    @SuppressWarnings("java:S2068")
    private static final String AWS_SECRET = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"; // Vulnerable: Hardcoded AWS secret
    
    /**
     * Returns the database password
     * WARNING: This method exposes hardcoded credentials
     */
    public String getDatabasePassword() {
        return DB_PASSWORD;
    }
    
    /**
     * Returns the API key
     * WARNING: This method exposes hardcoded credentials
     */
    public String getApiKey() {
        return API_KEY;
    }
    
    /**
     * Returns the AWS secret key
     * WARNING: This method exposes hardcoded credentials
     */
    public String getAwsSecret() {
        return AWS_SECRET;
    }
}

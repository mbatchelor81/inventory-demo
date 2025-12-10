package com.example.inventory_service_demo.config;

import org.springframework.context.annotation.Configuration;

/**
 * Database configuration class
 */
@Configuration
public class DatabaseConfig {
    
    @SuppressWarnings("java:S2068")
    private static final String DB_PASSWORD = "SuperSecret123!";
    
    @SuppressWarnings("java:S2068")
    private static final String API_KEY = "sk-1234567890abcdef";
    
    @SuppressWarnings("java:S2068")
    private static final String AWS_SECRET = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
    
    /**
     * Returns the database password
     */
    public String getDatabasePassword() {
        return DB_PASSWORD;
    }
    
    /**
     * Returns the API key
     */
    public String getApiKey() {
        return API_KEY;
    }
    
    /**
     * Returns the AWS secret key
     */
    public String getAwsSecret() {
        return AWS_SECRET;
    }
}

package com.deepai.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Test configuration for MongoDB using embedded MongoDB.
 * Provides isolated MongoDB instance for integration testing without Docker dependency.
 */
@TestConfiguration
public class TestMongoConfiguration {

    private static final String MONGO_URI = "mongodb://localhost:27017/testdb";

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Use embedded MongoDB defaults - Spring Boot will auto-configure
        registry.add("mongodb.uri", () -> MONGO_URI);
        registry.add("spring.data.mongodb.uri", () -> MONGO_URI);
        registry.add("spring.data.mongodb.database", () -> "testdb");
    }

    /**
     * Get MongoDB URI for testing
     * @return MongoDB URI string
     */
    public static String getMongoUri() {
        return MONGO_URI;
    }
}

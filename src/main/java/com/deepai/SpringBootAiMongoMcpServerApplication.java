package com.deepai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Spring Boot AI MongoDB MCP Server.
 * 
 * This application provides a Model Context Protocol (MCP) server with 45+ MongoDB operations
 * accessible to AI agents. The server includes:
 * - Core MongoDB operations (databases, collections, documents)
 * - Advanced analytics and aggregation tools
 * - AI-powered vector search and semantic analysis
 * 
 * Configuration is handled via application.properties for MongoDB connection settings.
 */
@SpringBootApplication
public class SpringBootAiMongoMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAiMongoMcpServerApplication.class, args);
    }

}
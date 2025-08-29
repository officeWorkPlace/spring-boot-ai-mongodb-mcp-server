package com.deepai.config;

import com.deepai.service.MongoAdvancedAnalyticsService;
import com.deepai.service.MongoAIService;
import com.deepai.service.MongoServiceClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Model Context Protocol (MCP) tools.
 * Registers all MongoDB operation tools for AI agent consumption.
 */
@Configuration
public class McpConfiguration {

    /**
     * Register all MongoDB service tools for AI agent consumption.
     * Provides 45+ comprehensive tools across three service categories:
     * - Core Operations: 20+ database, collection, and document operations
     * - Advanced Analytics: 15+ aggregation, indexing, and administration tools
     * - AI-Powered Tools: 10+ vector search, semantic analysis, and embeddings
     */
    @Bean
    public ToolCallbackProvider mongoTools(MongoServiceClient mongoServiceClient,
                                         MongoAdvancedAnalyticsService mongoAdvancedAnalyticsService,
                                         MongoAIService mongoAIService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mongoServiceClient, mongoAdvancedAnalyticsService, mongoAIService)
                .build();
    }
}
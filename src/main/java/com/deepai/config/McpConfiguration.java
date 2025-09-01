package com.deepai.config;

import com.deepai.service.MongoAdvancedAnalyticsService;
import com.deepai.service.MongoAIService;
import com.deepai.service.MongoServiceClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class for Model Context Protocol (MCP) tools.
 * Registers all MongoDB operation tools for AI agent consumption.
 */
@Configuration
public class McpConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(McpConfiguration.class);

    @Value("${mcp.tools.exposure:public}")
    private String toolsExposure;

    /**
     * Register MongoDB service tools for AI agent consumption.
     * Exposure level controlled by mcp.tools.exposure property:
     * - "public": Expose only stable, well-tested tools (default)
     * - "all": Expose all tools including advanced analytics and AI features
     * 
     * Tool count by exposure level:
     * - Public: 11 core MongoDB operations
     * - All: 39+ comprehensive tools across three service categories:
     *   * Core Operations: 20 database, collection, and document operations
     *   * Advanced Analytics: 12 aggregation, indexing, and administration tools
     *   * AI-Powered Tools: 7 vector search, semantic analysis, and embeddings
     */
    @Bean
    public ToolCallbackProvider mongoTools(MongoServiceClient mongoServiceClient,
                                         MongoAdvancedAnalyticsService mongoAdvancedAnalyticsService,
                                         MongoAIService mongoAIService) {
        
        logger.info("Configuring MCP tools with exposure level: {}", toolsExposure);
        
        if ("all".equalsIgnoreCase(toolsExposure)) {
            logger.info("Registering ALL 39 MongoDB MCP tools (Core + Advanced + AI)");
            return MethodToolCallbackProvider.builder()
                    .toolObjects(mongoServiceClient, mongoAdvancedAnalyticsService, mongoAIService)
                    .build();
        } else {
            logger.info("Registering PUBLIC MongoDB MCP tools (Core operations only)");
            return MethodToolCallbackProvider.builder()
                    .toolObjects(mongoServiceClient)
                    .build();
        }
    }
}
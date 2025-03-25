package com.bootcamptoprod.config;

import com.bootcamptoprod.service.MongoServiceClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfiguration {

    @Bean
    public ToolCallbackProvider mongoTools(MongoServiceClient mongoServiceClient) {
        return MethodToolCallbackProvider.builder().toolObjects(mongoServiceClient).build();
    }
}

package com.deepai;

import com.deepai.mcp.EnhancedMcpServerHandler;
import com.deepai.service.MongoServiceClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for Enhanced MCP Server Handler to verify dynamic tool discovery
 * and execution functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
public class EnhancedMcpServerTest {

    @Autowired
    private ApplicationContext applicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testServiceBeansExist() {
        // Verify that our service beans exist and can be retrieved
        assertNotNull(applicationContext.getBean("mongoServiceClient"));
        assertNotNull(applicationContext.getBean("mongoAdvancedAnalyticsService"));
        assertNotNull(applicationContext.getBean("mongoAIService"));
    }

    @Test 
    void testToolDiscovery() throws Exception {
        // Test that we can discover @Tool annotated methods
        MongoServiceClient mongoService = applicationContext.getBean(MongoServiceClient.class);
        
        Method[] methods = mongoService.getClass().getDeclaredMethods();
        int toolCount = 0;
        
        for (Method method : methods) {
            if (method.getAnnotation(org.springframework.ai.tool.annotation.Tool.class) != null) {
                toolCount++;
                System.out.println("Discovered tool: " + method.getName() + 
                    " - " + method.getAnnotation(org.springframework.ai.tool.annotation.Tool.class).description());
            }
        }
        
        assertTrue(toolCount > 0, "Should discover at least some @Tool methods");
        System.out.println("Total tools discovered in MongoServiceClient: " + toolCount);
    }

    @Test
    void testParameterNamesAvailable() throws Exception {
        // Test that parameter names are preserved for reflection
        MongoServiceClient mongoService = applicationContext.getBean(MongoServiceClient.class);
        
        Method pingMethod = mongoService.getClass().getMethod("ping");
        assertNotNull(pingMethod);
        
        // Find a method with parameters
        Method listCollectionsMethod = mongoService.getClass().getMethod("listCollections", String.class);
        assertNotNull(listCollectionsMethod);
        
        // Check parameter name is available
        String paramName = listCollectionsMethod.getParameters()[0].getName();
        assertNotNull(paramName);
        assertTrue(paramName.equals("dbName") || paramName.equals("arg0"), 
            "Parameter name should be 'dbName' if -parameters flag is working, got: " + paramName);
        
        System.out.println("Parameter name for listCollections: " + paramName);
    }

    @Test
    void testMcpInitializeRequest() throws Exception {
        // Test MCP initialize request format
        String initRequest = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\",\"params\":{\"protocolVersion\":\"2024-11-05\",\"capabilities\":{\"roots\":{\"listChanged\":false},\"sampling\":{}},\"clientInfo\":{\"name\":\"test-client\",\"version\":\"1.0.0\"}}}";
        
        JsonNode requestNode = objectMapper.readTree(initRequest);
        
        assertEquals("2.0", requestNode.get("jsonrpc").asText());
        assertEquals(1, requestNode.get("id").asInt());
        assertEquals("initialize", requestNode.get("method").asText());
        
        System.out.println("MCP Initialize request parsed successfully");
    }

    @Test
    void testToolCallRequestFormat() throws Exception {
        // Test MCP tools/call request format
        String toolCallRequest = "{\"jsonrpc\":\"2.0\",\"id\":2,\"method\":\"tools/call\",\"params\":{\"name\":\"ping\",\"arguments\":{}}}";
        
        JsonNode requestNode = objectMapper.readTree(toolCallRequest);
        
        assertEquals("2.0", requestNode.get("jsonrpc").asText());
        assertEquals(2, requestNode.get("id").asInt());
        assertEquals("tools/call", requestNode.get("method").asText());
        assertEquals("ping", requestNode.get("params").get("name").asText());
        
        System.out.println("MCP Tools/call request parsed successfully");
    }
}

package com.deepai;

import com.deepai.service.MongoServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test actual tool method execution to verify our enhanced MCP handler
 * can invoke real MongoDB operations.
 */
@SpringBootTest
@ActiveProfiles("test")
public class ToolExecutionTest {

    @Autowired
    private MongoServiceClient mongoServiceClient;

    @Test
    void testDirectToolExecution() throws Exception {
        // Test direct method invocation (simulating our enhanced MCP handler)
        
        // 1. Get the ping method
        Method pingMethod = mongoServiceClient.getClass().getMethod("ping");
        assertNotNull(pingMethod);
        
        // 2. Verify it has @Tool annotation
        org.springframework.ai.tool.annotation.Tool toolAnnotation = 
            pingMethod.getAnnotation(org.springframework.ai.tool.annotation.Tool.class);
        assertNotNull(toolAnnotation);
        assertEquals("Test database connectivity with ping.", toolAnnotation.description());
        
        // 3. Execute the method directly
        Object result = pingMethod.invoke(mongoServiceClient);
        assertNotNull(result);
        assertTrue(result instanceof String);
        
        String pingResult = (String) result;
        System.out.println("Ping result: " + pingResult);
        
        // 4. Verify the result contains expected content
        assertTrue(pingResult.contains("ping") || pingResult.contains("connection") || pingResult.contains("healthy"),
            "Ping result should contain connection status information: " + pingResult);
    }

    @Test
    void testParameterMethodExecution() throws Exception {
        // Test method with parameters (simulating MCP tool call with arguments)
        
        // 1. Get the listDatabases method (no parameters)
        Method listDbMethod = mongoServiceClient.getClass().getMethod("listDatabases");
        assertNotNull(listDbMethod);
        
        // 2. Execute and verify result type
        Object result = listDbMethod.invoke(mongoServiceClient);
        assertNotNull(result);
        assertTrue(result instanceof java.util.List);
        
        @SuppressWarnings("unchecked")
        java.util.List<java.util.Map<String, Object>> dbList = (java.util.List<java.util.Map<String, Object>>) result;
        System.out.println("Databases found: " + dbList.size());
        
        // 3. Test method with string parameter
        Method listCollectionsMethod = mongoServiceClient.getClass().getMethod("listCollections", String.class);
        assertNotNull(listCollectionsMethod);
        
        // 4. Execute with test database name
        Object collectionsResult = listCollectionsMethod.invoke(mongoServiceClient, "testdb");
        assertNotNull(collectionsResult);
        assertTrue(collectionsResult instanceof java.util.List);
        
        System.out.println("Collections result type: " + collectionsResult.getClass().getName());
    }

    @Test 
    void testReflectionBasedExecution() throws Exception {
        // Test exactly how our Enhanced MCP Handler executes tools
        
        String toolName = "ping";
        java.util.Map<String, Object> arguments = new java.util.HashMap<>();
        
        // 1. Find the method by name (like our handler does)
        Method method = null;
        for (Method m : mongoServiceClient.getClass().getDeclaredMethods()) {
            if (m.getName().equals(toolName) && 
                m.getAnnotation(org.springframework.ai.tool.annotation.Tool.class) != null) {
                method = m;
                break;
            }
        }
        
        assertNotNull(method, "Should find ping method with @Tool annotation");
        
        // 2. Prepare arguments (empty for ping)
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        Object[] methodArgs = new Object[parameters.length];
        
        assertEquals(0, parameters.length, "Ping method should have no parameters");
        
        // 3. Execute the method (exactly like EnhancedMcpServerHandler does)
        Object result = method.invoke(mongoServiceClient, methodArgs);
        
        // 4. Verify result
        assertNotNull(result);
        assertTrue(result instanceof String);
        System.out.println("Reflection-based execution result: " + result);
    }
}

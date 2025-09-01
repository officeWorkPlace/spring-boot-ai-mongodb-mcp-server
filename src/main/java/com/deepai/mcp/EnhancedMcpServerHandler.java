package com.deepai.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Enhanced MCP Server Handler with Dynamic Spring AI Tool Discovery and Execution.
 * This handler automatically discovers @Tool annotated methods and executes them dynamically.
 */
@Component
@Profile("mcp")
public class EnhancedMcpServerHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedMcpServerHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PrintWriter stdout;
    private final BufferedReader stdin;
    private boolean initialized = false;
    
    // Cache for discovered tools
    private final Map<String, ToolMetadata> discoveredTools = new HashMap<>();
    private final Map<String, Object> serviceInstances = new HashMap<>();
    
    @Autowired
    private ToolCallbackProvider toolCallbackProvider;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public EnhancedMcpServerHandler() {
        this.stdout = new PrintWriter(System.out, true);
        this.stdin = new BufferedReader(new InputStreamReader(System.in));
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void startMcpServer() {
        logger.info("Starting Enhanced MCP Server Handler with Dynamic Tool Discovery");
        
        // Discover all @Tool annotated methods
        discoverTools();
        
        logger.info("Discovered {} MongoDB tools from Spring AI annotations", discoveredTools.size());
        
        CompletableFuture.runAsync(() -> {
            try {
                handleMcpCommunication();
            } catch (Exception e) {
                logger.error("MCP Server error: ", e);
            }
        });
    }
    
    /**
     * Dynamically discover all @Tool annotated methods from Spring beans
     */
    private void discoverTools() {
        // Get service beans that contain @Tool methods
        String[] serviceNames = {"mongoServiceClient", "mongoAdvancedAnalyticsService", "mongoAIService"};
        
        for (String serviceName : serviceNames) {
            try {
                Object serviceBean = applicationContext.getBean(serviceName);
                serviceInstances.put(serviceName, serviceBean);
                
                // Scan all methods for @Tool annotations
                Method[] methods = serviceBean.getClass().getDeclaredMethods();
                for (Method method : methods) {
                    Tool toolAnnotation = method.getAnnotation(Tool.class);
                    if (toolAnnotation != null) {
                        String toolName = method.getName();
                        ToolMetadata metadata = new ToolMetadata();
                        metadata.methodName = toolName;
                        metadata.description = toolAnnotation.description();
                        metadata.method = method;
                        metadata.serviceInstance = serviceBean;
                        metadata.parameters = extractParameterMetadata(method);
                        
                        discoveredTools.put(toolName, metadata);
                        logger.debug("Discovered tool: {} - {}", toolName, metadata.description);
                    }
                }
                
                logger.info("Scanned service '{}' and found {} tools", 
                    serviceName, discoveredTools.size());
                    
            } catch (Exception e) {
                logger.warn("Could not scan service '{}' for tools: {}", serviceName, e.getMessage());
            }
        }
    }
    
    /**
     * Extract parameter metadata for tool schema generation
     */
    private List<ParameterMetadata> extractParameterMetadata(Method method) {
        List<ParameterMetadata> paramList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        
        for (Parameter param : parameters) {
            ParameterMetadata paramMeta = new ParameterMetadata();
            paramMeta.name = param.getName();
            paramMeta.type = param.getType();
            paramMeta.required = !param.getType().isPrimitive() && 
                               !param.isAnnotationPresent(org.springframework.lang.Nullable.class);
            paramList.add(paramMeta);
        }
        
        return paramList;
    }
    
    private void handleMcpCommunication() {
        try {
            String line;
            while ((line = stdin.readLine()) != null) {
                try {
                    JsonNode request = objectMapper.readTree(line);
                    logger.debug("Received MCP request: {}", request.toString());
                    JsonNode response = processRequest(request);
                    if (response != null) {
                        logger.debug("Sending MCP response: {}", response.toString());
                        stdout.println(objectMapper.writeValueAsString(response));
                        stdout.flush();
                    }
                } catch (Exception e) {
                    logger.error("Error processing MCP request: ", e);
                    sendErrorResponse(null, -1, "Internal error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Error reading from stdin: ", e);
        }
    }
    
    private JsonNode processRequest(JsonNode request) {
        Long id = request.has("id") ? request.get("id").asLong() : null;
        String method = request.has("method") ? request.get("method").asText() : null;
        JsonNode params = request.get("params");
        
        if (method == null) {
            return null; // Notification or invalid request
        }
        
        try {
            return switch (method) {
                case "initialize" -> handleInitialize(id, params);
                case "tools/list" -> handleDynamicToolsList(id);
                case "tools/call" -> handleDynamicToolCall(id, params);
                case "resources/list" -> handleResourcesList(id);
                case "resources/read" -> handleResourceRead(id, params);
                default -> createErrorResponse(id, -32601, "Method not found: " + method);
            };
        } catch (Exception e) {
            logger.error("Error handling method {}: ", method, e);
            return createErrorResponse(id, -32603, "Internal error: " + e.getMessage());
        }
    }
    
    private JsonNode handleInitialize(Long id, JsonNode params) {
        initialized = true;
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        
        ObjectNode result = objectMapper.createObjectNode();
        result.put("protocolVersion", "2024-11-05");
        
        ObjectNode capabilities = objectMapper.createObjectNode();
        ObjectNode tools = objectMapper.createObjectNode();
        tools.put("listChanged", true);
        capabilities.set("tools", tools);
        
        ObjectNode resources = objectMapper.createObjectNode();
        resources.put("subscribe", false);
        resources.put("listChanged", true);
        capabilities.set("resources", resources);
        
        result.set("capabilities", capabilities);
        
        ObjectNode serverInfo = objectMapper.createObjectNode();
        serverInfo.put("name", "mongo-mcp-server");
        serverInfo.put("version", "0.0.1");
        serverInfo.put("description", "MongoDB MCP Server with Dynamic Spring AI Tools Integration");
        result.set("serverInfo", serverInfo);
        
        response.set("result", result);
        return response;
    }
    
    /**
     * FIXED: Dynamic tool discovery instead of hardcoded list
     */
    private JsonNode handleDynamicToolsList(Long id) {
        logger.info("Handling dynamic tools/list request with {} discovered tools", discoveredTools.size());
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        
        ObjectNode result = objectMapper.createObjectNode();
        ArrayNode toolsArray = objectMapper.createArrayNode();
        
        // Generate tool definitions from discovered @Tool methods
        for (Map.Entry<String, ToolMetadata> entry : discoveredTools.entrySet()) {
            String toolName = entry.getKey();
            ToolMetadata metadata = entry.getValue();
            
            ObjectNode tool = objectMapper.createObjectNode();
            tool.put("name", toolName);
            tool.put("description", metadata.description);
            
            // Generate JSON schema for parameters
            ObjectNode inputSchema = generateToolSchema(metadata);
            tool.set("inputSchema", inputSchema);
            
            toolsArray.add(tool);
        }
        
        result.set("tools", toolsArray);
        response.set("result", result);
        
        logger.info("Returning {} dynamically discovered tools", toolsArray.size());
        return response;
    }
    
    /**
     * Generate JSON schema for tool parameters
     */
    private ObjectNode generateToolSchema(ToolMetadata metadata) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        
        ObjectNode properties = objectMapper.createObjectNode();
        ArrayNode required = objectMapper.createArrayNode();
        
        for (ParameterMetadata param : metadata.parameters) {
            ObjectNode propSchema = objectMapper.createObjectNode();
            
            // Map Java types to JSON Schema types
            String jsonType = mapJavaTypeToJsonType(param.type);
            propSchema.put("type", jsonType);
            
            if (param.type == String.class) {
                propSchema.put("description", "String parameter for " + param.name);
            } else if (param.type == int.class || param.type == Integer.class) {
                propSchema.put("description", "Integer parameter for " + param.name);
            } else if (param.type == long.class || param.type == Long.class) {
                propSchema.put("description", "Long integer parameter for " + param.name);
            }
            
            properties.set(param.name, propSchema);
            
            if (param.required) {
                required.add(param.name);
            }
        }
        
        schema.set("properties", properties);
        if (required.size() > 0) {
            schema.set("required", required);
        }
        
        return schema;
    }
    
    private String mapJavaTypeToJsonType(Class<?> javaType) {
        if (javaType == String.class) return "string";
        if (javaType == int.class || javaType == Integer.class) return "integer";
        if (javaType == long.class || javaType == Long.class) return "integer";
        if (javaType == double.class || javaType == Double.class) return "number";
        if (javaType == boolean.class || javaType == Boolean.class) return "boolean";
        if (List.class.isAssignableFrom(javaType)) return "array";
        if (Map.class.isAssignableFrom(javaType)) return "object";
        return "string"; // Default fallback
    }
    
    /**
     * FIXED: Dynamic tool execution instead of mock responses
     */
    private JsonNode handleDynamicToolCall(Long id, JsonNode params) {
        logger.info("Handling dynamic tool call with id: {}, params: {}", id, params);
        
        if (!params.has("name")) {
            logger.error("Missing tool name in params: {}", params);
            return createErrorResponse(id, -32602, "Missing tool name");
        }
        
        String toolName = params.get("name").asText();
        ToolMetadata toolMetadata = discoveredTools.get(toolName);
        
        if (toolMetadata == null) {
            logger.error("Tool not found: {}", toolName);
            return createErrorResponse(id, -32601, "Tool not found: " + toolName);
        }
        
        // Extract arguments from MCP request
        Map<String, Object> arguments = new HashMap<>();
        if (params.has("arguments")) {
            JsonNode argsNode = params.get("arguments");
            arguments = objectMapper.convertValue(argsNode, Map.class);
        }
        
        logger.info("Executing tool: {} with arguments: {}", toolName, arguments);
        
        try {
            // FIXED: Call actual Spring AI tool method
            Object result = executeToolMethod(toolMetadata, arguments);
            
            ObjectNode response = objectMapper.createObjectNode();
            response.put("jsonrpc", "2.0");
            response.put("id", id);
            
            ObjectNode resultNode = objectMapper.createObjectNode();
            ArrayNode content = objectMapper.createArrayNode();
            
            ObjectNode contentItem = objectMapper.createObjectNode();
            contentItem.put("type", "text");
            
            // Convert result to JSON string
            String resultJson = objectMapper.writeValueAsString(result);
            contentItem.put("text", resultJson);
            content.add(contentItem);
            
            resultNode.set("content", content);
            resultNode.put("isError", false);
            
            response.set("result", resultNode);
            
            logger.info("Tool {} executed successfully", toolName);
            return response;
            
        } catch (Exception e) {
            logger.error("Error executing tool {}: ", toolName, e);
            return createErrorResponse(id, -32603, "Tool execution failed: " + e.getMessage());
        }
    }
    
    /**
     * Execute the actual Spring AI tool method using reflection
     */
    private Object executeToolMethod(ToolMetadata metadata, Map<String, Object> arguments) throws Exception {
        Method method = metadata.method;
        Object serviceInstance = metadata.serviceInstance;
        
        // Prepare method arguments
        Parameter[] parameters = method.getParameters();
        Object[] methodArgs = new Object[parameters.length];
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            String paramName = param.getName();
            Class<?> paramType = param.getType();
            
            Object argValue = arguments.get(paramName);
            
            if (argValue != null) {
                // Type conversion if necessary
                if (paramType == String.class) {
                    methodArgs[i] = argValue.toString();
                } else if (paramType == int.class || paramType == Integer.class) {
                    methodArgs[i] = argValue instanceof Number ? 
                        ((Number) argValue).intValue() : Integer.parseInt(argValue.toString());
                } else if (paramType == long.class || paramType == Long.class) {
                    methodArgs[i] = argValue instanceof Number ? 
                        ((Number) argValue).longValue() : Long.parseLong(argValue.toString());
                } else if (paramType == double.class || paramType == Double.class) {
                    methodArgs[i] = argValue instanceof Number ? 
                        ((Number) argValue).doubleValue() : Double.parseDouble(argValue.toString());
                } else if (paramType == boolean.class || paramType == Boolean.class) {
                    methodArgs[i] = argValue instanceof Boolean ? 
                        (Boolean) argValue : Boolean.parseBoolean(argValue.toString());
                } else {
                    methodArgs[i] = argValue;
                }
            } else {
                // Handle default values for primitive types
                if (paramType.isPrimitive()) {
                    if (paramType == int.class) methodArgs[i] = 0;
                    else if (paramType == long.class) methodArgs[i] = 0L;
                    else if (paramType == double.class) methodArgs[i] = 0.0;
                    else if (paramType == boolean.class) methodArgs[i] = false;
                } else {
                    methodArgs[i] = null;
                }
            }
        }
        
        // Invoke the method
        return method.invoke(serviceInstance, methodArgs);
    }
    
    private JsonNode handleResourcesList(Long id) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        
        ObjectNode result = objectMapper.createObjectNode();
        ArrayNode resources = objectMapper.createArrayNode();
        
        ObjectNode resource = objectMapper.createObjectNode();
        resource.put("uri", "mongo://info");
        resource.put("name", "MongoDB Server Information");
        resource.put("description", "Information about the MongoDB server");
        resource.put("mimeType", "application/json");
        resources.add(resource);
        
        result.set("resources", resources);
        response.set("result", result);
        return response;
    }
    
    private JsonNode handleResourceRead(Long id, JsonNode params) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        
        ObjectNode result = objectMapper.createObjectNode();
        result.put("uri", params.get("uri").asText());
        result.put("mimeType", "application/json");
        
        Map<String, Object> serverInfo = new HashMap<>();
        serverInfo.put("status", "MongoDB MCP Server Active");
        serverInfo.put("tools_discovered", discoveredTools.size());
        serverInfo.put("services_scanned", serviceInstances.keySet());
        serverInfo.put("integration", "Spring AI Dynamic Discovery");
        
        try {
            result.put("text", objectMapper.writeValueAsString(serverInfo));
        } catch (Exception e) {
            result.put("text", "{\"error\":\"Failed to serialize server info\"}");
        }
        
        response.set("result", result);
        return response;
    }
    
    private JsonNode createErrorResponse(Long id, int code, String message) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        if (id != null) {
            response.put("id", id);
        }
        
        ObjectNode error = objectMapper.createObjectNode();
        error.put("code", code);
        error.put("message", message);
        response.set("error", error);
        
        return response;
    }
    
    private void sendErrorResponse(Long id, int code, String message) {
        try {
            JsonNode errorResponse = createErrorResponse(id, code, message);
            stdout.println(objectMapper.writeValueAsString(errorResponse));
            stdout.flush();
        } catch (Exception e) {
            logger.error("Error sending error response: ", e);
        }
    }
    
    // Helper classes for metadata storage
    private static class ToolMetadata {
        String methodName;
        String description;
        Method method;
        Object serviceInstance;
        List<ParameterMetadata> parameters;
    }
    
    private static class ParameterMetadata {
        String name;
        Class<?> type;
        boolean required;
    }
}

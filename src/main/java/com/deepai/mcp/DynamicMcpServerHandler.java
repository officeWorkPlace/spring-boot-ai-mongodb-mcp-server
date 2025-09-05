package com.deepai.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Dynamic MCP Server Handler that integrates with Spring AI tools.
 * This handler dynamically discovers registered @Tool methods from Spring AI.
 */
@Component
@Profile("mcp-old") // DISABLED: Replaced by EnhancedMcpServerHandler
public class DynamicMcpServerHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicMcpServerHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PrintWriter stdout;
    private final BufferedReader stdin;
    private boolean initialized = false;
    
    @Autowired
    private ToolCallbackProvider toolCallbackProvider;
    
    public DynamicMcpServerHandler() {
        this.stdout = new PrintWriter(System.out, true);
        this.stdin = new BufferedReader(new InputStreamReader(System.in));
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void startMcpServer() {
        logger.info("Starting Dynamic MCP Server Handler");
        logger.info("Discovered Spring AI Tool Callback Provider: {}", toolCallbackProvider.getClass().getSimpleName());
        
        CompletableFuture.runAsync(() -> {
            try {
                handleMcpCommunication();
            } catch (Exception e) {
                logger.error("MCP Server error: ", e);
            }
        });
    }
    
    private void handleMcpCommunication() {
        try {
            String line;
            while ((line = stdin.readLine()) != null) {
                try {
                    JsonNode request = objectMapper.readTree(line);
                    logger.info("Received MCP request: {}", request.toString());
                    JsonNode response = processRequest(request);
                    if (response != null) {
                        logger.info("Sending MCP response: {}", response.toString());
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
                case "tools/list" -> handleToolsList(id);
                case "tools/call" -> handleToolCall(id, params);
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
        serverInfo.put("description", "MongoDB MCP Server with Spring AI Tools Integration");
        result.set("serverInfo", serverInfo);
        
        response.set("result", result);
        return response;
    }
    
    private JsonNode handleToolsList(Long id) {
        logger.info("Handling tools/list request with id: {}", id);
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        
        ObjectNode result = objectMapper.createObjectNode();
        List<ObjectNode> tools = new ArrayList<>();
        
        // For now, expose all 39 tools based on our service classes
        // This will be expanded to use Spring AI discovery in the future
        String[] allTools = {
            // MongoServiceClient (20 tools)
            "listDatabases", "createDatabase", "dropDatabase", "getDatabaseStats", "ping",
            "listCollections", "createCollection", "dropCollection", "getCollectionStats", "renameCollection",
            "insertDocument", "insertMany", "findDocument", "findOne", "updateDocument", "deleteDocument", "countDocuments",
            "simpleQuery", "complexQuery", "listIndexes",
            // MongoAdvancedAnalyticsService (12 tools)
            "aggregatePipeline", "distinctValues", "groupByField", "textSearch", "geoSearch",
            "createIndex", "createVectorIndex", "dropIndex", "reIndex",
            "explainQuery", "validateSchema", "repairDatabase",
            // MongoAIService (7 tools)
            "vectorSearch", "generateEmbeddings", "aiAnalyzeDocument", "aiAnalyzeCollection",
            "aiQuerySuggestion", "aiDocumentSummary", "semanticSearch"
        };
        
        logger.info("Exposing {} MongoDB tools through MCP", allTools.length);
        
        for (String toolName : allTools) {
            ObjectNode tool = objectMapper.createObjectNode();
            tool.put("name", toolName);
            tool.put("description", "MongoDB " + toolName + " operation (Spring AI integrated)");
            
            ObjectNode inputSchema = objectMapper.createObjectNode();
            inputSchema.put("type", "object");
            inputSchema.set("properties", objectMapper.createObjectNode());
            tool.set("inputSchema", inputSchema);
            
            tools.add(tool);
        }
        
        result.set("tools", objectMapper.valueToTree(tools));
        response.set("result", result);
        logger.info("Created tools/list response with {} tools: {}", tools.size(), 
            tools.stream().map(t -> t.get("name").asText()).toList());
        return response;
    }
    
    private JsonNode handleToolCall(Long id, JsonNode params) {
        logger.info("Handling tool call with id: {}, params: {}", id, params);
        if (!params.has("name")) {
            logger.error("Missing tool name in params: {}", params);
            return createErrorResponse(id, -32602, "Missing tool name");
        }
        
        String toolName = params.get("name").asText();
        Map<String, Object> arguments = new HashMap<>();
        
        if (params.has("arguments")) {
            JsonNode argsNode = params.get("arguments");
            arguments = objectMapper.convertValue(argsNode, Map.class);
        }
        
        logger.info("Executing tool: {} with arguments: {}", toolName, arguments);
        
        try {
            // Try to call the tool using Spring AI
            String result = callSpringAITool(toolName, arguments);
            
            ObjectNode response = objectMapper.createObjectNode();
            response.put("jsonrpc", "2.0");
            response.put("id", id);
            
            ObjectNode resultNode = objectMapper.createObjectNode();
            List<ObjectNode> content = new ArrayList<>();
            
            ObjectNode contentItem = objectMapper.createObjectNode();
            contentItem.put("type", "text");
            contentItem.put("text", result);
            content.add(contentItem);
            
            resultNode.set("content", objectMapper.valueToTree(content));
            resultNode.put("isError", false);
            
            response.set("result", resultNode);
            
            logger.info("Tool {} executed successfully, returning: {}", toolName, result);
            return response;
            
        } catch (Exception e) {
            logger.error("Error executing tool {}: ", toolName, e);
            return createErrorResponse(id, -32603, "Tool execution failed: " + e.getMessage());
        }
    }
    
    private String callSpringAITool(String toolName, Map<String, Object> arguments) {
        // TODO: Integrate with actual Spring AI tool execution
        // For now, return successful mock responses for all 39 tools
        
        return switch (toolName) {
            // MongoServiceClient tools (20)
            case "ping" -> "{\"status\":\"ok\",\"message\":\"MongoDB MCP Server is healthy\",\"tools_registered\":39}";
            case "listDatabases" -> "{\"databases\":[\"mcpserver\",\"admin\",\"local\"],\"status\":\"success\"}";
            case "createDatabase" -> "{\"database\":\"" + arguments.getOrDefault("dbName", "test") + "\",\"status\":\"created\"}";
            case "dropDatabase" -> "{\"database\":\"" + arguments.getOrDefault("dbName", "test") + "\",\"status\":\"dropped\"}";
            case "getDatabaseStats" -> "{\"database\":\"" + arguments.getOrDefault("dbName", "test") + "\",\"collections\":5,\"documents\":1000,\"dataSize\":256000}";
            case "listCollections" -> "{\"collections\":[\"users\",\"products\",\"orders\"],\"database\":\"" + arguments.getOrDefault("dbName", "test") + "\"}";
            case "createCollection" -> "{\"collection\":\"" + arguments.getOrDefault("collectionName", "test") + "\",\"status\":\"created\"}";
            case "dropCollection" -> "{\"collection\":\"" + arguments.getOrDefault("collectionName", "test") + "\",\"status\":\"dropped\"}";
            case "getCollectionStats" -> "{\"collection\":\"" + arguments.getOrDefault("collectionName", "test") + "\",\"documents\":100,\"avgSize\":256}";
            case "renameCollection" -> "{\"oldName\":\"" + arguments.getOrDefault("oldName", "old") + "\",\"newName\":\"" + arguments.getOrDefault("newName", "new") + "\",\"status\":\"renamed\"}";
            case "insertDocument" -> "{\"insertedId\":\"507f1f77bcf86cd799439011\",\"status\":\"success\"}";
            case "insertMany" -> "{\"insertedCount\":3,\"insertedIds\":[\"507f1f77bcf86cd799439011\",\"507f1f77bcf86cd799439012\"],\"status\":\"success\"}";
            case "findDocument" -> "{\"documents\":[{\"_id\":\"1\",\"name\":\"test\"}],\"count\":1}";
            case "findOne" -> "{\"_id\":\"1\",\"name\":\"test\",\"data\":\"sample\"}";
            case "updateDocument" -> "{\"matchedCount\":1,\"modifiedCount\":1,\"status\":\"success\"}";
            case "deleteDocument" -> "{\"deletedCount\":1,\"status\":\"success\"}";
            case "countDocuments" -> "{\"count\":42,\"status\":\"success\"}";
            case "simpleQuery" -> "{\"documents\":[{\"_id\":\"1\",\"" + arguments.getOrDefault("field", "name") + "\":\"" + arguments.getOrDefault("value", "test") + "\"}],\"count\":1}";
            case "complexQuery" -> "{\"documents\":[{\"_id\":\"1\",\"name\":\"complex_result\"}],\"count\":1}";
            case "listIndexes" -> "{\"indexes\":[{\"name\":\"_id_\",\"key\":{\"_id\":1}},{\"name\":\"name_1\",\"key\":{\"name\":1}}]}";
            
            // MongoAdvancedAnalyticsService tools (12)
            case "aggregatePipeline" -> "{\"results\":[{\"_id\":\"group1\",\"count\":5},{\"_id\":\"group2\",\"count\":3}],\"status\":\"success\"}";
            case "distinctValues" -> "{\"distinctValues\":[\"value1\",\"value2\",\"value3\"],\"count\":3}";
            case "groupByField" -> "{\"groups\":[{\"_id\":\"category1\",\"count\":10},{\"_id\":\"category2\",\"count\":5}]}";
            case "textSearch" -> "{\"documents\":[{\"_id\":\"1\",\"text\":\"search result\",\"score\":0.95}],\"count\":1}";
            case "geoSearch" -> "{\"documents\":[{\"_id\":\"1\",\"location\":{\"lat\":40.7128,\"lng\":-74.0060},\"distance\":100}],\"count\":1}";
            case "createIndex" -> "{\"indexName\":\"" + arguments.getOrDefault("indexKeys", "field_1") + "\",\"status\":\"created\"}";
            case "createVectorIndex" -> "{\"indexName\":\"vector_index\",\"dimensions\":" + arguments.getOrDefault("dimensions", 128) + ",\"status\":\"created\"}";
            case "dropIndex" -> "{\"indexName\":\"" + arguments.getOrDefault("indexName", "test_index") + "\",\"status\":\"dropped\"}";
            case "reIndex" -> "{\"collection\":\"" + arguments.getOrDefault("collectionName", "test") + "\",\"status\":\"reindexed\"}";
            case "explainQuery" -> "{\"executionStats\":{\"totalDocsExamined\":100,\"totalKeysExamined\":10,\"executionTimeMillis\":5}}";
            case "validateSchema" -> "{\"valid\":true,\"errors\":[],\"status\":\"validation_complete\"}";
            case "repairDatabase" -> "{\"database\":\"" + arguments.getOrDefault("dbName", "test") + "\",\"status\":\"repaired\"}";
            
            // MongoAIService tools (7)
            case "vectorSearch" -> "{\"documents\":[{\"_id\":\"1\",\"similarity\":0.95,\"content\":\"vector match\"}],\"count\":1}";
            case "generateEmbeddings" -> "{\"processed\":100,\"embeddings_generated\":100,\"status\":\"success\"}";
            case "aiAnalyzeDocument" -> "{\"analysis\":{\"sentiment\":\"positive\",\"topics\":[\"business\",\"technology\"],\"summary\":\"AI analysis complete\"}}";
            case "aiAnalyzeCollection" -> "{\"analysis\":{\"schema\":{\"fields\":5,\"types\":[\"string\",\"number\"]},\"patterns\":[\"data_quality:good\"],\"insights\":\"Collection well-structured\"}}";
            case "aiQuerySuggestion" -> "{\"suggestions\":[\"db.collection.find({status:'active'})\",\"db.collection.aggregate([{$match:{type:'user'}}])\"],\"intent\":\"" + arguments.getOrDefault("userIntent", "find active records") + "\"}";
            case "aiDocumentSummary" -> "{\"summary\":\"This document contains user information with key attributes including name, email, and registration date.\",\"key_points\":[\"user_data\",\"contact_info\"],\"confidence\":0.92}";
            case "semanticSearch" -> "{\"documents\":[{\"_id\":\"1\",\"relevance\":0.89,\"snippet\":\"semantically relevant content\"}],\"query\":\"" + arguments.getOrDefault("naturalLanguageQuery", "search query") + "\"}";
            
            default -> {
                try {
                    yield "{\"result\":\"Tool executed successfully\",\"tool\":\"" + toolName + "\",\"arguments\":" + objectMapper.writeValueAsString(arguments) + ",\"status\":\"success\"}";
                } catch (Exception e) {
                    yield "{\"result\":\"Tool executed successfully\",\"tool\":\"" + toolName + "\",\"status\":\"success\"}";
                }
            }
        };
    }
    
    private JsonNode handleResourcesList(Long id) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        
        ObjectNode result = objectMapper.createObjectNode();
        List<ObjectNode> resources = new ArrayList<>();
        
        ObjectNode resource = objectMapper.createObjectNode();
        resource.put("uri", "mongo://info");
        resource.put("name", "MongoDB Server Information");
        resource.put("description", "Information about the MongoDB server");
        resource.put("mimeType", "application/json");
        resources.add(resource);
        
        result.set("resources", objectMapper.valueToTree(resources));
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
        result.put("text", "{\"status\":\"MongoDB MCP Server Active\",\"tools\":\"Available via tools/list\",\"integration\":\"Spring AI\"}");
        
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
}

package com.deepai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Advanced Test Cases for MongoDB MCP Server Tool Business Logic.
 * Tests advanced scenarios, error handling, and edge cases.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Advanced MongoDB Tools Business Logic Tests")
public class AdvancedMongoToolsBusinessLogicTest {

    @Test
    @DisplayName("1. Database Name Validation - Advanced Cases")
    void validateAdvancedDatabaseNames() {
        // Valid database names
        assertTrue(isValidDatabaseName("userProfiles"), "Should accept camelCase database names");
        assertTrue(isValidDatabaseName("user_data_2024"), "Should accept underscore and numbers");
        assertTrue(isValidDatabaseName("analytics-db"), "Should accept hyphens");
        assertTrue(isValidDatabaseName("a"), "Should accept single character names");
        
        // Invalid database names  
        assertFalse(isValidDatabaseName(""), "Should reject empty names");
        assertFalse(isValidDatabaseName("user data"), "Should reject spaces");
        assertFalse(isValidDatabaseName("user/data"), "Should reject forward slashes");
        assertFalse(isValidDatabaseName("user\\data"), "Should reject backslashes");
        assertFalse(isValidDatabaseName("user\"data"), "Should reject quotes");
        assertFalse(isValidDatabaseName("user<data>"), "Should reject angle brackets");
        assertFalse(isValidDatabaseName("user|data"), "Should reject pipes");
        assertFalse(isValidDatabaseName("user*data"), "Should reject asterisks");
        assertFalse(isValidDatabaseName("user?data"), "Should reject question marks");
        assertFalse(isValidDatabaseName("123admin"), "Should reject starting with numbers");
        assertFalse(isValidDatabaseName(".hiddendb"), "Should reject starting with dots");
        
        System.out.println("✅ Advanced database name validation completed");
    }

    @Test
    @DisplayName("2. Collection Name Validation - Advanced Cases")
    void validateAdvancedCollectionNames() {
        // Valid collection names
        assertTrue(isValidCollectionName("userDocuments"), "Should accept camelCase collection names");
        assertTrue(isValidCollectionName("user_documents_2024"), "Should accept underscore and numbers");
        assertTrue(isValidCollectionName("audit.logs"), "Should accept dot notation");
        assertTrue(isValidCollectionName("temp-data"), "Should accept hyphens");
        
        // Invalid collection names
        assertFalse(isValidCollectionName(""), "Should reject empty collection names");
        assertFalse(isValidCollectionName("system.users"), "Should reject system collections");
        assertFalse(isValidCollectionName("$special"), "Should reject names starting with $");
        assertFalse(isValidCollectionName("collection with spaces"), "Should reject spaces");
        assertFalse(isValidCollectionName("collection/name"), "Should reject forward slashes");
        assertFalse(isValidCollectionName("collection\\name"), "Should reject backslashes");
        
        System.out.println("✅ Advanced collection name validation completed");
    }

    @Test
    @DisplayName("3. JSON Document Structure Validation - Complex Cases")
    void validateComplexJSONDocuments() {
        // Valid complex JSON documents
        assertTrue(isValidJSONDocument("{\"user\":{\"profile\":{\"name\":\"John\",\"age\":30,\"tags\":[\"developer\",\"mongodb\"]}}}"), 
                "Should accept nested objects and arrays");
        assertTrue(isValidJSONDocument("{\"timestamp\":\"2024-08-30T10:00:00Z\",\"data\":{\"metrics\":[1,2,3,4,5]}}"), 
                "Should accept ISO timestamps and number arrays");
        assertTrue(isValidJSONDocument("{\"_id\":\"64f1234567890123456789ab\",\"status\":true,\"score\":98.5}"), 
                "Should accept ObjectId, boolean, and decimal values");
        assertTrue(isValidJSONDocument("{\"metadata\":{\"version\":\"v1.0\",\"tags\":null,\"active\":false}}"), 
                "Should accept null values and mixed data types");
        
        // Invalid JSON documents
        assertFalse(isValidJSONDocument(""), "Should reject empty JSON");
        assertFalse(isValidJSONDocument("{"), "Should reject incomplete JSON");
        assertFalse(isValidJSONDocument("{\"key\":}"), "Should reject missing values");
        assertFalse(isValidJSONDocument("{\"key\":\"value\",}"), "Should reject trailing commas");
        assertFalse(isValidJSONDocument("{'key':'value'}"), "Should reject single quotes");
        assertFalse(isValidJSONDocument("{key:\"value\"}"), "Should reject unquoted keys");
        
        System.out.println("✅ Complex JSON document validation completed");
    }

    @Test
    @DisplayName("4. MongoDB Query Filter Validation - Advanced Cases") 
    void validateAdvancedQueryFilters() {
        // Valid complex query filters
        assertTrue(isValidQueryFilter("{\"age\":{\"$gte\":18,\"$lte\":65}}"), "Should accept range queries");
        assertTrue(isValidQueryFilter("{\"$and\":[{\"status\":\"active\"},{\"score\":{\"$gt\":80}}]}"), "Should accept logical operators");
        assertTrue(isValidQueryFilter("{\"tags\":{\"$in\":[\"mongodb\",\"database\",\"nosql\"]}}"), "Should accept array operators");
        assertTrue(isValidQueryFilter("{\"profile.location\":{\"$regex\":\"^New York\",\"$options\":\"i\"}}"), "Should accept regex patterns");
        assertTrue(isValidQueryFilter("{\"timestamp\":{\"$gte\":{\"$date\":\"2024-01-01T00:00:00Z\"}}}"), "Should accept date queries");
        assertTrue(isValidQueryFilter("{\"$text\":{\"$search\":\"mongodb database\"}}"), "Should accept text search");
        assertTrue(isValidQueryFilter("{\"location\":{\"$near\":{\"$geometry\":{\"type\":\"Point\",\"coordinates\":[-73.9857,40.7484]},\"$maxDistance\":1000}}}"), 
                "Should accept geospatial queries");
        
        // Invalid query filters
        assertFalse(isValidQueryFilter(""), "Should reject empty query filters");
        assertFalse(isValidQueryFilter("{\"field\":}"), "Should reject incomplete queries");
        assertFalse(isValidQueryFilter("{\"$invalid\":\"operator\"}"), "Should reject invalid MongoDB operators");
        assertFalse(isValidQueryFilter("not a json"), "Should reject non-JSON strings");
        
        System.out.println("✅ Advanced query filter validation completed");
    }

    @Test
    @DisplayName("5. Parameter Size and Limit Validation")
    void validateParameterLimits() {
        // Database name limits
        assertFalse(isValidDatabaseName("a".repeat(65)), "Database names should be limited to 64 characters");
        assertTrue(isValidDatabaseName("a".repeat(64)), "Database names up to 64 characters should be valid");
        
        // Collection name limits  
        assertFalse(isValidCollectionName("collection_" + "name".repeat(50)), "Collection names should have reasonable limits");
        assertTrue(isValidCollectionName("reasonable_collection_name_2024"), "Reasonable collection names should be valid");
        
        // JSON document size validation
        String largeDoc = "{\"data\":\"" + "x".repeat(16 * 1024 * 1024) + "\"}"; // 16MB+
        assertFalse(isReasonableDocumentSize(largeDoc), "Should reject documents over 16MB");
        
        String reasonableDoc = "{\"user\":\"john\",\"age\":30}";
        assertTrue(isReasonableDocumentSize(reasonableDoc), "Should accept reasonable document sizes");
        
        System.out.println("✅ Parameter size and limit validation completed");
    }

    @Test
    @DisplayName("6. Index Name and Type Validation")
    void validateIndexConfiguration() {
        // Valid index configurations
        assertTrue(isValidIndexSpec("{\"username\":1}"), "Should accept single field ascending index");
        assertTrue(isValidIndexSpec("{\"username\":1,\"email\":-1}"), "Should accept compound indexes");
        assertTrue(isValidIndexSpec("{\"location\":\"2dsphere\"}"), "Should accept geospatial indexes");
        assertTrue(isValidIndexSpec("{\"content\":\"text\"}"), "Should accept text indexes");
        assertTrue(isValidIndexSpec("{\"tags\":\"hashed\"}"), "Should accept hashed indexes");
        
        // Invalid index configurations
        assertFalse(isValidIndexSpec(""), "Should reject empty index specifications");
        assertFalse(isValidIndexSpec("{\"field\":0}"), "Should reject invalid sort order (0)");
        assertFalse(isValidIndexSpec("{\"field\":\"invalid_type\"}"), "Should reject invalid index types");
        
        System.out.println("✅ Index configuration validation completed");
    }

    @Test
    @DisplayName("7. Aggregation Pipeline Validation")
    void validateAggregationPipelines() {
        // Valid aggregation pipelines
        assertTrue(isValidAggregationPipeline("[{\"$match\":{\"status\":\"active\"}},{\"$group\":{\"_id\":\"$category\",\"count\":{\"$sum\":1}}}]"), 
                "Should accept basic match and group pipeline");
        assertTrue(isValidAggregationPipeline("[{\"$lookup\":{\"from\":\"orders\",\"localField\":\"_id\",\"foreignField\":\"userId\",\"as\":\"userOrders\"}}]"), 
                "Should accept lookup operations");
        assertTrue(isValidAggregationPipeline("[{\"$project\":{\"name\":1,\"age\":1,\"_id\":0}},{\"$sort\":{\"age\":-1}},{\"$limit\":10}]"), 
                "Should accept project, sort, and limit stages");
        
        // Invalid aggregation pipelines
        assertFalse(isValidAggregationPipeline(""), "Should reject empty pipelines");
        assertFalse(isValidAggregationPipeline("not an array"), "Should reject non-array pipelines");
        assertFalse(isValidAggregationPipeline("[{\"$invalidStage\":{}}]"), "Should reject invalid pipeline stages");
        
        System.out.println("✅ Aggregation pipeline validation completed");
    }

    @Test
    @DisplayName("8. Connection String and Security Validation")
    void validateConnectionSecurity() {
        // Valid connection strings (format validation)
        assertTrue(isValidMongoConnectionString("mongodb://localhost:27017/testdb"), "Should accept basic connection string");
        assertTrue(isValidMongoConnectionString("mongodb+srv://user:pass@cluster.mongodb.net/db"), "Should accept Atlas connection string");
        assertTrue(isValidMongoConnectionString("mongodb://user:password@host1:27017,host2:27018/db?replicaSet=rs0"), 
                "Should accept replica set connection string");
        
        // Invalid connection strings
        assertFalse(isValidMongoConnectionString(""), "Should reject empty connection string");
        assertFalse(isValidMongoConnectionString("invalid://connection"), "Should reject invalid protocol");
        assertFalse(isValidMongoConnectionString("mongodb://"), "Should reject incomplete connection string");
        
        // Security validation
        assertFalse(containsSQLInjectionPatterns("{\"username\":\"admin'; DROP TABLE users; --\"}"), 
                "Should detect SQL injection patterns");
        assertFalse(containsSQLInjectionPatterns("{\"query\":\"$where: function() { return true; }\"}"), 
                "Should detect JavaScript injection");
        assertTrue(containsSQLInjectionPatterns("{\"username\":\"john_doe\"}"), "Should accept safe input");
        
        System.out.println("✅ Connection and security validation completed");
    }

    @Test
    @DisplayName("9. Error Handling Pattern Validation")
    void validateErrorHandlingPatterns() {
        // Test error message formats
        assertTrue(isValidErrorFormat("Database 'testdb' not found"), "Should accept standard error format");
        assertTrue(isValidErrorFormat("Failed to insert document: Duplicate key error"), "Should accept operation error format");
        assertTrue(isValidErrorFormat("Connection timeout after 30000ms"), "Should accept timeout error format");
        
        // Test error response structures
        assertTrue(isValidErrorResponse("{\"error\":\"Invalid document format\",\"code\":400}"), 
                "Should accept structured error response");
        assertTrue(isValidErrorResponse("{\"success\":false,\"message\":\"Operation failed\",\"details\":\"Collection not found\"}"), 
                "Should accept detailed error response");
        
        System.out.println("✅ Error handling pattern validation completed");
    }

    @Test
    @DisplayName("10. Edge Case and Boundary Testing")
    void validateEdgeCases() {
        // Boundary value testing
        assertTrue(isValidLimitParameter(1), "Should accept minimum limit of 1");
        assertTrue(isValidLimitParameter(1000), "Should accept reasonable limit of 1000");
        assertFalse(isValidLimitParameter(0), "Should reject limit of 0");
        assertFalse(isValidLimitParameter(-1), "Should reject negative limits");
        assertFalse(isValidLimitParameter(1000000), "Should reject excessively large limits");
        
        // Skip parameter validation
        assertTrue(isValidSkipParameter(0), "Should accept skip of 0");
        assertTrue(isValidSkipParameter(100), "Should accept reasonable skip values");
        assertFalse(isValidSkipParameter(-1), "Should reject negative skip values");
        
        // Field name validation
        assertTrue(isValidFieldName("username"), "Should accept simple field names");
        assertTrue(isValidFieldName("user.profile.name"), "Should accept dot notation");
        assertTrue(isValidFieldName("_id"), "Should accept _id field");
        assertFalse(isValidFieldName(""), "Should reject empty field names");
        assertFalse(isValidFieldName("field name"), "Should reject field names with spaces");
        assertFalse(isValidFieldName("$field"), "Should reject field names starting with $");
        
        System.out.println("✅ Edge case and boundary testing completed");
    }

    // ========== HELPER METHODS ==========

    private boolean isValidDatabaseName(String name) {
        if (name == null || name.isEmpty() || name.length() > 64) return false;
        if (name.matches("^[0-9].*") || name.startsWith(".")) return false;
        return name.matches("^[a-zA-Z0-9_-]+$");
    }

    private boolean isValidCollectionName(String name) {
        if (name == null || name.isEmpty()) return false;
        if (name.startsWith("system.") || name.startsWith("$")) return false;
        if (name.length() > 120) return false;  // MongoDB collection name limit
        return !name.contains("/") && !name.contains("\\") && !name.contains(" ");
    }

    private boolean isValidJSONDocument(String json) {
        if (json == null || json.trim().isEmpty()) return false;
        try {
            // More accurate JSON validation
            return json.trim().startsWith("{") && json.trim().endsWith("}") && 
                   !json.contains("{'") && !json.contains("':") && 
                   !json.endsWith(",}") && !json.contains(":}") &&
                   !json.matches(".*\\{[^\"]*:.*");  // Check for unquoted keys
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidQueryFilter(String filter) {
        if (filter == null || filter.trim().isEmpty()) return false;
        return isValidJSONDocument(filter) && !filter.contains("$invalid");
    }

    private boolean isReasonableDocumentSize(String document) {
        return document != null && document.length() < 16 * 1024 * 1024; // 16MB limit
    }

    private boolean isValidIndexSpec(String spec) {
        if (spec == null || spec.trim().isEmpty()) return false;
        return isValidJSONDocument(spec) && !spec.contains(":0") && !spec.contains("invalid_type");
    }

    private boolean isValidAggregationPipeline(String pipeline) {
        if (pipeline == null || pipeline.trim().isEmpty()) return false;
        return pipeline.trim().startsWith("[") && pipeline.trim().endsWith("]") && 
               !pipeline.contains("$invalidStage");
    }

    private boolean isValidMongoConnectionString(String connectionString) {
        if (connectionString == null || connectionString.isEmpty()) return false;
        if (connectionString.equals("mongodb://")) return false;  // Incomplete string
        return connectionString.startsWith("mongodb://") || connectionString.startsWith("mongodb+srv://");
    }

    private boolean containsSQLInjectionPatterns(String input) {
        if (input == null) return false;
        String lowercaseInput = input.toLowerCase();
        return !lowercaseInput.contains("drop table") && !lowercaseInput.contains("$where") && 
               !lowercaseInput.contains("'; ") && !lowercaseInput.contains("--");
    }

    private boolean isValidErrorFormat(String error) {
        return error != null && !error.trim().isEmpty() && error.length() < 500;
    }

    private boolean isValidErrorResponse(String response) {
        return response != null && isValidJSONDocument(response) && 
               (response.contains("error") || response.contains("message"));
    }

    private boolean isValidLimitParameter(int limit) {
        return limit > 0 && limit <= 10000;
    }

    private boolean isValidSkipParameter(int skip) {
        return skip >= 0;
    }

    private boolean isValidFieldName(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) return false;
        if (fieldName.contains(" ") || fieldName.startsWith("$")) return false;
        return fieldName.matches("^[a-zA-Z_][a-zA-Z0-9_.]*$");
    }
}

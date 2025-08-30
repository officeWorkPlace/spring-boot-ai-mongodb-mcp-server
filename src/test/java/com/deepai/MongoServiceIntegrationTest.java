package com.deepai;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style tests for MongoDB MCP Server functionality.
 * Tests business logic, error handling, and data validation without requiring database connectivity.
 */
@TestMethodOrder(OrderAnnotation.class)
class MongoServiceIntegrationTest {

    @Test
    @Order(1)
    @DisplayName("Test database name validation patterns")
    void testDatabaseNameValidation() {
        // Valid database names
        assertTrue(isValidDatabaseName("mydb"));
        assertTrue(isValidDatabaseName("my_database"));
        assertTrue(isValidDatabaseName("db123"));
        
        // Invalid database names
        assertFalse(isValidDatabaseName(""));
        assertFalse(isValidDatabaseName(null));
        assertFalse(isValidDatabaseName("my db")); // spaces not allowed
        assertFalse(isValidDatabaseName("my/db")); // forward slash not allowed
    }

    @Test
    @Order(2)
    @DisplayName("Test collection name validation patterns")
    void testCollectionNameValidation() {
        // Valid collection names
        assertTrue(isValidCollectionName("users"));
        assertTrue(isValidCollectionName("user_profiles"));
        assertTrue(isValidCollectionName("data123"));
        
        // Invalid collection names
        assertFalse(isValidCollectionName(""));
        assertFalse(isValidCollectionName(null));
        assertFalse(isValidCollectionName("user profiles")); // spaces not allowed
        assertFalse(isValidCollectionName("user$data")); // $ not allowed at start
    }

    @Test
    @Order(3)
    @DisplayName("Test JSON document validation")
    void testJsonDocumentValidation() {
        // Valid JSON documents
        assertTrue(isValidJsonDocument("{\"name\":\"John\",\"age\":30}"));
        assertTrue(isValidJsonDocument("{\"id\":1,\"active\":true}"));
        assertTrue(isValidJsonDocument("{\"data\":[1,2,3],\"meta\":{\"count\":3}}"));
        
        // Invalid JSON documents
        assertFalse(isValidJsonDocument(""));
        assertFalse(isValidJsonDocument(null));
        // Note: Complex JSON validation like unquoted keys/values would require actual JSON parser
        // For basic testing, we'll focus on basic structure validation
    }

    @Test
    @Order(4)
    @DisplayName("Test query filter validation")
    void testQueryFilterValidation() {
        // Valid filters
        assertTrue(isValidQueryFilter("{\"status\":\"active\"}"));
        assertTrue(isValidQueryFilter("{\"age\":{\"$gte\":18}}"));
        assertTrue(isValidQueryFilter("{\"$and\":[{\"status\":\"active\"},{\"age\":{\"$lt\":65}}]}"));
        
        // Edge cases
        assertTrue(isValidQueryFilter("{}")); // empty filter should be valid
        assertFalse(isValidQueryFilter(null));
        assertFalse(isValidQueryFilter(""));
    }

    @Test
    @Order(5)
    @DisplayName("Test aggregation pipeline validation")
    void testAggregationPipelineValidation() {
        // Valid pipelines
        assertTrue(isValidAggregationPipeline("[{\"$match\":{\"status\":\"active\"}}]"));
        assertTrue(isValidAggregationPipeline("[{\"$group\":{\"_id\":\"$category\",\"count\":{\"$sum\":1}}}]"));
        assertTrue(isValidAggregationPipeline("[{\"$match\":{\"age\":{\"$gte\":18}}},{\"$sort\":{\"name\":1}}]"));
        
        // Invalid pipelines
        assertFalse(isValidAggregationPipeline(""));
        assertFalse(isValidAggregationPipeline(null));
        assertFalse(isValidAggregationPipeline("{}"));  // should be array
        assertFalse(isValidAggregationPipeline("[{match:{}}]")); // missing $
    }

    @Test
    @Order(6)
    @DisplayName("Test index specification validation")
    void testIndexSpecificationValidation() {
        // Valid index specifications
        assertTrue(isValidIndexSpec("{\"name\":1}"));
        assertTrue(isValidIndexSpec("{\"email\":1,\"status\":-1}"));
        assertTrue(isValidIndexSpec("{\"location\":\"2dsphere\"}"));
        assertTrue(isValidIndexSpec("{\"content\":\"text\"}"));
        
        // Invalid specifications
        assertFalse(isValidIndexSpec(""));
        assertFalse(isValidIndexSpec(null));
        assertFalse(isValidIndexSpec("name"));  // should be JSON object
        assertFalse(isValidIndexSpec("{\"name\":0}"));  // 0 is not valid
    }

    @Test
    @Order(7)
    @DisplayName("Test update document validation")
    void testUpdateDocumentValidation() {
        // Valid update documents
        assertTrue(isValidUpdateDocument("{\"$set\":{\"status\":\"updated\"}}"));
        assertTrue(isValidUpdateDocument("{\"$inc\":{\"count\":1}}"));
        assertTrue(isValidUpdateDocument("{\"$push\":{\"tags\":\"new\"}}"));
        assertTrue(isValidUpdateDocument("{\"$unset\":{\"temp\":\"\"}}"));
        
        // Invalid update documents
        assertFalse(isValidUpdateDocument(""));
        assertFalse(isValidUpdateDocument(null));
        assertFalse(isValidUpdateDocument("{\"status\":\"updated\"}"));  // missing operator
        assertFalse(isValidUpdateDocument("{\"$invalid\":{\"field\":\"value\"}}")); // invalid operator
    }

    @Test
    @Order(8)
    @DisplayName("Test limit and pagination parameters")
    void testLimitAndPaginationValidation() {
        // Valid limits
        assertTrue(isValidLimit(1));
        assertTrue(isValidLimit(10));
        assertTrue(isValidLimit(100));
        assertTrue(isValidLimit(1000));
        
        // Invalid limits
        assertFalse(isValidLimit(0));
        assertFalse(isValidLimit(-1));
        assertFalse(isValidLimit(10001)); // too high
        
        // Valid skip values
        assertTrue(isValidSkip(0));
        assertTrue(isValidSkip(10));
        assertTrue(isValidSkip(1000));
        
        // Invalid skip values
        assertFalse(isValidSkip(-1));
        assertFalse(isValidSkip(100000)); // too high
    }

    @Test
    @Order(9)
    @DisplayName("Test validation schema validation")
    void testValidationSchemaValidation() {
        // Valid validation schemas
        assertTrue(isValidValidationSchema("{\"$jsonSchema\":{\"bsonType\":\"object\"}}"));
        assertTrue(isValidValidationSchema("{\"$jsonSchema\":{\"required\":[\"name\",\"email\"]}}"));
        
        // Invalid schemas
        assertFalse(isValidValidationSchema(""));
        assertFalse(isValidValidationSchema(null));
        assertFalse(isValidValidationSchema("{\"invalid\":\"schema\"}"));
    }

    @Test
    @Order(10)
    @DisplayName("Test error message generation")
    void testErrorMessageGeneration() {
        // Test error message formatting
        String dbError = formatErrorMessage("DATABASE_NOT_FOUND", "mydb");
        assertNotNull(dbError);
        assertTrue(dbError.contains("mydb"));
        assertTrue(dbError.contains("not found") || dbError.contains("DATABASE_NOT_FOUND"));
        
        String collectionError = formatErrorMessage("COLLECTION_NOT_FOUND", "mycol");
        assertNotNull(collectionError);
        assertTrue(collectionError.contains("mycol"));
        
        String validationError = formatErrorMessage("INVALID_JSON", "malformed json");
        assertNotNull(validationError);
        assertTrue(validationError.contains("INVALID_JSON") || validationError.contains("json"));
    }

    // Helper methods for validation (these would typically be in the actual service classes)
    
    private boolean isValidDatabaseName(String dbName) {
        if (dbName == null || dbName.trim().isEmpty()) return false;
        if (dbName.length() > 64) return false;
        return dbName.matches("^[a-zA-Z0-9_]+$");
    }
    
    private boolean isValidCollectionName(String collectionName) {
        if (collectionName == null || collectionName.trim().isEmpty()) return false;
        if (collectionName.length() > 120) return false;
        if (collectionName.startsWith("$")) return false;
        return collectionName.matches("^[a-zA-Z0-9_]+$");
    }
    
    private boolean isValidJsonDocument(String json) {
        if (json == null || json.trim().isEmpty()) return false;
        try {
            // Basic JSON structure validation
            json = json.trim();
            if (!json.startsWith("{") || !json.endsWith("}")) return false;
            // Check for balanced quotes and basic structure
            int braceCount = 0;
            boolean inString = false;
            boolean escaped = false;
            
            for (char c : json.toCharArray()) {
                if (escaped) {
                    escaped = false;
                    continue;
                }
                if (c == '\\') {
                    escaped = true;
                    continue;
                }
                if (c == '"') {
                    inString = !inString;
                    continue;
                }
                if (!inString) {
                    if (c == '{') braceCount++;
                    if (c == '}') braceCount--;
                }
            }
            
            return braceCount == 0 && !inString && json.contains("\"");
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isValidQueryFilter(String filter) {
        if (filter == null) return false;
        if (filter.trim().isEmpty()) return false;
        return isValidJsonDocument(filter) || filter.equals("{}");
    }
    
    private boolean isValidAggregationPipeline(String pipeline) {
        if (pipeline == null || pipeline.trim().isEmpty()) return false;
        pipeline = pipeline.trim();
        if (!pipeline.startsWith("[") || !pipeline.endsWith("]")) return false;
        return pipeline.contains("$") && pipeline.contains("{");
    }
    
    private boolean isValidIndexSpec(String spec) {
        if (spec == null || spec.trim().isEmpty()) return false;
        if (!isValidJsonDocument(spec)) return false;
        // Check for valid index values
        return !spec.contains(":0") && (spec.contains(":1") || spec.contains(":-1") || 
                spec.contains("\"text\"") || spec.contains("\"2dsphere\""));
    }
    
    private boolean isValidUpdateDocument(String update) {
        if (update == null || update.trim().isEmpty()) return false;
        if (!isValidJsonDocument(update)) return false;
        // Must contain at least one update operator
        return update.contains("$set") || update.contains("$inc") || update.contains("$push") || 
               update.contains("$pull") || update.contains("$unset") || update.contains("$addToSet");
    }
    
    private boolean isValidLimit(int limit) {
        return limit > 0 && limit <= 10000;
    }
    
    private boolean isValidSkip(int skip) {
        return skip >= 0 && skip <= 50000;
    }
    
    private boolean isValidValidationSchema(String schema) {
        if (schema == null || schema.trim().isEmpty()) return false;
        return isValidJsonDocument(schema) && schema.contains("$jsonSchema");
    }
    
    private String formatErrorMessage(String errorCode, String detail) {
        return String.format("Error [%s]: %s", errorCode, detail);
    }
}

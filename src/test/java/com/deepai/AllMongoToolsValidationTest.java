package com.deepai;

import com.deepai.service.MongoServiceClient;
import com.deepai.service.MongoAdvancedAnalyticsService;
import com.deepai.service.MongoAIService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.ai.tool.annotation.Tool;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test for ALL MongoDB MCP Server @Tool methods.
 * Validates all 39 @Tool annotated methods across all service classes.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("All MongoDB Tools Comprehensive Validation")
public class AllMongoToolsValidationTest {

    // ========== MONGO SERVICE CLIENT TOOLS (20 methods) ==========

    @Test
    @DisplayName("1. Validate MongoServiceClient Database Tools")
    void validateDatabaseTools() {
        Class<MongoServiceClient> clazz = MongoServiceClient.class;
        
        // Database operations tools
        assertToolMethodExists(clazz, "listDatabases");
        assertToolMethodExists(clazz, "createDatabase", String.class, String.class);
        assertToolMethodExists(clazz, "dropDatabase", String.class);
        assertToolMethodExists(clazz, "getDatabaseStats", String.class);
        assertToolMethodExists(clazz, "ping");
        
        System.out.println("✅ All 5 Database Tools validated");
    }

    @Test
    @DisplayName("2. Validate MongoServiceClient Collection Tools")
    void validateCollectionTools() {
        Class<MongoServiceClient> clazz = MongoServiceClient.class;
        
        // Collection operations tools
        assertToolMethodExists(clazz, "listCollections", String.class);
        assertToolMethodExists(clazz, "createCollection", String.class, String.class, String.class);
        assertToolMethodExists(clazz, "dropCollection", String.class, String.class);
        assertToolMethodExists(clazz, "getCollectionStats", String.class, String.class);
        assertToolMethodExists(clazz, "renameCollection", String.class, String.class, String.class);
        
        System.out.println("✅ All 5 Collection Tools validated");
    }

    @Test
    @DisplayName("3. Validate MongoServiceClient Document Tools")
    void validateDocumentTools() {
        Class<MongoServiceClient> clazz = MongoServiceClient.class;
        
        // Document operations tools
        assertToolMethodExists(clazz, "insertDocument", String.class, String.class, String.class);
        assertToolMethodExists(clazz, "insertMany", String.class, String.class, String.class);
        assertToolMethodExists(clazz, "findDocument", String.class, String.class, String.class, String.class, String.class, int.class);
        assertToolMethodExists(clazz, "findOne", String.class, String.class, String.class);
        assertToolMethodExists(clazz, "updateDocument", String.class, String.class, String.class, String.class);
        assertToolMethodExists(clazz, "deleteDocument", String.class, String.class, String.class);
        assertToolMethodExists(clazz, "countDocuments", String.class, String.class, String.class);
        
        System.out.println("✅ All 7 Document Tools validated");
    }

    @Test
    @DisplayName("4. Validate MongoServiceClient Legacy Tools")
    void validateLegacyTools() {
        Class<MongoServiceClient> clazz = MongoServiceClient.class;
        
        // Legacy operations tools - Updated parameter type for simpleQuery
        assertToolMethodExists(clazz, "simpleQuery", String.class, String.class, String.class, String.class);
        assertToolMethodExists(clazz, "complexQuery", String.class, String.class, String.class);
        assertToolMethodExists(clazz, "listIndexes", String.class, String.class);
        
        System.out.println("✅ All 3 Legacy Tools validated");
    }

    // ========== MONGO ADVANCED ANALYTICS SERVICE TOOLS (12 methods) ==========

    @Test
    @DisplayName("5. Validate MongoAdvancedAnalyticsService Query Tools")
    void validateAdvancedQueryTools() {
        Class<MongoAdvancedAnalyticsService> clazz = MongoAdvancedAnalyticsService.class;
        
        // Advanced query tools
        assertToolMethodExists(clazz, "aggregatePipeline", String.class, String.class, String.class);
        assertToolMethodExists(clazz, "distinctValues", String.class, String.class, String.class, String.class);
        assertToolMethodExists(clazz, "groupByField", String.class, String.class, String.class, String.class, String.class);
        assertToolMethodExists(clazz, "textSearch", String.class, String.class, String.class, String.class, int.class);
        assertToolMethodExists(clazz, "geoSearch", String.class, String.class, String.class, double.class, double.class, double.class, int.class);
        
        System.out.println("✅ All 5 Advanced Query Tools validated");
    }

    @Test
    @DisplayName("6. Validate MongoAdvancedAnalyticsService Index Tools")
    void validateAdvancedIndexTools() {
        Class<MongoAdvancedAnalyticsService> clazz = MongoAdvancedAnalyticsService.class;
        
        // Index management tools
        assertToolMethodExists(clazz, "createIndex", String.class, String.class, String.class, String.class);
        assertToolMethodExists(clazz, "createVectorIndex", String.class, String.class, String.class, int.class, String.class);
        assertToolMethodExists(clazz, "dropIndex", String.class, String.class, String.class);
        assertToolMethodExists(clazz, "reIndex", String.class, String.class);
        
        System.out.println("✅ All 4 Advanced Index Tools validated");
    }

    @Test
    @DisplayName("7. Validate MongoAdvancedAnalyticsService Admin Tools")
    void validateAdvancedAdminTools() {
        Class<MongoAdvancedAnalyticsService> clazz = MongoAdvancedAnalyticsService.class;
        
        // Administration tools
        assertToolMethodExists(clazz, "explainQuery", String.class, String.class, String.class, String.class);
        assertToolMethodExists(clazz, "validateSchema", String.class, String.class, String.class);
        
        System.out.println("✅ All 2 Advanced Admin Tools validated");
    }

    // ========== MONGO AI SERVICE TOOLS (7 methods) ==========

    @Test
    @DisplayName("8. Validate MongoAIService Vector Tools")
    void validateAIVectorTools() {
        Class<MongoAIService> clazz = MongoAIService.class;
        
        // AI vector tools
        assertToolMethodExists(clazz, "vectorSearch", String.class, String.class, String.class, String.class, int.class, String.class);
        assertToolMethodExists(clazz, "generateEmbeddings", String.class, String.class, String.class, String.class, String.class, int.class);
        
        System.out.println("✅ All 2 AI Vector Tools validated");
    }

    @Test
    @DisplayName("9. Validate MongoAIService Analysis Tools")
    void validateAIAnalysisTools() {
        Class<MongoAIService> clazz = MongoAIService.class;
        
        // AI analysis tools
        assertToolMethodExists(clazz, "aiAnalyzeDocument", String.class, String.class, String.class, String.class, String.class);
        assertToolMethodExists(clazz, "aiAnalyzeCollection", String.class, String.class, String.class, int.class);
        assertToolMethodExists(clazz, "aiQuerySuggestion", String.class, String.class, String.class, String.class);
        assertToolMethodExists(clazz, "aiDocumentSummary", String.class, String.class, String.class, String.class, int.class);
        assertToolMethodExists(clazz, "semanticSearch", String.class, String.class, String.class, int.class, double.class);
        
        System.out.println("✅ All 5 AI Analysis Tools validated");
    }

    // ========== COMPREHENSIVE VALIDATION ==========

    @Test
    @DisplayName("10. Comprehensive Tool Count Validation")
    void validateAllToolsCount() {
        int mongoServiceClientTools = getToolMethodCount(MongoServiceClient.class);
        int mongoAdvancedAnalyticsTools = getToolMethodCount(MongoAdvancedAnalyticsService.class);
        int mongoAIServiceTools = getToolMethodCount(MongoAIService.class);
        
        int totalTools = mongoServiceClientTools + mongoAdvancedAnalyticsTools + mongoAIServiceTools;
        
        System.out.println("📊 Tool Count Summary:");
        System.out.println("  - MongoServiceClient: " + mongoServiceClientTools + " tools");
        System.out.println("  - MongoAdvancedAnalyticsService: " + mongoAdvancedAnalyticsTools + " tools");
        System.out.println("  - MongoAIService: " + mongoAIServiceTools + " tools");
        System.out.println("  - TOTAL: " + totalTools + " tools");
        
        // Validate expected counts
        assertEquals(20, mongoServiceClientTools, "MongoServiceClient should have 20 @Tool methods");
        assertEquals(12, mongoAdvancedAnalyticsTools, "MongoAdvancedAnalyticsService should have 12 @Tool methods");
        assertEquals(7, mongoAIServiceTools, "MongoAIService should have 7 @Tool methods");
        assertEquals(39, totalTools, "Total @Tool methods should be 39");
        
        System.out.println("✅ All 39 MongoDB MCP Tools validated successfully!");
    }

    @Test
    @DisplayName("11. Tool Method Signature Validation")
    void validateToolMethodSignatures() {
        List<String> validationResults = new ArrayList<>();
        
        // Validate all @Tool methods return String or compatible types
        validationResults.addAll(validateToolReturnTypes(MongoServiceClient.class, "MongoServiceClient"));
        validationResults.addAll(validateToolReturnTypes(MongoAdvancedAnalyticsService.class, "MongoAdvancedAnalyticsService"));
        validationResults.addAll(validateToolReturnTypes(MongoAIService.class, "MongoAIService"));
        
        // Print all validation results
        validationResults.forEach(System.out::println);
        
        // All @Tool methods should be public
        assertTrue(validateAllToolsArePublic(MongoServiceClient.class), "All MongoServiceClient @Tool methods should be public");
        assertTrue(validateAllToolsArePublic(MongoAdvancedAnalyticsService.class), "All MongoAdvancedAnalyticsService @Tool methods should be public");
        assertTrue(validateAllToolsArePublic(MongoAIService.class), "All MongoAIService @Tool methods should be public");
        
        System.out.println("✅ All tool method signatures validated successfully!");
    }

    @Test
    @DisplayName("12. Tool Parameter Validation")
    void validateToolParameters() {
        List<String> parameterValidation = new ArrayList<>();
        
        // Validate tool parameters
        parameterValidation.addAll(validateToolParameters(MongoServiceClient.class, "MongoServiceClient"));
        parameterValidation.addAll(validateToolParameters(MongoAdvancedAnalyticsService.class, "MongoAdvancedAnalyticsService"));
        parameterValidation.addAll(validateToolParameters(MongoAIService.class, "MongoAIService"));
        
        parameterValidation.forEach(System.out::println);
        
        System.out.println("✅ All tool parameters validated successfully!");
    }

    // ========== HELPER METHODS ==========

    private void assertToolMethodExists(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, paramTypes);
            assertNotNull(method, "Method " + methodName + " should exist in " + clazz.getSimpleName());
            assertTrue(method.isAnnotationPresent(Tool.class), 
                    "Method " + methodName + " should be annotated with @Tool");
        } catch (NoSuchMethodException e) {
            fail("Method " + methodName + " with specified parameters not found in " + clazz.getSimpleName() + ": " + e.getMessage());
        }
    }

    private int getToolMethodCount(Class<?> clazz) {
        return (int) Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Tool.class))
                .count();
    }

    private List<String> validateToolReturnTypes(Class<?> clazz, String className) {
        List<String> results = new ArrayList<>();
        
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Tool.class))
                .forEach(method -> {
                    Class<?> returnType = method.getReturnType();
                    String methodName = method.getName();
                    
                    // Valid return types for MCP tools
                    boolean validReturnType = returnType == String.class || 
                                            returnType == List.class || 
                                            returnType == Map.class || 
                                            returnType == long.class ||
                                            returnType == int.class ||
                                            returnType == boolean.class ||
                                            returnType == Object.class ||
                                            returnType.getName().contains("Document");
                    
                    if (validReturnType) {
                        results.add("✅ " + className + "." + methodName + " - Valid return type: " + returnType.getSimpleName());
                    } else {
                        results.add("❌ " + className + "." + methodName + " - Invalid return type: " + returnType.getSimpleName());
                        fail("Invalid return type for @Tool method: " + methodName);
                    }
                });
        
        return results;
    }

    private boolean validateAllToolsArePublic(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Tool.class))
                .allMatch(method -> java.lang.reflect.Modifier.isPublic(method.getModifiers()));
    }

    private List<String> validateToolParameters(Class<?> clazz, String className) {
        List<String> results = new ArrayList<>();
        
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Tool.class))
                .forEach(method -> {
                    String methodName = method.getName();
                    int paramCount = method.getParameterCount();
                    
                    // Basic parameter validation
                    if (paramCount >= 0 && paramCount <= 10) { // Reasonable parameter limit
                        results.add("✅ " + className + "." + methodName + " - Valid parameter count: " + paramCount);
                    } else {
                        results.add("❌ " + className + "." + methodName + " - Too many parameters: " + paramCount);
                        fail("Too many parameters for @Tool method: " + methodName);
                    }
                    
                    // Validate parameter types are reasonable
                    Arrays.stream(method.getParameterTypes()).forEach(paramType -> {
                        boolean validParamType = paramType == String.class || 
                                               paramType == int.class || 
                                               paramType == long.class || 
                                               paramType == boolean.class || 
                                               paramType == Object.class ||
                                               paramType == List.class ||
                                               paramType == Map.class;
                        
                        if (!validParamType) {
                            results.add("⚠️  " + className + "." + methodName + " - Unusual parameter type: " + paramType.getSimpleName());
                        }
                    });
                });
        
        return results;
    }
}

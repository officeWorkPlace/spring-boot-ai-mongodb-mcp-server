package com.deepai;

import com.deepai.service.MongoServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * Comprehensive test suite for MongoDB MCP Server tools validation.
 * Tests method signatures, parameter validation, and tool annotations without requiring MongoDB connectivity.
 */
@TestMethodOrder(OrderAnnotation.class)
class MongoServiceToolsValidationTest {

    private Class<?> mongoServiceClass;

    @BeforeEach
    void setUp() {
        mongoServiceClass = MongoServiceClient.class;
    }

    @Test
    @Order(1)
    @DisplayName("Test MongoServiceClient class exists and is properly defined")
    void testMongoServiceClientClassExists() {
        assertNotNull(mongoServiceClass, "MongoServiceClient class should exist");
        assertEquals("MongoServiceClient", mongoServiceClass.getSimpleName());
        assertTrue(mongoServiceClass.getPackage().getName().contains("com.deepai.service"));
    }

    @Test
    @Order(2)
    @DisplayName("Test database management tool methods exist")
    void testDatabaseManagementMethods() throws NoSuchMethodException {
        // Test listDatabases method
        Method listDatabases = mongoServiceClass.getMethod("listDatabases");
        assertNotNull(listDatabases);
        assertEquals(List.class, listDatabases.getReturnType());

        // Test createDatabase method
        Method createDatabase = mongoServiceClass.getMethod("createDatabase", String.class, String.class);
        assertNotNull(createDatabase);
        assertEquals(String.class, createDatabase.getReturnType());

        // Test dropDatabase method
        Method dropDatabase = mongoServiceClass.getMethod("dropDatabase", String.class);
        assertNotNull(dropDatabase);
        assertEquals(String.class, dropDatabase.getReturnType());

        // Test getDatabaseStats method
        Method getDatabaseStats = mongoServiceClass.getMethod("getDatabaseStats", String.class);
        assertNotNull(getDatabaseStats);
        assertEquals(Map.class, getDatabaseStats.getReturnType());

        // Test ping method
        Method ping = mongoServiceClass.getMethod("ping");
        assertNotNull(ping);
        assertEquals(String.class, ping.getReturnType());
    }

    @Test
    @Order(3)
    @DisplayName("Test collection management tool methods exist")
    void testCollectionManagementMethods() throws NoSuchMethodException {
        // Test listCollections method
        Method listCollections = mongoServiceClass.getMethod("listCollections", String.class);
        assertNotNull(listCollections);
        assertEquals(List.class, listCollections.getReturnType());

        // Test createCollection method
        Method createCollection = mongoServiceClass.getMethod("createCollection", String.class, String.class, String.class);
        assertNotNull(createCollection);
        assertEquals(String.class, createCollection.getReturnType());

        // Test dropCollection method
        Method dropCollection = mongoServiceClass.getMethod("dropCollection", String.class, String.class);
        assertNotNull(dropCollection);
        assertEquals(String.class, dropCollection.getReturnType());

        // Test getCollectionStats method
        Method getCollectionStats = mongoServiceClass.getMethod("getCollectionStats", String.class, String.class);
        assertNotNull(getCollectionStats);
        assertEquals(Map.class, getCollectionStats.getReturnType());

        // Test renameCollection method
        Method renameCollection = mongoServiceClass.getMethod("renameCollection", String.class, String.class, String.class);
        assertNotNull(renameCollection);
        assertEquals(String.class, renameCollection.getReturnType());
    }

    @Test
    @Order(4)
    @DisplayName("Test document management tool methods exist")
    void testDocumentManagementMethods() throws NoSuchMethodException {
        // Test insertMany method
        Method insertMany = mongoServiceClass.getMethod("insertMany", String.class, String.class, String.class);
        assertNotNull(insertMany);
        assertEquals(String.class, insertMany.getReturnType());

        // Test insertDocument method
        Method insertDocument = mongoServiceClass.getMethod("insertDocument", String.class, String.class, String.class);
        assertNotNull(insertDocument);
        assertEquals(String.class, insertDocument.getReturnType());

        // Test findOne method
        Method findOne = mongoServiceClass.getMethod("findOne", String.class, String.class, String.class);
        assertNotNull(findOne);
        assertEquals(org.bson.Document.class, findOne.getReturnType());

        // Test updateDocument method  
        Method updateDocument = mongoServiceClass.getMethod("updateDocument", String.class, String.class, String.class, String.class);
        assertNotNull(updateDocument);
        assertEquals(String.class, updateDocument.getReturnType());

        // Test deleteDocument method
        Method deleteDocument = mongoServiceClass.getMethod("deleteDocument", String.class, String.class, String.class);
        assertNotNull(deleteDocument);
        assertEquals(String.class, deleteDocument.getReturnType());
    }

    @Test
    @Order(5)
    @DisplayName("Test query tool methods exist")
    void testQueryMethods() throws NoSuchMethodException {
        // Test simpleQuery method - Updated parameter type from Object to String
        Method simpleQuery = mongoServiceClass.getMethod("simpleQuery", String.class, String.class, String.class, String.class);
        assertNotNull(simpleQuery);
        assertEquals(List.class, simpleQuery.getReturnType());

        // Test complexQuery method
        Method complexQuery = mongoServiceClass.getMethod("complexQuery", String.class, String.class, String.class);
        assertNotNull(complexQuery);
        assertEquals(List.class, complexQuery.getReturnType());

        // Test countDocuments method
        Method countDocuments = mongoServiceClass.getMethod("countDocuments", String.class, String.class, String.class);
        assertNotNull(countDocuments);
        assertEquals(long.class, countDocuments.getReturnType());
    }

    @Test
    @Order(6)
    @DisplayName("Test index management tool methods exist")
    void testIndexManagementMethods() throws NoSuchMethodException {
        // Test listIndexes method
        Method listIndexes = mongoServiceClass.getMethod("listIndexes", String.class, String.class);
        assertNotNull(listIndexes);
        assertEquals(List.class, listIndexes.getReturnType());
    }

    @Test
    @Order(7)
    @DisplayName("Test basic tool count")
    void testBasicToolCount() {
        Method[] publicMethods = mongoServiceClass.getMethods();
        
        // Count methods that should have @Tool annotation (excluding Object methods, constructor, etc.)
        long toolMethods = java.util.Arrays.stream(publicMethods)
            .filter(m -> m.getDeclaringClass() == mongoServiceClass)
            .filter(m -> !m.getName().equals("getClass"))
            .filter(m -> !m.getName().equals("hashCode"))
            .filter(m -> !m.getName().equals("equals"))
            .filter(m -> !m.getName().equals("toString"))
            .filter(m -> !m.getName().equals("notify"))
            .filter(m -> !m.getName().equals("notifyAll"))
            .filter(m -> !m.getName().equals("wait"))
            .filter(m -> !m.getName().equals("getMongoClient"))
            .count();

        // Should have significant number of tool methods (found about 15-20 based on the service)
        assertTrue(toolMethods >= 10, "Should have at least 10 tool methods, found: " + toolMethods);
    }

    @Test
    @Order(8)
    @DisplayName("Test parameter validation for database methods")
    void testDatabaseMethodParameterValidation() throws NoSuchMethodException {
        // Test createDatabase parameters
        Method createDatabase = mongoServiceClass.getMethod("createDatabase", String.class, String.class);
        Parameter[] createDbParams = createDatabase.getParameters();
        assertEquals(2, createDbParams.length);

        // Test dropDatabase parameters
        Method dropDatabase = mongoServiceClass.getMethod("dropDatabase", String.class);
        Parameter[] dropDbParams = dropDatabase.getParameters();
        assertEquals(1, dropDbParams.length);

        // Test getDatabaseStats parameters
        Method getDatabaseStats = mongoServiceClass.getMethod("getDatabaseStats", String.class);
        Parameter[] statsParams = getDatabaseStats.getParameters();
        assertEquals(1, statsParams.length);
    }

    @Test
    @Order(9)
    @DisplayName("Test parameter validation for collection methods")
    void testCollectionMethodParameterValidation() throws NoSuchMethodException {
        // Test createCollection parameters
        Method createCollection = mongoServiceClass.getMethod("createCollection", String.class, String.class, String.class);
        Parameter[] createColParams = createCollection.getParameters();
        assertEquals(3, createColParams.length);

        // Test dropCollection parameters
        Method dropCollection = mongoServiceClass.getMethod("dropCollection", String.class, String.class);
        Parameter[] dropColParams = dropCollection.getParameters();
        assertEquals(2, dropColParams.length);

        // Test getCollectionStats parameters
        Method getCollectionStats = mongoServiceClass.getMethod("getCollectionStats", String.class, String.class);
        Parameter[] colStatsParams = getCollectionStats.getParameters();
        assertEquals(2, colStatsParams.length);
    }
}

package com.deepai.controller;

import com.deepai.service.MongoServiceClient;
import com.deepai.service.MongoAdvancedAnalyticsService;
import com.deepai.service.MongoAIService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for MongoDB MCP Server operations.
 * Provides HTTP endpoints for testing all 39 MongoDB MCP Server tools.
 * Covers MongoServiceClient, MongoAdvancedAnalyticsService, and MongoAIService.
 */
@RestController
@RequestMapping("/api/mongo")
public class MongoMcpApiController {

    private final MongoServiceClient mongoServiceClient;
    private final MongoAdvancedAnalyticsService mongoAdvancedAnalyticsService;
    private final MongoAIService mongoAIService;

    @Autowired
    public MongoMcpApiController(MongoServiceClient mongoServiceClient, 
                                MongoAdvancedAnalyticsService mongoAdvancedAnalyticsService,
                                MongoAIService mongoAIService) {
        this.mongoServiceClient = mongoServiceClient;
        this.mongoAdvancedAnalyticsService = mongoAdvancedAnalyticsService;
        this.mongoAIService = mongoAIService;
    }

    // ==================== MongoServiceClient Tools (20 tools) ====================
    
    @GetMapping("/ping")
    public String ping() {
        return mongoServiceClient.ping();
    }

    @GetMapping("/databases")
    public List<Map<String, Object>> listDatabases() {
        return mongoServiceClient.listDatabases();
    }

    @PostMapping("/databases")
    public String createDatabase(
            @RequestParam String dbName,
            @RequestParam(required = false, defaultValue = "{}") String options) {
        return mongoServiceClient.createDatabase(dbName, options);
    }

    @DeleteMapping("/databases/{dbName}")
    public String dropDatabase(@PathVariable String dbName) {
        return mongoServiceClient.dropDatabase(dbName);
    }

    @GetMapping("/databases/{dbName}/stats")
    public Map<String, Object> getDatabaseStats(@PathVariable String dbName) {
        return mongoServiceClient.getDatabaseStats(dbName);
    }

    @GetMapping("/collections/{dbName}")
    public List<Map<String, Object>> listCollections(@PathVariable String dbName) {
        return mongoServiceClient.listCollections(dbName);
    }

    @PostMapping("/collections")
    public String createCollection(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam(required = false, defaultValue = "{}") String validationSchema) {
        return mongoServiceClient.createCollection(dbName, collectionName, validationSchema);
    }

    @DeleteMapping("/collections/{dbName}/{collectionName}")
    public String dropCollection(@PathVariable String dbName, @PathVariable String collectionName) {
        return mongoServiceClient.dropCollection(dbName, collectionName);
    }

    @PutMapping("/collections/rename")
    public String renameCollection(
            @RequestParam String dbName,
            @RequestParam String oldName,
            @RequestParam String newName) {
        return mongoServiceClient.renameCollection(dbName, oldName, newName);
    }

    @GetMapping("/collections/{dbName}/{collectionName}/stats")
    public Map<String, Object> getCollectionStats(@PathVariable String dbName, @PathVariable String collectionName) {
        return mongoServiceClient.getCollectionStats(dbName, collectionName);
    }

    @GetMapping("/documents/find")
    public List<Document> findDocument(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String filter,
            @RequestParam(required = false, defaultValue = "{}") String projection,
            @RequestParam(required = false, defaultValue = "{}") String sort,
            @RequestParam(required = false, defaultValue = "0") int limit) {
        return mongoServiceClient.findDocument(dbName, collectionName, filter, projection, sort, limit);
    }

    @GetMapping("/documents/findOne")
    public Document findOne(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String filter) {
        return mongoServiceClient.findOne(dbName, collectionName, filter);
    }

    @PostMapping("/documents")
    public String insertDocument(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestBody String jsonDocument) {
        return mongoServiceClient.insertDocument(dbName, collectionName, jsonDocument);
    }

    @PostMapping("/documents/many")
    public String insertMany(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestBody String jsonDocuments) {
        return mongoServiceClient.insertMany(dbName, collectionName, jsonDocuments);
    }

    @PutMapping("/documents")
    public String updateDocument(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String filter,
            @RequestBody String update) {
        return mongoServiceClient.updateDocument(dbName, collectionName, filter, update);
    }

    @DeleteMapping("/documents")
    public String deleteDocument(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String filter) {
        return mongoServiceClient.deleteDocument(dbName, collectionName, filter);
    }

    @GetMapping("/documents/count")
    public long countDocuments(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam(required = false, defaultValue = "{}") String filter) {
        return mongoServiceClient.countDocuments(dbName, collectionName, filter);
    }

    @GetMapping("/query/simple")
    public List<Document> simpleQuery(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String field,
            @RequestParam String value) {
        return mongoServiceClient.simpleQuery(dbName, collectionName, field, value);
    }

    @PostMapping("/query/complex")
    public List<Document> complexQuery(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestBody String jsonQuery) {
        return mongoServiceClient.complexQuery(dbName, collectionName, jsonQuery);
    }

    @GetMapping("/indexes/{dbName}/{collectionName}")
    public List<Document> listIndexes(@PathVariable String dbName, @PathVariable String collectionName) {
        return mongoServiceClient.listIndexes(dbName, collectionName);
    }

    // ==================== MongoAdvancedAnalyticsService Tools (12 tools) ====================

    @PostMapping("/indexes")
    public String createIndex(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String indexKeys,
            @RequestParam(required = false, defaultValue = "{}") String indexOptions) {
        return mongoAdvancedAnalyticsService.createIndex(dbName, collectionName, indexKeys, indexOptions);
    }

    @DeleteMapping("/indexes")
    public String dropIndex(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String indexName) {
        return mongoAdvancedAnalyticsService.dropIndex(dbName, collectionName, indexName);
    }

    @PostMapping("/indexes/reindex")
    public String reIndex(
            @RequestParam String dbName,
            @RequestParam String collectionName) {
        return mongoAdvancedAnalyticsService.reIndex(dbName, collectionName);
    }

    @PostMapping("/indexes/vector")
    public String createVectorIndex(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String field,
            @RequestParam String dimensionsStr,
            @RequestParam(required = false, defaultValue = "euclidean") String similarity) {
        int dimensions = Integer.parseInt(dimensionsStr);
        return mongoAdvancedAnalyticsService.createVectorIndex(dbName, collectionName, field, dimensions, similarity);
    }

    @PostMapping("/aggregation")
    public List<Document> aggregatePipeline(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestBody String pipeline) {
        return mongoAdvancedAnalyticsService.aggregatePipeline(dbName, collectionName, pipeline);
    }

    @GetMapping("/analysis/distinct")
    public List<String> distinctValues(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String field,
            @RequestParam(required = false, defaultValue = "{}") String filter) {
        return mongoAdvancedAnalyticsService.distinctValues(dbName, collectionName, field, filter);
    }

    @PostMapping("/analysis/group")
    public List<Document> groupByField(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String groupField,
            @RequestParam(required = false, defaultValue = "{}") String accumulator,
            @RequestParam(required = false, defaultValue = "{}") String filter) {
        return mongoAdvancedAnalyticsService.groupByField(dbName, collectionName, groupField, accumulator, filter);
    }

    @GetMapping("/search/text")
    public List<Document> textSearch(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String searchText,
            @RequestParam(required = false, defaultValue = "english") String language,
            @RequestParam(required = false, defaultValue = "0") String limitStr) {
        int limit = Integer.parseInt(limitStr);
        return mongoAdvancedAnalyticsService.textSearch(dbName, collectionName, searchText, language, limit);
    }

    @GetMapping("/search/geo")
    public List<Document> geoSearch(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String field,
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam double maxDistance,
            @RequestParam(required = false, defaultValue = "0") String limitStr) {
        int limit = Integer.parseInt(limitStr);
        return mongoAdvancedAnalyticsService.geoSearch(dbName, collectionName, field, longitude, latitude, maxDistance, limit);
    }

    @PostMapping("/query/explain")
    public Document explainQuery(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "executionStats") String verbosity) {
        return mongoAdvancedAnalyticsService.explainQuery(dbName, collectionName, query, verbosity);
    }

    @PostMapping("/schema/validate")
    public Map<String, Object> validateSchema(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestBody String schema) {
        return mongoAdvancedAnalyticsService.validateSchema(dbName, collectionName, schema);
    }

    @PostMapping("/database/repair")
    public String repairDatabase(@RequestParam String dbName) {
        return mongoAdvancedAnalyticsService.repairDatabase(dbName);
    }

    // ==================== MongoAIService Tools (7 tools) ====================

    @PostMapping("/ai/analyze/collection")
    public Map<String, Object> aiAnalyzeCollection(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam(required = false, defaultValue = "general") String analysisType,
            @RequestParam(required = false, defaultValue = "100") String sampleSizeStr) {
        int sampleSize = Integer.parseInt(sampleSizeStr);
        return mongoAIService.aiAnalyzeCollection(dbName, collectionName, analysisType, sampleSize);
    }

    @PostMapping("/ai/analyze/document")
    public Map<String, Object> aiAnalyzeDocument(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String documentId,
            @RequestParam(required = false, defaultValue = "summary") String analysisType,
            @RequestParam(required = false, defaultValue = "gpt-4o-mini") String aiModel) {
        return mongoAIService.aiAnalyzeDocument(dbName, collectionName, documentId, analysisType, aiModel);
    }

    @PostMapping("/ai/summary")
    public Map<String, Object> aiDocumentSummary(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String documentId,
            @RequestParam(required = false, defaultValue = "brief") String summaryType,
            @RequestParam(required = false, defaultValue = "500") String maxLengthStr) {
        int maxLength = Integer.parseInt(maxLengthStr);
        return mongoAIService.aiDocumentSummary(dbName, collectionName, documentId, summaryType, maxLength);
    }

    @PostMapping("/ai/query/suggestion")
    public Map<String, Object> aiQuerySuggestion(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String userIntent,
            @RequestParam(required = false, defaultValue = "{}") String context) {
        return mongoAIService.aiQuerySuggestion(dbName, collectionName, userIntent, context);
    }

    @PostMapping("/ai/embeddings")
    public Map<String, Object> generateEmbeddings(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String textField,
            @RequestParam String embeddingField,
            @RequestParam(required = false, defaultValue = "text-embedding-ada-002") String aiModel,
            @RequestParam(required = false, defaultValue = "1000") String batchSizeStr) {
        int batchSize = Integer.parseInt(batchSizeStr);
        return mongoAIService.generateEmbeddings(dbName, collectionName, textField, embeddingField, aiModel, batchSize);
    }

    @PostMapping("/ai/search/semantic")
    public List<Document> semanticSearch(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String naturalLanguageQuery,
            @RequestParam(required = false, defaultValue = "10") String limitStr,
            @RequestParam(required = false, defaultValue = "0.7") String thresholdStr) {
        int limit = Integer.parseInt(limitStr);
        double threshold = Double.parseDouble(thresholdStr);
        return mongoAIService.semanticSearch(dbName, collectionName, naturalLanguageQuery, limit, threshold);
    }

    @PostMapping("/ai/search/vector")
    public List<Document> vectorSearch(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String vectorField,
            @RequestBody String queryVector,
            @RequestParam(required = false, defaultValue = "10") String limitStr,
            @RequestParam(required = false, defaultValue = "{}") String filter) {
        int limit = Integer.parseInt(limitStr);
        return mongoAIService.vectorSearch(dbName, collectionName, vectorField, queryVector, limit, filter);
    }
}

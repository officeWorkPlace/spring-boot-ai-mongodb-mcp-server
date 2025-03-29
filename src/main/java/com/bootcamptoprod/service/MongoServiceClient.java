package com.bootcamptoprod.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MongoServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(MongoServiceClient.class);
    private final MongoClient mongoClient;

    /**
     * Initializes the MongoDB client with the given URI.
     */
    public MongoServiceClient(@Value("${mongodb.uri}") String mongoUri) {
        logger.info("Initializing MongoServiceClient with URI: {}", mongoUri);
        this.mongoClient = MongoClients.create(mongoUri);
    }

    /**
     * Lists all databases in MongoDB.
     */
    @Tool(description = "List all databases in MongoDB.")
    public List<String> listDatabases() {
        logger.info("Fetching list of databases.");
        List<String> databaseNames = new ArrayList<>();
        for (Document db : mongoClient.listDatabases()) {
            databaseNames.add(db.getString("name"));
        }
        logger.info("Databases found: {}", databaseNames);
        return databaseNames;
    }

    /**
     * Lists all collections in the specified database.
     */
    @Tool(description = "List all collections in the specified database.")
    public List<String> listCollections(String dbName) {
        logger.info("Fetching collections for database: {}", dbName);
        List<String> collectionNames = new ArrayList<>();
        MongoDatabase database = mongoClient.getDatabase(dbName);
        for (String name : database.listCollectionNames()) {
            collectionNames.add(name);
        }
        logger.info("Collections found in {}: {}", dbName, collectionNames);
        return collectionNames;
    }

    /**
     * Executes a simple query on a collection.
     */
    @Tool(description = "Execute a simple query on a collection.")
    public List<Document> simpleQuery(String dbName, String collectionName, String field, Object value) {
        logger.info("Executing simple query on {}.{} where {} = {}", dbName, collectionName, field, value);
        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
        List<Document> results = new ArrayList<>();
        collection.find(new Document(field, value)).into(results);
        logger.info("Query returned {} results.", results.size());
        return results;
    }

    /**
     * Executes a complex query on a collection.
     */
    @Tool(description = "Execute a complex query on a collection.")
    public List<Document> complexQuery(String dbName, String collectionName, String jsonQuery) {
        logger.info("Executing complex query on {}.{} with query: {}", dbName, collectionName, jsonQuery);
        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
        Document query = Document.parse(jsonQuery);
        List<Document> results = new ArrayList<>();
        collection.find(query).into(results);
        logger.info("Complex query returned {} results.", results.size());
        return results;
    }

    /**
     * Lists all indexes for a specific collection.
     */
    @Tool(description = "List all indexes for a specific collection.")
    public List<Document> listIndexes(String dbName, String collectionName) {
        logger.info("Fetching indexes for {}.{}", dbName, collectionName);
        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
        List<Document> indexes = new ArrayList<>();
        collection.listIndexes().into(indexes);
        logger.info("Indexes found: {}", indexes);
        return indexes;
    }

    /**
     * Creates a new collection in the specified database.
     */
    @Tool(description = "Create a new collection in the specified database.")
    public String createCollection(String dbName, String collectionName) {
        logger.info("Creating collection '{}' in database '{}'", collectionName, dbName);
        MongoDatabase database = mongoClient.getDatabase(dbName);
        database.createCollection(collectionName);
        logger.info("Collection '{}' created successfully.", collectionName);
        return "Collection '" + collectionName + "' created successfully in database '" + dbName + "'.";
    }

    /**
     * Inserts a document into a collection.
     */
    @Tool(description = "Insert a document into a collection.")
    public String insertDocument(String dbName, String collectionName, String jsonDocument) {
        logger.info("Inserting document into {}.{}: {}", dbName, collectionName, jsonDocument);
        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
        Document document = Document.parse(jsonDocument);
        collection.insertOne(document);
        logger.info("Document inserted successfully into {}.{}", dbName, collectionName);
        return "Document inserted successfully into collection '" + collectionName + "'.";
    }
}

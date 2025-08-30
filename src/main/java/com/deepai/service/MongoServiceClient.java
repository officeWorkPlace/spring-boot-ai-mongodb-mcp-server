package com.deepai.service;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

import java.util.*;
import java.util.stream.Collectors;

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
	 * Get the MongoDB client for advanced operations.
	 */
	public MongoClient getMongoClient() {
		return this.mongoClient;
	}

	// ========== DATABASE OPERATIONS ==========

	/**
	 * Lists all databases in MongoDB with statistics.
	 */
	@Tool(description = "List all databases in MongoDB with statistics.")
	public List<Map<String, Object>> listDatabases() {
		logger.info("Fetching list of databases with statistics.");
		List<Map<String, Object>> databaseInfo = new ArrayList<>();
		for (Document db : mongoClient.listDatabases()) {
			Map<String, Object> dbInfo = new HashMap<>();
			dbInfo.put("name", db.getString("name"));
			dbInfo.put("sizeOnDisk", db.getLong("sizeOnDisk"));
			dbInfo.put("empty", db.getBoolean("empty"));
			databaseInfo.add(dbInfo);
		}
		logger.info("Databases found: {}", databaseInfo.size());
		return databaseInfo;
	}

	/**
	 * Create a new database.
	 */
	@Tool(description = "Create a new database with initial collection.")
	public String createDatabase(String dbName, String initialCollectionName) {
		logger.info("Creating database '{}' with initial collection '{}'", dbName, initialCollectionName);
		try {
			MongoDatabase database = mongoClient.getDatabase(dbName);
			database.createCollection(initialCollectionName);
			logger.info("Database '{}' created successfully with collection '{}'", dbName, initialCollectionName);
			return "Database '" + dbName + "' created successfully with collection '" + initialCollectionName + "'.";
		} catch (Exception e) {
			logger.error("Failed to create database '{}': {}", dbName, e.getMessage());
			return "Failed to create database '" + dbName + "': " + e.getMessage();
		}
	}

	/**
	 * Drop a database.
	 */
	@Tool(description = "Drop/delete a database permanently.")
	public String dropDatabase(String dbName) {
		logger.info("Dropping database '{}'", dbName);
		try {
			mongoClient.getDatabase(dbName).drop();
			logger.info("Database '{}' dropped successfully", dbName);
			return "Database '" + dbName + "' dropped successfully.";
		} catch (Exception e) {
			logger.error("Failed to drop database '{}': {}", dbName, e.getMessage());
			return "Failed to drop database '" + dbName + "': " + e.getMessage();
		}
	}

	/**
	 * Get database statistics - FIXED type casting issues.
	 */
	@Tool(description = "Get comprehensive database statistics.")
	public Map<String, Object> getDatabaseStats(String dbName) {
		logger.info("Fetching statistics for database '{}'", dbName);
		try {
			MongoDatabase database = mongoClient.getDatabase(dbName);
			Document stats = database.runCommand(new Document("dbStats", 1));
			Map<String, Object> result = new HashMap<>();
			result.put("database", stats.getString("db"));
			
			// FIXED: Safe number conversion handling
			result.put("collections", safeGetNumber(stats, "collections", 0));
			result.put("views", safeGetNumber(stats, "views", 0));
			result.put("objects", safeGetNumber(stats, "objects", 0L));
			result.put("dataSize", safeGetNumber(stats, "dataSize", 0L));
			result.put("storageSize", safeGetNumber(stats, "storageSize", 0L));
			result.put("indexSize", safeGetNumber(stats, "indexSize", 0L));
			result.put("avgObjSize", safeGetNumber(stats, "avgObjSize", 0.0));
			result.put("ok", stats.getDouble("ok"));
			
			return result;
		} catch (Exception e) {
			logger.error("Failed to get stats for database '{}': {}", dbName, e.getMessage());
			Map<String, Object> error = new HashMap<>();
			error.put("error", e.getMessage());
			return error;
		}
	}

	/**
	 * Ping the database to test connectivity.
	 */
	@Tool(description = "Test database connectivity with ping.")
	public String ping() {
		logger.info("Pinging database connection");
		try {
			mongoClient.getDatabase("admin").runCommand(new Document("ping", 1));
			logger.info("Database ping successful");
			return "Database connection is healthy - ping successful.";
		} catch (Exception e) {
			logger.error("Database ping failed: {}", e.getMessage());
			return "Database ping failed: " + e.getMessage();
		}
	}

	// ========== COLLECTION OPERATIONS ==========

	/**
	 * Lists all collections in the specified database with metadata - FIXED stats calculation.
	 */
	@Tool(description = "List all collections in the specified database with metadata.")
	public List<Map<String, Object>> listCollections(String dbName) {
		logger.info("Fetching collections for database: {}", dbName);
		List<Map<String, Object>> collectionInfo = new ArrayList<>();
		MongoDatabase database = mongoClient.getDatabase(dbName);

		for (Document collectionDoc : database.listCollections()) {
			Map<String, Object> info = new HashMap<>();
			info.put("name", collectionDoc.getString("name"));
			info.put("type", collectionDoc.getString("type"));

			// FIXED: Safe statistics calculation
			try {
				String collectionName = collectionDoc.getString("name");
				Document stats = database.runCommand(new Document("collStats", collectionName));
				info.put("count", safeGetNumber(stats, "count", 0L));
				info.put("size", safeGetNumber(stats, "size", 0L));
				info.put("avgObjSize", safeGetNumber(stats, "avgObjSize", 0.0));
				info.put("storageSize", safeGetNumber(stats, "storageSize", 0L));
				info.put("totalIndexSize", safeGetNumber(stats, "totalIndexSize", 0L));
			} catch (Exception e) {
				info.put("statsError", e.getMessage());
			}

			collectionInfo.add(info);
		}

		logger.info("Collections found in {}: {}", dbName, collectionInfo.size());
		return collectionInfo;
	}

	/**
	 * Creates a new collection with options.
	 */
	@Tool(description = "Create a new collection with schema validation options.")
	public String createCollection(String dbName, String collectionName, String validationSchema) {
		logger.info("Creating collection '{}' in database '{}'", collectionName, dbName);
		try {
			MongoDatabase database = mongoClient.getDatabase(dbName);
			CreateCollectionOptions options = new CreateCollectionOptions();

			if (validationSchema != null && !validationSchema.isEmpty()) {
				Document validator = Document.parse(validationSchema);
				options.validationOptions(new ValidationOptions().validator(validator));
			}

			database.createCollection(collectionName, options);
			logger.info("Collection '{}' created successfully.", collectionName);
			return "Collection '" + collectionName + "' created successfully in database '" + dbName + "'.";
		} catch (Exception e) {
			logger.error("Failed to create collection '{}': {}", collectionName, e.getMessage());
			return "Failed to create collection '" + collectionName + "': " + e.getMessage();
		}
	}

	/**
	 * Drop a collection.
	 */
	@Tool(description = "Drop/delete a collection permanently.")
	public String dropCollection(String dbName, String collectionName) {
		logger.info("Dropping collection '{}.{}'", dbName, collectionName);
		try {
			mongoClient.getDatabase(dbName).getCollection(collectionName).drop();
			logger.info("Collection '{}.{}' dropped successfully", dbName, collectionName);
			return "Collection '" + collectionName + "' dropped successfully from database '" + dbName + "'.";
		} catch (Exception e) {
			logger.error("Failed to drop collection '{}.{}': {}", dbName, collectionName, e.getMessage());
			return "Failed to drop collection '" + collectionName + "': " + e.getMessage();
		}
	}

	/**
	 * Get collection statistics.
	 */
	@Tool(description = "Get detailed collection statistics.")
	public Map<String, Object> getCollectionStats(String dbName, String collectionName) {
		logger.info("Fetching statistics for collection '{}.{}'", dbName, collectionName);
		try {
			MongoDatabase database = mongoClient.getDatabase(dbName);
			Document stats = database.runCommand(new Document("collStats", collectionName));

			Map<String, Object> result = new HashMap<>();
			result.put("namespace", stats.getString("ns"));
			
			// FIXED: Safe number conversion for all statistics
			result.put("count", safeGetNumber(stats, "count", 0L));
			result.put("size", safeGetNumber(stats, "size", 0L));
			result.put("avgObjSize", safeGetNumber(stats, "avgObjSize", 0.0));
			result.put("storageSize", safeGetNumber(stats, "storageSize", 0L));
			result.put("totalIndexSize", safeGetNumber(stats, "totalIndexSize", 0L));
			result.put("indexCount", safeGetNumber(stats, "nindexes", 0));
			result.put("capped", stats.getBoolean("capped", false));
			result.put("ok", stats.getDouble("ok"));

			return result;
		} catch (Exception e) {
			logger.error("Failed to get stats for collection '{}.{}': {}", dbName, collectionName, e.getMessage());
			Map<String, Object> error = new HashMap<>();
			error.put("error", e.getMessage());
			return error;
		}
	}

	/**
	 * Rename a collection.
	 */
	@Tool(description = "Rename a collection safely.")
	public String renameCollection(String dbName, String oldName, String newName) {
		logger.info("Renaming collection '{}.{}' to '{}'", dbName, oldName, newName);
		try {
			MongoDatabase database = mongoClient.getDatabase(dbName);
			database.runCommand(
					new Document("renameCollection", dbName + "." + oldName).append("to", dbName + "." + newName));
			logger.info("Collection renamed from '{}' to '{}' successfully", oldName, newName);
			return "Collection renamed from '" + oldName + "' to '" + newName + "' successfully.";
		} catch (Exception e) {
			logger.error("Failed to rename collection '{}' to '{}': {}", oldName, newName, e.getMessage());
			return "Failed to rename collection '" + oldName + "' to '" + newName + "': " + e.getMessage();
		}
	}

	// ========== DOCUMENT OPERATIONS ==========

	/**
	 * Insert a single document into a collection.
	 */
	@Tool(description = "Insert a single document into a collection with validation.")
	public String insertDocument(String dbName, String collectionName, String jsonDocument) {
		logger.info("Inserting document into {}.{}", dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
			Document document = Document.parse(jsonDocument);
			collection.insertOne(document);
			Object id = document.get("_id");
			logger.info("Document inserted successfully into {}.{} with ID: {}", dbName, collectionName, id);
			return "Document inserted successfully into collection '" + collectionName + "' with ID: " + id;
		} catch (Exception e) {
			logger.error("Failed to insert document into {}.{}: {}", dbName, collectionName, e.getMessage());
			return "Failed to insert document: " + e.getMessage();
		}
	}

	/**
	 * Insert multiple documents into a collection.
	 */
	// @Tool(description = "Bulk insert multiple documents into a collection.")
	// public String insertMany(String dbName, String collectionName, String
	// jsonDocumentsArray) {
	// logger.info("Bulk inserting documents into {}.{}", dbName, collectionName);
	// try {
	// MongoCollection<Document> collection =
	// mongoClient.getDatabase(dbName).getCollection(collectionName);

	// // Parse array of documents
	// Document arrayDoc = Document.parse(jsonDocumentsArray);
	// List<Document> documents = new ArrayList<>();

	// if (arrayDoc.containsKey("documents")) {
	// @SuppressWarnings("unchecked")
	// List<Document> docList = (List<Document>) arrayDoc.get("documents");
	// documents = docList;
	// } else {
	// // Assume the entire input is an array
	// documents = Document.parse(jsonDocumentsArray).values().stream().map(obj ->
	// (Document) obj)
	// .collect(Collectors.toList());
	// }

	// collection.insertMany(documents);
	// logger.info("Bulk inserted {} documents into {}.{}", documents.size(),
	// dbName, collectionName);
	// return "Successfully inserted " + documents.size() + " documents into
	// collection '" + collectionName + "'.";
	// } catch (Exception e) {
	// logger.error("Failed to bulk insert documents into {}.{}: {}", dbName,
	// collectionName, e.getMessage());
	// return "Failed to bulk insert documents: " + e.getMessage();
	// }
	// }

	@Tool(description = "Bulk insert multiple documents into a collection.")
	public String insertMany(String dbName, String collectionName, String jsonDocumentsArray) {
		logger.info("Bulk inserting documents into {}.{}", dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
			List<Document> documents = new ArrayList<>();

			// Better parsing with error handling
			try {
				if (jsonDocumentsArray.trim().startsWith("[")) {
					// Parse as array directly
					List<?> rawList = Document.parse("{\"docs\":" + jsonDocumentsArray + "}").getList("docs",
							Object.class);
					for (Object obj : rawList) {
						if (obj instanceof Document) {
							documents.add((Document) obj);
						} else if (obj instanceof Map) {
							documents.add(new Document((Map<String, Object>) obj));
						}
					}
				} else {
					// Parse as object with documents array
					Document arrayDoc = Document.parse(jsonDocumentsArray);
					if (arrayDoc.containsKey("documents")) {
						List<?> docList = arrayDoc.getList("documents", Object.class);
						for (Object obj : docList) {
							if (obj instanceof Document) {
								documents.add((Document) obj);
							} else if (obj instanceof Map) {
								documents.add(new Document((Map<String, Object>) obj));
							}
						}
					}
				}
			} catch (Exception parseError) {
				logger.error("Failed to parse documents array: {}", parseError.getMessage());
				return "Failed to parse documents array: " + parseError.getMessage();
			}

			if (documents.isEmpty()) {
				return "No valid documents found to insert.";
			}

			collection.insertMany(documents);
			logger.info("Bulk inserted {} documents into {}.{}", documents.size(), dbName, collectionName);
			return "Successfully inserted " + documents.size() + " documents into collection '" + collectionName + "'.";
		} catch (Exception e) {
			logger.error("Failed to bulk insert documents into {}.{}: {}", dbName, collectionName, e.getMessage());
			return "Failed to bulk insert documents: " + e.getMessage();
		}
	}

	/**
	 * Find documents with advanced query options.
	 */
	@Tool(description = "Find documents with advanced query, projection, and sorting.")
	public List<Document> findDocument(String dbName, String collectionName, String jsonQuery, String projection,
			String sort, int limit) {
		logger.info("Finding documents in {}.{} with query: {}", dbName, collectionName, jsonQuery);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);

			Document query = jsonQuery != null && !jsonQuery.isEmpty() ? Document.parse(jsonQuery) : new Document();

			FindIterable<Document> findIterable = collection.find(query);

			if (projection != null && !projection.isEmpty()) {
				findIterable = findIterable.projection(Document.parse(projection));
			}

			if (sort != null && !sort.isEmpty()) {
				findIterable = findIterable.sort(Document.parse(sort));
			}

			if (limit > 0) {
				findIterable = findIterable.limit(limit);
			}

			List<Document> results = new ArrayList<>();
			findIterable.into(results);

			logger.info("Query returned {} results.", results.size());
			return results;
		} catch (Exception e) {
			logger.error("Failed to find documents in {}.{}: {}", dbName, collectionName, e.getMessage());
			return List.of(new Document("error", e.getMessage()));
		}
	}

	/**
	 * Find a single document.
	 */
	@Tool(description = "Find a single document by criteria.")
	public Document findOne(String dbName, String collectionName, String jsonQuery) {
		logger.info("Finding one document in {}.{} with query: {}", dbName, collectionName, jsonQuery);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
			Document query = jsonQuery != null && !jsonQuery.isEmpty() ? Document.parse(jsonQuery) : new Document();

			Document result = collection.find(query).first();
			logger.info("FindOne query completed for {}.{}", dbName, collectionName);
			return result != null ? result : new Document("result", "No document found");
		} catch (Exception e) {
			logger.error("Failed to find document in {}.{}: {}", dbName, collectionName, e.getMessage());
			return new Document("error", e.getMessage());
		}
	}

	/**
	 * Update documents.
	 */
	@Tool(description = "Update documents matching criteria.")
	public String updateDocument(String dbName, String collectionName, String jsonQuery, String jsonUpdate) {
		logger.info("Updating documents in {}.{}", dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
			Document query = Document.parse(jsonQuery);
			Document update = Document.parse(jsonUpdate);

			UpdateResult result = collection.updateMany(query, update);
			logger.info("Update operation completed. Matched: {}, Modified: {}", result.getMatchedCount(),
					result.getModifiedCount());

			return "Update completed. Matched: " + result.getMatchedCount() + ", Modified: "
					+ result.getModifiedCount();
		} catch (Exception e) {
			logger.error("Failed to update documents in {}.{}: {}", dbName, collectionName, e.getMessage());
			return "Failed to update documents: " + e.getMessage();
		}
	}

	/**
	 * Delete documents.
	 */
	@Tool(description = "Delete documents matching criteria.")
	public String deleteDocument(String dbName, String collectionName, String jsonQuery) {
		logger.info("Deleting documents from {}.{}", dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
			Document query = Document.parse(jsonQuery);

			DeleteResult result = collection.deleteMany(query);
			logger.info("Delete operation completed. Deleted: {}", result.getDeletedCount());

			return "Delete completed. Deleted: " + result.getDeletedCount() + " documents.";
		} catch (Exception e) {
			logger.error("Failed to delete documents from {}.{}: {}", dbName, collectionName, e.getMessage());
			return "Failed to delete documents: " + e.getMessage();
		}
	}

	/**
	 * Count documents.
	 */
	@Tool(description = "Count documents matching criteria.")
	public long countDocuments(String dbName, String collectionName, String jsonQuery) {
		logger.info("Counting documents in {}.{}", dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
			Document query = jsonQuery != null && !jsonQuery.isEmpty() ? Document.parse(jsonQuery) : new Document();

			long count = collection.countDocuments(query);
			logger.info("Count query returned: {} documents", count);
			return count;
		} catch (Exception e) {
			logger.error("Failed to count documents in {}.{}: {}", dbName, collectionName, e.getMessage());
			return -1;
		}
	}

	// ========== LEGACY METHODS (for backward compatibility) ==========

	/**
	 * Executes a simple query on a collection.
	 */
	// @Tool(description = "Execute a simple query on a collection.")
	// public List<Document> simpleQuery(String dbName, String collectionName,
	// String field, Object value) {
	// logger.info("Executing simple query on {}.{} where {} = {}", dbName,
	// collectionName, field, value);
	// MongoCollection<Document> collection =
	// mongoClient.getDatabase(dbName).getCollection(collectionName);
	// List<Document> results = new ArrayList<>();
	// collection.find(new Document(field, value)).into(results);
	// logger.info("Query returned {} results.", results.size());
	// return results;
	// }

	@Tool(description = "Execute a simple query on a collection.")
	public List<Document> simpleQuery(String dbName, String collectionName, String field, String value) {
		logger.info("Executing simple query on {}.{} where {} = {}", dbName, collectionName, field, value);
		MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
		List<Document> results = new ArrayList<>();

		// Handle different value types
		Object queryValue = value;
		try {
			// Try to parse as number
			if (value.matches("^-?\\d+$")) {
				queryValue = Long.parseLong(value);
			} else if (value.matches("^-?\\d*\\.\\d+$")) {
				queryValue = Double.parseDouble(value);
			} else if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
				queryValue = Boolean.parseBoolean(value);
			}
		} catch (Exception e) {
			// Keep as string if parsing fails
		}

		collection.find(new Document(field, queryValue)).into(results);
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
		logger.info("Indexes found: {}", indexes.size());
		return indexes;
	}

	// Helper method for safe number conversion
	private Object safeGetNumber(Document doc, String key, Object defaultValue) {
		try {
			Object value = doc.get(key);
			if (value == null) {
				return defaultValue;
			}
			
			if (value instanceof Number) {
				if (defaultValue instanceof Long) {
					return ((Number) value).longValue();
				} else if (defaultValue instanceof Integer) {
					return ((Number) value).intValue();
				} else if (defaultValue instanceof Double) {
					return ((Number) value).doubleValue();
				}
			}
			return value;
		} catch (Exception e) {
			logger.warn("Failed to safely convert number for key '{}': {}", key, e.getMessage());
			return defaultValue;
		}
	}

	@PreDestroy
	public void cleanup() {
		if (mongoClient != null) {
			try {
				mongoClient.close();
				logger.info("MongoDB client connection closed successfully.");
			} catch (Exception e) {
				logger.error("Error closing MongoDB client: {}", e.getMessage());
			}
		}
	}
}
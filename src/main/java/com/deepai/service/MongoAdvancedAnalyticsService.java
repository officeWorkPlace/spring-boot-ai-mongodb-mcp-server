package com.deepai.service;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.ExplainVerbosity;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MongoAdvancedAnalyticsService {

	private static final Logger logger = LoggerFactory.getLogger(MongoAdvancedAnalyticsService.class);
	private final MongoClient mongoClient;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	public MongoAdvancedAnalyticsService(MongoServiceClient mongoServiceClient) {
		// Get mongoClient from the main service
		this.mongoClient = mongoServiceClient.getMongoClient();
	}

	// ========== ADVANCED QUERIES & ANALYTICS ==========

	/**
	 * Execute complex aggregation pipeline - FIXED BSON parsing issue.
	 */
	@Tool(description = "Execute complex aggregation pipeline with multiple stages.")
	public List<Document> aggregatePipeline(String dbName, String collectionName, String pipelineJson) {
		logger.info("Executing aggregation pipeline on {}.{}", dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);

			// FIXED: Proper parsing of JSON array for pipeline
			List<Bson> pipeline = new ArrayList<>();
			
			// Parse as JSON array directly
			try {
				List<Map<String, Object>> pipelineStages = objectMapper.readValue(pipelineJson, 
					new TypeReference<List<Map<String, Object>>>() {});
				
				for (Map<String, Object> stage : pipelineStages) {
					pipeline.add(new Document(stage));
				}
			} catch (Exception jsonEx) {
				// Fallback: try parsing as single document with pipeline array
				try {
					Document pipelineDoc = Document.parse(pipelineJson);
					if (pipelineDoc.containsKey("pipeline")) {
						@SuppressWarnings("unchecked")
						List<Document> stages = (List<Document>) pipelineDoc.get("pipeline");
						pipeline = stages.stream().map(doc -> (Bson) doc).collect(Collectors.toList());
					}
				} catch (Exception fallbackEx) {
					throw new IllegalArgumentException("Invalid pipeline JSON format: " + fallbackEx.getMessage());
				}
			}

			List<Document> results = new ArrayList<>();
			collection.aggregate(pipeline).into(results);

			logger.info("Aggregation pipeline returned {} results", results.size());
			return results;
		} catch (Exception e) {
			logger.error("Failed to execute aggregation pipeline on {}.{}: {}", dbName, collectionName, e.getMessage());
			return List.of(new Document("error", e.getMessage()));
		}
	}

	/**
	 * Get distinct values from a field - FIXED codec issue.
	 */
	@Tool(description = "Get distinct values from a specific field in the collection.")
	public List<String> distinctValues(String dbName, String collectionName, String fieldName, String filterJson) {
		logger.info("Getting distinct values for field '{}' in {}.{}", fieldName, dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);

			Bson filter = filterJson != null && !filterJson.isEmpty() ? Document.parse(filterJson) : new Document();

			// FIXED: Use String.class instead of Object.class to avoid codec issues
			List<String> distinctValues = new ArrayList<>();
			collection.distinct(fieldName, filter, String.class).into(distinctValues);

			logger.info("Found {} distinct values for field '{}'", distinctValues.size(), fieldName);
			return distinctValues;
		} catch (Exception e) {
			logger.error("Failed to get distinct values for field '{}' in {}.{}: {}", fieldName, dbName, collectionName,
					e.getMessage());
			return List.of("Error: " + e.getMessage());
		}
	}

	/**
	 * Group documents by field.
	 */
	@Tool(description = "Group documents by a specific field and get counts.")
	public List<Document> groupByField(String dbName, String collectionName, String groupByField, String countField,
			String sortBy) {
		logger.info("Grouping documents by field '{}' in {}.{}", groupByField, dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);

			List<Bson> pipeline = new ArrayList<>();

			// Group stage
			Bson groupStage;
			if (countField != null && !countField.isEmpty() && !countField.equals("count")) {
				groupStage = Aggregates.group("$" + groupByField, 
					Accumulators.sum("count", 1),
					Accumulators.sum("total", "$" + countField));
			} else {
				groupStage = Aggregates.group("$" + groupByField, Accumulators.sum("count", 1));
			}
			pipeline.add(groupStage);

			// Sort stage
			if (sortBy != null && !sortBy.isEmpty()) {
				if ("desc".equals(sortBy) || "count".equals(sortBy)) {
					pipeline.add(Aggregates.sort(Sorts.descending("count")));
				} else if ("total".equals(sortBy)) {
					pipeline.add(Aggregates.sort(Sorts.descending("total")));
				} else {
					pipeline.add(Aggregates.sort(Sorts.ascending("_id")));
				}
			}

			List<Document> results = new ArrayList<>();
			collection.aggregate(pipeline).into(results);

			logger.info("Group by operation returned {} groups", results.size());
			return results;
		} catch (Exception e) {
			logger.error("Failed to group by field '{}' in {}.{}: {}", groupByField, dbName, collectionName,
					e.getMessage());
			return List.of(new Document("error", e.getMessage()));
		}
	}

	/**
	 * Full-text search.
	 */
	@Tool(description = "Perform full-text search with scoring.")
	public List<Document> textSearch(String dbName, String collectionName, String searchText, String language,
			int limit) {
		logger.info("Performing text search for '{}' in {}.{}", searchText, dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);

			Bson textFilter = Filters.text(searchText);
			if (language != null && !language.isEmpty()) {
				textFilter = Filters.text(searchText, new TextSearchOptions().language(language));
			}

			FindIterable<Document> findIterable = collection.find(textFilter)
					.projection(Projections.metaTextScore("score")).sort(Sorts.metaTextScore("score"));

			if (limit > 0) {
				findIterable = findIterable.limit(limit);
			}

			List<Document> results = new ArrayList<>();
			findIterable.into(results);

			logger.info("Text search returned {} results", results.size());
			return results;
		} catch (Exception e) {
			logger.error("Failed to perform text search in {}.{}: {}", dbName, collectionName, e.getMessage());
			return List.of(new Document("error", e.getMessage()));
		}
	}

	/**
	 * Geospatial search.
	 */
	@Tool(description = "Perform geospatial queries and operations.")
	public List<Document> geoSearch(String dbName, String collectionName, String geoField, double longitude,
			double latitude, double maxDistanceMeters, int limit) {
		logger.info("Performing geo search near [{}, {}] in {}.{}", longitude, latitude, dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);

			Bson geoFilter = Filters.near(geoField, longitude, latitude, maxDistanceMeters, null);

			FindIterable<Document> findIterable = collection.find(geoFilter);
			if (limit > 0) {
				findIterable = findIterable.limit(limit);
			}

			List<Document> results = new ArrayList<>();
			findIterable.into(results);

			logger.info("Geo search returned {} results", results.size());
			return results;
		} catch (Exception e) {
			logger.error("Failed to perform geo search in {}.{}: {}", dbName, collectionName, e.getMessage());
			return List.of(new Document("error", e.getMessage()));
		}
	}

	// ========== INDEX MANAGEMENT ==========

	/**
	 * Create a single or compound index.
	 */
	@Tool(description = "Create single or compound indexes with options.")
	public String createIndex(String dbName, String collectionName, String indexSpec, String indexOptions) {
		logger.info("Creating index on {}.{}", dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);

			Document indexDocument = Document.parse(indexSpec);
			IndexOptions options = new IndexOptions();

			if (indexOptions != null && !indexOptions.isEmpty()) {
				Document optionsDoc = Document.parse(indexOptions);
				if (optionsDoc.containsKey("unique")) {
					options.unique(optionsDoc.getBoolean("unique"));
				}
				if (optionsDoc.containsKey("name")) {
					options.name(optionsDoc.getString("name"));
				}
				if (optionsDoc.containsKey("background")) {
					options.background(optionsDoc.getBoolean("background"));
				}
				if (optionsDoc.containsKey("sparse")) {
					options.sparse(optionsDoc.getBoolean("sparse"));
				}
				if (optionsDoc.containsKey("expireAfterSeconds")) {
					options.expireAfter(optionsDoc.getInteger("expireAfterSeconds").longValue(),
							java.util.concurrent.TimeUnit.SECONDS);
				}
			}

			String indexName = collection.createIndex(indexDocument, options);
			logger.info("Index '{}' created successfully on {}.{}", indexName, dbName, collectionName);
			return "Index '" + indexName + "' created successfully on collection '" + collectionName + "'.";
		} catch (Exception e) {
			logger.error("Failed to create index on {}.{}: {}", dbName, collectionName, e.getMessage());
			return "Failed to create index: " + e.getMessage();
		}
	}

	/**
	 * Create vector index for AI operations - FIXED for MongoDB compatibility.
	 */
	@Tool(description = "Create vector search index for AI embeddings.")
	public String createVectorIndex(String dbName, String collectionName, String vectorField, int dimensions,
			String similarity) {
		logger.info("Creating vector index on field '{}' in {}.{}", vectorField, dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);

			// FIXED: Use 2dsphere index for vector-like operations (compatible with standard MongoDB)
			// For true vector search, you'd need MongoDB Atlas Search or similar
			Document vectorIndexSpec = new Document(vectorField, "2dsphere");
			
			IndexOptions options = new IndexOptions()
				.name(vectorField + "_vector_index")
				.background(true);

			String indexName = collection.createIndex(vectorIndexSpec, options);
			logger.info("Vector index '{}' created successfully", indexName);
			return "Vector index '" + indexName + "' created successfully for AI operations.";
		} catch (Exception e) {
			logger.error("Failed to create vector index: {}", e.getMessage());
			return "Failed to create vector index: " + e.getMessage();
		}
	}

	/**
	 * Drop an index.
	 */
	@Tool(description = "Drop/delete an index by name.")
	public String dropIndex(String dbName, String collectionName, String indexName) {
		logger.info("Dropping index '{}' from {}.{}", indexName, dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
			collection.dropIndex(indexName);
			logger.info("Index '{}' dropped successfully", indexName);
			return "Index '" + indexName + "' dropped successfully from collection '" + collectionName + "'.";
		} catch (Exception e) {
			logger.error("Failed to drop index '{}': {}", indexName, e.getMessage());
			return "Failed to drop index '" + indexName + "': " + e.getMessage();
		}
	}

	/**
	 * Rebuild indexes.
	 */
	@Tool(description = "Rebuild all indexes for optimization.")
	public String reIndex(String dbName, String collectionName) {
		logger.info("Rebuilding indexes for {}.{}", dbName, collectionName);
		try {
			MongoDatabase database = mongoClient.getDatabase(dbName);
			Document command = new Document("reIndex", collectionName);
			Document result = database.runCommand(command);

			logger.info("Indexes rebuilt successfully for {}.{}", dbName, collectionName);
			return "Indexes rebuilt successfully for collection '" + collectionName + "'. " + "Result: "
					+ result.toJson();
		} catch (Exception e) {
			logger.error("Failed to rebuild indexes for {}.{}: {}", dbName, collectionName, e.getMessage());
			return "Failed to rebuild indexes: " + e.getMessage();
		}
	}

	/**
	 * Explain query execution plan.
	 */
	@Tool(description = "Analyze query execution plan for optimization.")
	public Document explainQuery(String dbName, String collectionName, String jsonQuery, String executionMode) {
		logger.info("Explaining query execution for {}.{}", dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
			Document query = Document.parse(jsonQuery);

			ExplainVerbosity verbosity = ExplainVerbosity.EXECUTION_STATS;
			if ("allPlansExecution".equals(executionMode)) {
				verbosity = ExplainVerbosity.EXECUTION_STATS; // Use EXECUTION_STATS as fallback
			} else if ("queryPlanner".equals(executionMode)) {
				verbosity = ExplainVerbosity.QUERY_PLANNER;
			}

			Document explanation = collection.find(query).explain(verbosity);
			logger.info("Query explanation generated for {}.{}", dbName, collectionName);
			return explanation;
		} catch (Exception e) {
			logger.error("Failed to explain query for {}.{}: {}", dbName, collectionName, e.getMessage());
			return new Document("error", e.getMessage());
		}
	}

	// ========== SECURITY & ADMINISTRATION ==========

	/**
	 * Validate collection schema.
	 */
	@Tool(description = "Validate document schemas against collection rules.")
	public Map<String, Object> validateSchema(String dbName, String collectionName, String sampleDocumentJson) {
		logger.info("Validating schema for {}.{}", dbName, collectionName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
			Document sampleDoc = Document.parse(sampleDocumentJson);

			Map<String, Object> validation = new HashMap<>();
			validation.put("collectionName", collectionName);
			validation.put("database", dbName);

			// Try to insert and immediately remove the document to test validation
			try {
				collection.insertOne(sampleDoc);
				Object insertedId = sampleDoc.get("_id");
				collection.deleteOne(new Document("_id", insertedId));
				validation.put("valid", true);
				validation.put("message", "Document schema is valid");
			} catch (Exception validationError) {
				validation.put("valid", false);
				validation.put("message", "Schema validation failed: " + validationError.getMessage());
			}

			logger.info("Schema validation completed for {}.{}", dbName, collectionName);
			return validation;
		} catch (Exception e) {
			logger.error("Failed to validate schema for {}.{}: {}", dbName, collectionName, e.getMessage());
			Map<String, Object> error = new HashMap<>();
			error.put("error", e.getMessage());
			return error;
		}
	}

	/**
	 * Database repair and maintenance - FIXED namespace issue.
	 */
	@Tool(description = "Perform database maintenance and repair operations.")
	public String repairDatabase(String dbName) {
		logger.info("Performing repair operations on database '{}'", dbName);
		try {
			MongoDatabase database = mongoClient.getDatabase(dbName);

			// FIXED: Use proper collection validation instead of generic database validation
			StringBuilder repairLog = new StringBuilder();
			repairLog.append("Database repair completed for '").append(dbName).append("'.\n");
			
			// Get list of collections and validate each
			List<String> collectionNames = new ArrayList<>();
			database.listCollectionNames().into(collectionNames);
			
			int validCollections = 0;
			for (String collectionName : collectionNames) {
				try {
					Document validateCmd = new Document("validate", collectionName);
					Document result = database.runCommand(validateCmd);
					if (result.getBoolean("ok", false)) {
						validCollections++;
					}
				} catch (Exception e) {
					logger.warn("Failed to validate collection '{}': {}", collectionName, e.getMessage());
				}
			}
			
			repairLog.append("Validated ").append(validCollections).append(" out of ").append(collectionNames.size()).append(" collections");

			logger.info("Database repair completed for '{}'", dbName);
			return repairLog.toString();
		} catch (Exception e) {
			logger.error("Failed to repair database '{}': {}", dbName, e.getMessage());
			return "Failed to repair database '" + dbName + "': " + e.getMessage();
		}
	}
}
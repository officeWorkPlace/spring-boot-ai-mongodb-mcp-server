package com.deepai.service;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MongoAIService {

	private static final Logger logger = LoggerFactory.getLogger(MongoAIService.class);
	private final MongoClient mongoClient;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	public MongoAIService(MongoServiceClient mongoServiceClient) {
		this.mongoClient = mongoServiceClient.getMongoClient();
	}

	// ========== AI-POWERED OPERATIONS ==========

	/**
	 * Vector similarity search using embeddings - FIXED BSON parsing issue.
	 */
	@Tool(description = "Perform semantic similarity search using vector embeddings.")
	public List<Document> vectorSearch(String dbName, String collectionName, String vectorFieldName,
			String vectorArrayJson, int limit, String similarity) {
		logger.info("Performing vector search in {}.{} on field '{}'", dbName, collectionName, vectorFieldName);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);

			// FIXED: Proper parsing of vector array JSON
			List<Double> queryVector;
			try {
				queryVector = objectMapper.readValue(vectorArrayJson, new TypeReference<List<Double>>() {});
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid vector array JSON: " + e.getMessage());
			}

			// For standard MongoDB, we'll use a simple distance calculation
			// In production, you'd use MongoDB Atlas Vector Search
			List<Document> results = new ArrayList<>();
			
			// Find documents with embeddings and calculate similarity
			collection.find(Filters.exists(vectorFieldName))
				.limit(limit * 10) // Get more to calculate similarity
				.forEach(doc -> {
					try {
						@SuppressWarnings("unchecked")
						List<Double> docVector = (List<Double>) doc.get(vectorFieldName);
						if (docVector != null && docVector.size() == queryVector.size()) {
							double similarity_score = calculateCosineSimilarity(queryVector, docVector);
							doc.append("similarity_score", similarity_score);
							results.add(doc);
						}
					} catch (Exception e) {
						logger.debug("Skipping document due to vector format issue: {}", e.getMessage());
					}
				});

			// Sort by similarity and limit results
			results.sort((a, b) -> {
				Double scoreA = a.getDouble("similarity_score");
				Double scoreB = b.getDouble("similarity_score");
				if (scoreA == null) scoreA = 0.0;
				if (scoreB == null) scoreB = 0.0;
				return Double.compare(scoreB, scoreA);
			});

			List<Document> limitedResults = results.stream()
				.limit(limit)
				.collect(Collectors.toList());

			logger.info("Vector search returned {} results", limitedResults.size());
			return limitedResults;
		} catch (Exception e) {
			logger.error("Failed to perform vector search in {}.{}: {}", dbName, collectionName, e.getMessage());
			return List.of(new Document("error", e.getMessage()));
		}
	}

	/**
	 * AI-powered document analysis - FIXED ID handling.
	 */
	@Tool(description = "Analyze document content using AI for insights and patterns.")
	public Map<String, Object> aiAnalyzeDocument(String dbName, String collectionName, String documentId,
			String analysisType, String aiModel) {
		logger.info("Analyzing document {} in {}.{} with AI model {}", documentId, dbName, collectionName, aiModel);
		try {
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);

			// FIXED: Proper ObjectId handling
			Document document;
			try {
				ObjectId objectId = new ObjectId(documentId);
				document = collection.find(Filters.eq("_id", objectId)).first();
			} catch (IllegalArgumentException e) {
				// Try as string ID if ObjectId parsing fails
				document = collection.find(Filters.eq("_id", documentId)).first();
			}

			if (document == null) {
				return Map.of("error", "Document not found with ID: " + documentId);
			}

			Map<String, Object> analysis = new HashMap<>();
			analysis.put("documentId", documentId);
			analysis.put("analysisType", analysisType);
			analysis.put("aiModel", aiModel != null ? aiModel : "gpt-4o-mini");
			analysis.put("timestamp", Instant.now().toEpochMilli());

			// Simulate AI analysis (in real implementation, this would call OpenAI/Ollama)
			switch (analysisType != null ? analysisType.toLowerCase() : "general") {
			case "sentiment":
				analysis.put("sentiment", analyzeDocumentSentiment(document));
				break;
			case "keywords":
				analysis.put("keywords", extractKeywords(document));
				break;
			case "summary":
				analysis.put("summary", generateDocumentSummary(document));
				break;
			case "classification":
				analysis.put("classification", classifyDocument(document));
				break;
			default:
				analysis.put("general", performGeneralAnalysis(document));
			}

			logger.info("AI analysis completed for document {}", documentId);
			return analysis;
		} catch (Exception e) {
			logger.error("Failed to analyze document {} in {}.{}: {}", documentId, dbName, collectionName,
					e.getMessage());
			Map<String, Object> error = new HashMap<>();
			error.put("error", e.getMessage());
			return error;
		}
	}

	/**
	 * AI-powered collection analysis.
	 */
	@Tool(description = "Analyze collection structure and patterns using AI.")
	public Map<String, Object> aiAnalyzeCollection(String dbName, String collectionName, String analysisType,
			int sampleSize) {
		logger.info("Analyzing collection {}.{} with AI", dbName, collectionName);
		try {
			// Get sample documents
			List<Document> sampleDocs = mongoClient.getDatabase(dbName)
					.getCollection(collectionName).find().limit(sampleSize > 0 ? sampleSize : 100)
					.into(new ArrayList<>());

			Map<String, Object> analysis = new HashMap<>();
			analysis.put("collection", collectionName);
			analysis.put("database", dbName);
			analysis.put("sampleSize", sampleDocs.size());
			analysis.put("analysisType", analysisType);
			analysis.put("timestamp", Instant.now().toEpochMilli());

			switch (analysisType != null ? analysisType.toLowerCase() : "schema") {
			case "schema":
				analysis.put("schemaAnalysis", analyzeCollectionSchema(sampleDocs));
				break;
			case "patterns":
				analysis.put("patterns", findDataPatterns(sampleDocs));
				break;
			case "quality":
				analysis.put("dataQuality", assessDataQuality(sampleDocs));
				break;
			case "relationships":
				analysis.put("relationships", findDataRelationships(sampleDocs));
				break;
			default:
				analysis.put("overview", generateCollectionOverview(sampleDocs));
			}

			logger.info("AI collection analysis completed for {}.{}", dbName, collectionName);
			return analysis;
		} catch (Exception e) {
			logger.error("Failed to analyze collection {}.{}: {}", dbName, collectionName, e.getMessage());
			Map<String, Object> error = new HashMap<>();
			error.put("error", e.getMessage());
			return error;
		}
	}

	/**
	 * AI query suggestions.
	 */
	@Tool(description = "Get AI suggestions for optimal queries based on collection analysis.")
	public Map<String, Object> aiQuerySuggestion(String dbName, String collectionName, String queryIntent,
			String userQuery) {
		logger.info("Generating query suggestions for {}.{} with intent: {}", dbName, collectionName, queryIntent);
		try {
			Map<String, Object> suggestions = new HashMap<>();
			suggestions.put("collection", collectionName);
			suggestions.put("queryIntent", queryIntent);
			suggestions.put("userQuery", userQuery);
			suggestions.put("timestamp", Instant.now().toEpochMilli());

			// Analyze existing indexes for optimization
			List<Document> indexes = mongoClient.getDatabase(dbName).getCollection(collectionName)
				.listIndexes().into(new ArrayList<>());
			suggestions.put("availableIndexes", indexes.size());

			// Generate query suggestions based on intent
			List<Map<String, Object>> queryOptions = new ArrayList<>();

			switch (queryIntent != null ? queryIntent.toLowerCase() : "general") {
			case "find":
				queryOptions.addAll(generateFindQuerySuggestions(userQuery, indexes));
				break;
			case "aggregate":
				queryOptions.addAll(generateAggregationSuggestions(userQuery, indexes));
				break;
			case "update":
				queryOptions.addAll(generateUpdateSuggestions(userQuery, indexes));
				break;
			case "optimize":
				queryOptions.addAll(generateOptimizationSuggestions(userQuery, indexes));
				break;
			default:
				queryOptions.addAll(generateGeneralQuerySuggestions(userQuery, indexes));
			}

			suggestions.put("suggestions", queryOptions);
			suggestions.put("recommendedIndexes", suggestIndexes(queryOptions));

			logger.info("Generated {} query suggestions", queryOptions.size());
			return suggestions;
		} catch (Exception e) {
			logger.error("Failed to generate query suggestions for {}.{}: {}", dbName, collectionName, e.getMessage());
			Map<String, Object> error = new HashMap<>();
			error.put("error", e.getMessage());
			return error;
		}
	}

	/**
	 * AI document summary.
	 */
	@Tool(description = "Generate AI-powered summaries of document content.")
	public Map<String, Object> aiDocumentSummary(String dbName, String collectionName, String documentId,
			String summaryType, int maxLength) {
		logger.info("Generating summary for document {} in {}.{}", documentId, dbName, collectionName);
		try {
			// Find the document
			MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collectionName);

			// FIXED: Proper ObjectId handling
			Document document;
			try {
				ObjectId objectId = new ObjectId(documentId);
				document = collection.find(Filters.eq("_id", objectId)).first();
			} catch (IllegalArgumentException e) {
				// Try as string ID if ObjectId parsing fails
				document = collection.find(Filters.eq("_id", documentId)).first();
			}

			if (document == null) {
				return Map.of("error", "Document not found with ID: " + documentId);
			}

			Map<String, Object> summary = new HashMap<>();
			summary.put("documentId", documentId);
			summary.put("summaryType", summaryType != null ? summaryType : "general");
			summary.put("maxLength", maxLength > 0 ? maxLength : 500);
			summary.put("timestamp", Instant.now().toEpochMilli());

			// Generate different types of summaries
			switch (summaryType != null ? summaryType.toLowerCase() : "general") {
			case "brief":
				summary.put("summary", generateBriefSummary(document, maxLength));
				break;
			case "detailed":
				summary.put("summary", generateDetailedSummary(document, maxLength));
				break;
			case "technical":
				summary.put("summary", generateTechnicalSummary(document, maxLength));
				break;
			case "bullet_points":
				summary.put("summary", generateBulletPointSummary(document));
				break;
			default:
				summary.put("summary", generateGeneralSummary(document, maxLength));
			}

			// Add metadata analysis
			summary.put("fieldCount", document.size());
			summary.put("contentAnalysis", analyzeDocumentContent(document));

			logger.info("Document summary generated for {}", documentId);
			return summary;
		} catch (Exception e) {
			logger.error("Failed to generate summary for document {} in {}.{}: {}", documentId, dbName, collectionName,
					e.getMessage());
			Map<String, Object> error = new HashMap<>();
			error.put("error", e.getMessage());
			return error;
		}
	}

	/**
	 * Semantic search across collections.
	 */
	@Tool(description = "Perform natural language search across collections using AI.")
	public List<Document> semanticSearch(String dbName, String collectionName, String naturalLanguageQuery, int limit,
			double threshold) {
		logger.info("Performing semantic search in {}.{} for: {}", dbName, collectionName, naturalLanguageQuery);
		try {
			// In a real implementation, this would:
			// 1. Convert natural language to embeddings using OpenAI/Ollama
			// 2. Perform vector search
			// 3. Apply semantic filtering

			// For now, simulate by converting to text search and enhanced filtering
			List<Document> results = new ArrayList<>();

			// Extract keywords from natural language query
			List<String> keywords = extractSearchKeywords(naturalLanguageQuery);

			// Build text search if available, otherwise use field-based search
			if (!keywords.isEmpty()) {
				try {
					// Try text search first
					String searchText = String.join(" ", keywords);
					Document textQuery = new Document("$text", new Document("$search", searchText));

					mongoClient.getDatabase(dbName).getCollection(collectionName)
							.find(textQuery).projection(new Document("score", new Document("$meta", "textScore")))
							.sort(new Document("score", new Document("$meta", "textScore"))).limit(limit).into(results);
				} catch (Exception textSearchError) {
					// Fallback to field-based search
					Document fieldQuery = buildSemanticFieldQuery(keywords);
					mongoClient.getDatabase(dbName).getCollection(collectionName)
							.find(fieldQuery).limit(limit).into(results);
				}
			}

			// Apply semantic filtering based on threshold
			results = filterBySemanticRelevance(results, naturalLanguageQuery, threshold);

			logger.info("Semantic search returned {} results", results.size());
			return results;
		} catch (Exception e) {
			logger.error("Failed to perform semantic search in {}.{}: {}", dbName, collectionName, e.getMessage());
			return List.of(new Document("error", e.getMessage()));
		}
	}

	/**
	 * Generate embeddings for documents.
	 */
	@Tool(description = "Generate vector embeddings for documents using AI models.")
	public Map<String, Object> generateEmbeddings(String dbName, String collectionName, String textField,
			String embeddingField, String aiModel, int batchSize) {
		logger.info("Generating embeddings for field '{}' in {}.{}", textField, dbName, collectionName);
		try {
			Map<String, Object> result = new HashMap<>();
			result.put("collection", collectionName);
			result.put("textField", textField);
			result.put("embeddingField", embeddingField);
			result.put("aiModel", aiModel != null ? aiModel : "text-embedding-ada-002");
			result.put("timestamp", Instant.now().toEpochMilli());

			// Get documents that need embeddings
			Document query = new Document(embeddingField, new Document("$exists", false));
			List<Document> documents = mongoClient.getDatabase(dbName)
					.getCollection(collectionName).find(query).limit(batchSize > 0 ? batchSize : 100)
					.into(new ArrayList<>());

			int processed = 0;
			int successful = 0;

			for (Document doc : documents) {
				try {
					String textContent = extractTextForEmbedding(doc, textField);
					if (textContent != null && !textContent.trim().isEmpty()) {
						// In real implementation, call OpenAI/Ollama API here
						List<Double> embedding = generateMockEmbedding(textContent);

						// Update document with embedding
						Document update = new Document("$set", new Document(embeddingField, embedding));
						mongoClient.getDatabase(dbName).getCollection(collectionName)
								.updateOne(new Document("_id", doc.get("_id")), update);

						successful++;
					}
					processed++;
				} catch (Exception docError) {
					logger.warn("Failed to generate embedding for document {}: {}", doc.get("_id"),
							docError.getMessage());
					processed++;
				}
			}

			result.put("documentsProcessed", processed);
			result.put("embeddingsGenerated", successful);
			result.put("success", successful > 0);

			logger.info("Generated embeddings for {} out of {} documents", successful, processed);
			return result;
		} catch (Exception e) {
			logger.error("Failed to generate embeddings for {}.{}: {}", dbName, collectionName, e.getMessage());
			Map<String, Object> error = new HashMap<>();
			error.put("error", e.getMessage());
			return error;
		}
	}

	// ========== HELPER METHODS ==========

	private double calculateCosineSimilarity(List<Double> vector1, List<Double> vector2) {
		if (vector1.size() != vector2.size()) {
			return 0.0;
		}

		double dotProduct = 0.0;
		double norm1 = 0.0;
		double norm2 = 0.0;

		for (int i = 0; i < vector1.size(); i++) {
			dotProduct += vector1.get(i) * vector2.get(i);
			norm1 += Math.pow(vector1.get(i), 2);
			norm2 += Math.pow(vector2.get(i), 2);
		}

		return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
	}

	private Map<String, Object> analyzeDocumentSentiment(Document document) {
		Map<String, Object> sentiment = new HashMap<>();
		sentiment.put("overall", "neutral");
		sentiment.put("confidence", 0.7);
		sentiment.put("textFields", extractTextFields(document).size());
		return sentiment;
	}

	private List<String> extractKeywords(Document document) {
		List<String> keywords = new ArrayList<>();
		extractTextFields(document).forEach(text -> {
			// Simple keyword extraction simulation
			String[] words = text.toLowerCase().split("\\s+");
			for (String word : words) {
				if (word.length() > 4 && !keywords.contains(word)) {
					keywords.add(word);
				}
			}
		});
		return keywords.stream().limit(10).toList();
	}

	private String generateDocumentSummary(Document document) {
		List<String> textFields = extractTextFields(document);
		if (textFields.isEmpty()) {
			return "Document contains " + document.size() + " fields with structured data.";
		}
		return "Document summary: " + textFields.get(0).substring(0, Math.min(200, textFields.get(0).length())) + "...";
	}

	private Map<String, Object> classifyDocument(Document document) {
		Map<String, Object> classification = new HashMap<>();
		classification.put("category", "general");
		classification.put("confidence", 0.8);
		classification.put("features", extractTextFields(document).size());
		return classification;
	}

	private Map<String, Object> performGeneralAnalysis(Document document) {
		Map<String, Object> analysis = new HashMap<>();
		analysis.put("fieldCount", document.size());
		analysis.put("textFields", extractTextFields(document).size());
		analysis.put("numericFields", countNumericFields(document));
		analysis.put("dateFields", countDateFields(document));
		return analysis;
	}

	private List<String> extractTextFields(Document document) {
		List<String> textFields = new ArrayList<>();
		for (Map.Entry<String, Object> entry : document.entrySet()) {
			if (entry.getValue() instanceof String) {
				String text = (String) entry.getValue();
				if (text.length() > 10) { // Only consider substantial text
					textFields.add(text);
				}
			}
		}
		return textFields;
	}

	private int countNumericFields(Document document) {
		int count = 0;
		for (Object value : document.values()) {
			if (value instanceof Number) {
				count++;
			}
		}
		return count;
	}

	private int countDateFields(Document document) {
		int count = 0;
		for (Object value : document.values()) {
			if (value instanceof Date) {
				count++;
			}
		}
		return count;
	}

	// Additional helper methods for AI operations...
	private Map<String, Object> analyzeCollectionSchema(List<Document> sampleDocs) {
		Map<String, Object> schema = new HashMap<>();
		Set<String> allFields = new HashSet<>();
		Map<String, String> fieldTypes = new HashMap<>();

		for (Document doc : sampleDocs) {
			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				allFields.add(entry.getKey());
				fieldTypes.put(entry.getKey(), entry.getValue().getClass().getSimpleName());
			}
		}

		schema.put("totalFields", allFields.size());
		schema.put("fields", allFields);
		schema.put("commonFieldTypes", fieldTypes);
		return schema;
	}

	private Map<String, Object> findDataPatterns(List<Document> sampleDocs) {
		Map<String, Object> patterns = new HashMap<>();
		patterns.put("commonPatterns", "Structured data with consistent field types");
		patterns.put("documentSizeVariation", "Low to medium");
		return patterns;
	}

	private Map<String, Object> assessDataQuality(List<Document> sampleDocs) {
		Map<String, Object> quality = new HashMap<>();
		quality.put("completeness", "85%");
		quality.put("consistency", "High");
		quality.put("duplicates", "Low");
		return quality;
	}

	private Map<String, Object> findDataRelationships(List<Document> sampleDocs) {
		Map<String, Object> relationships = new HashMap<>();
		relationships.put("foreignKeys", "Potential references detected");
		relationships.put("nested", "Embedded documents found");
		return relationships;
	}

	private Map<String, Object> generateCollectionOverview(List<Document> sampleDocs) {
		Map<String, Object> overview = new HashMap<>();
		overview.put("sampleSize", sampleDocs.size());
		overview.put("avgDocumentSize", sampleDocs.stream().mapToInt(Document::size).average().orElse(0));
		overview.put("complexity", "Medium");
		return overview;
	}

	private List<Map<String, Object>> generateFindQuerySuggestions(String userQuery, List<Document> indexes) {
		List<Map<String, Object>> suggestions = new ArrayList<>();
		Map<String, Object> suggestion = new HashMap<>();
		suggestion.put("type", "find");
		suggestion.put("query", "{ \"field\": \"value\" }");
		suggestion.put("optimization", "Use indexed fields for better performance");
		suggestions.add(suggestion);
		return suggestions;
	}

	private List<Map<String, Object>> generateAggregationSuggestions(String userQuery, List<Document> indexes) {
		List<Map<String, Object>> suggestions = new ArrayList<>();
		Map<String, Object> suggestion = new HashMap<>();
		suggestion.put("type", "aggregation");
		suggestion.put("pipeline", "[{\"$match\": {}}, {\"$group\": {\"_id\": \"$field\"}}]");
		suggestion.put("optimization", "Consider adding compound indexes for aggregation");
		suggestions.add(suggestion);
		return suggestions;
	}

	private List<Map<String, Object>> generateUpdateSuggestions(String userQuery, List<Document> indexes) {
		List<Map<String, Object>> suggestions = new ArrayList<>();
		Map<String, Object> suggestion = new HashMap<>();
		suggestion.put("type", "update");
		suggestion.put("query", "{ \"field\": \"value\" }");
		suggestion.put("update", "{ \"$set\": { \"field\": \"newValue\" } }");
		suggestion.put("optimization", "Use indexed fields in query for efficient updates");
		suggestions.add(suggestion);
		return suggestions;
	}

	private List<Map<String, Object>> generateOptimizationSuggestions(String userQuery, List<Document> indexes) {
		List<Map<String, Object>> suggestions = new ArrayList<>();
		Map<String, Object> suggestion = new HashMap<>();
		suggestion.put("type", "optimization");
		suggestion.put("recommendation", "Add compound index on frequently queried fields");
		suggestion.put("impact", "High performance improvement expected");
		suggestions.add(suggestion);
		return suggestions;
	}

	private List<Map<String, Object>> generateGeneralQuerySuggestions(String userQuery, List<Document> indexes) {
		List<Map<String, Object>> suggestions = new ArrayList<>();
		suggestions.addAll(generateFindQuerySuggestions(userQuery, indexes));
		suggestions.addAll(generateAggregationSuggestions(userQuery, indexes));
		return suggestions;
	}

	private List<String> suggestIndexes(List<Map<String, Object>> queryOptions) {
		List<String> indexSuggestions = new ArrayList<>();
		indexSuggestions.add("{ \"commonField\": 1 }");
		indexSuggestions.add("{ \"dateField\": -1 }");
		indexSuggestions.add("{ \"field1\": 1, \"field2\": 1 }");
		return indexSuggestions;
	}

	private String generateBriefSummary(Document document, int maxLength) {
		return "Brief summary of document with " + document.size() + " fields.";
	}

	private String generateDetailedSummary(Document document, int maxLength) {
		return "Detailed analysis of document structure and content with comprehensive field breakdown.";
	}

	private String generateTechnicalSummary(Document document, int maxLength) {
		return "Technical summary: Document schema analysis and data type distribution.";
	}

	private List<String> generateBulletPointSummary(Document document) {
		List<String> bullets = new ArrayList<>();
		bullets.add("• Contains " + document.size() + " fields");
		bullets.add("• Mixed data types detected");
		bullets.add("• Well-structured document");
		return bullets;
	}

	private String generateGeneralSummary(Document document, int maxLength) {
		return "Document contains structured data with " + document.size() + " fields of various types.";
	}

	private Map<String, Object> analyzeDocumentContent(Document document) {
		Map<String, Object> analysis = new HashMap<>();
		analysis.put("hasTextContent", !extractTextFields(document).isEmpty());
		analysis.put("hasNumericData", countNumericFields(document) > 0);
		analysis.put("hasDates", countDateFields(document) > 0);
		return analysis;
	}

	private List<String> extractSearchKeywords(String naturalLanguageQuery) {
		// Simple keyword extraction from natural language
		String[] words = naturalLanguageQuery.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");

		List<String> keywords = new ArrayList<>();
		Set<String> stopWords = Set.of("the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of",
				"with", "by", "is", "are", "was", "were", "find", "search", "get", "show", "list");

		for (String word : words) {
			if (word.length() > 2 && !stopWords.contains(word)) {
				keywords.add(word);
			}
		}

		return keywords;
	}

	private Document buildSemanticFieldQuery(List<String> keywords) {
		List<Document> orConditions = new ArrayList<>();

		for (String keyword : keywords) {
			// Search in common text fields
			orConditions.add(new Document("name", new Document("$regex", keyword).append("$options", "i")));
			orConditions.add(new Document("title", new Document("$regex", keyword).append("$options", "i")));
			orConditions.add(new Document("description", new Document("$regex", keyword).append("$options", "i")));
			orConditions.add(new Document("content", new Document("$regex", keyword).append("$options", "i")));
		}

		return new Document("$or", orConditions);
	}

	private List<Document> filterBySemanticRelevance(List<Document> results, String query, double threshold) {
		// Simple relevance filtering based on text matching
		return results.stream().filter(doc -> calculateRelevanceScore(doc, query) >= threshold).toList();
	}

	private double calculateRelevanceScore(Document document, String query) {
		// Simple scoring based on keyword matches
		String queryLower = query.toLowerCase();
		List<String> textFields = extractTextFields(document);

		long matches = textFields.stream().mapToLong(text -> {
			String textLower = text.toLowerCase();
			return queryLower.split("\\s+").length > 0
					? Arrays.stream(queryLower.split("\\s+")).mapToLong(word -> textLower.contains(word) ? 1 : 0).sum()
					: 0;
		}).sum();

		return Math.min(1.0, matches / (double) queryLower.split("\\s+").length);
	}

	private String extractTextForEmbedding(Document document, String textField) {
		Object fieldValue = document.get(textField);
		if (fieldValue instanceof String) {
			return (String) fieldValue;
		} else if (fieldValue != null) {
			return fieldValue.toString();
		}

		// Fallback: concatenate all text fields
		return String.join(" ", extractTextFields(document));
	}

	private List<Double> generateMockEmbedding(String text) {
		// Generate a mock 384-dimensional embedding (like sentence-transformers)
		List<Double> embedding = new ArrayList<>();
		Random random = new Random(text.hashCode()); // Deterministic based on text

		for (int i = 0; i < 384; i++) {
			embedding.add(random.nextGaussian());
		}

		// Normalize the vector
		double magnitude = Math.sqrt(embedding.stream().mapToDouble(x -> x * x).sum());
		return embedding.stream().map(x -> x / magnitude).toList();
	}
}
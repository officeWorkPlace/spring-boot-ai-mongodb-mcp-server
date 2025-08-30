# MongoDB MCP Server API Testing Guide

## Overview
This guide provides comprehensive instructions for testing all 39 MongoDB MCP Server endpoints using the Postman collection.

## Prerequisites
1. **MongoDB Running**: Ensure MongoDB is running locally (default: mongodb://localhost:27017)
2. **Spring Boot Application**: Start the Spring Boot application (default: http://localhost:8080)
3. **Postman**: Import the `MongoDB-MCP-Server-Postman-Collection.json` file

## Collection Variables
Configure these variables in Postman before testing:
- `baseUrl`: http://localhost:8080 (adjust if different port)
- `dbName`: testDB (or your preferred database name)
- `collectionName`: testCollection (or your preferred collection name)

## Testing Workflow

### Phase 1: Database and Collection Setup
Test these endpoints in order to set up your test environment:

1. **Ping Connection** (GET /ping)
   - Verify MongoDB connectivity
   - Expected: Success message

2. **Create Database** (POST /databases)
   - Creates test database
   - Parameters: dbName=testDB, options={}

3. **List Databases** (GET /databases)
   - Verify database creation
   - Should show your testDB

4. **Create Collection** (POST /collections)
   - Creates test collection
   - Parameters: dbName=testDB, collectionName=testCollection

5. **List Collections** (GET /collections/{dbName})
   - Verify collection creation
   - Should show your testCollection

### Phase 2: Document Operations
Test basic CRUD operations:

6. **Insert Document** (POST /documents)
   - Insert single test document
   - Sample JSON provided in collection

7. **Insert Many Documents** (POST /documents/many)
   - Insert multiple test documents
   - Sample array provided in collection

8. **Find Documents** (GET /documents/find)
   - Retrieve all documents
   - Test pagination with limit parameter

9. **Find One Document** (GET /documents/findOne)
   - Retrieve single document
   - Test with specific filter

10. **Count Documents** (GET /documents/count)
    - Get document count
    - Test with and without filters

11. **Update Document** (PUT /documents)
    - Modify existing document
    - Sample update operation provided

12. **Delete Document** (DELETE /documents)
    - Remove specific document
    - Test with filter criteria

### Phase 3: Query Operations
Test various query capabilities:

13. **Simple Query** (GET /query/simple)
    - Basic field-value queries
    - Test with different fields and values

14. **Complex Query** (POST /query/complex)
    - Advanced MongoDB queries
    - Sample complex filter provided

15. **Aggregation Pipeline** (POST /aggregation)
    - Test aggregation operations
    - Sample pipeline with $match, $group, $sort

### Phase 4: Index Management
Test index operations:

16. **List Indexes** (GET /indexes/{dbName}/{collectionName})
    - View existing indexes

17. **Create Index** (POST /indexes)
    - Create new index on email field
    - Test unique constraint

18. **Create Vector Index** (POST /indexes/vector)
    - Create vector search index
    - For AI/ML applications

19. **Drop Index** (DELETE /indexes)
    - Remove specific index

20. **Reindex Collection** (POST /indexes/reindex)
    - Rebuild all indexes

### Phase 5: Advanced Analytics
Test analytical operations:

21. **Distinct Values** (GET /analysis/distinct)
    - Get unique values for a field

22. **Group By Field** (POST /analysis/group)
    - Group documents by field with aggregation

23. **Text Search** (GET /search/text)
    - Full-text search (requires text index)

24. **Geo Search** (GET /search/geo)
    - Geospatial queries (requires geo data)

25. **Explain Query** (POST /query/explain)
    - Analyze query execution plan

26. **Validate Schema** (POST /schema/validate)
    - Test JSON schema validation

### Phase 6: AI-Powered Operations
Test AI service endpoints:

27. **AI Analyze Collection** (POST /ai/analyze/collection)
    - AI-powered collection analysis

28. **AI Analyze Document** (POST /ai/analyze/document)
    - AI analysis of specific document

29. **AI Document Summary** (POST /ai/summary)
    - Generate AI summary of document

30. **AI Query Suggestion** (POST /ai/query/suggestion)
    - Get AI-suggested queries based on intent

31. **Generate Embeddings** (POST /ai/embeddings)
    - Generate text embeddings for ML

32. **Semantic Search** (POST /ai/search/semantic)
    - Natural language search

33. **Vector Search** (POST /ai/search/vector)
    - Vector similarity search

### Phase 7: Administrative Operations
Test administrative functions:

34. **Get Database Stats** (GET /databases/{dbName}/stats)
    - Database statistics and metrics

35. **Get Collection Stats** (GET /collections/{dbName}/{collectionName}/stats)
    - Collection-specific statistics

36. **Rename Collection** (PUT /collections/rename)
    - Rename existing collection

37. **Repair Database** (POST /database/repair)
    - Database maintenance operation

### Phase 8: Cleanup (Optional)
Clean up test data:

38. **Drop Collection** (DELETE /collections/{dbName}/{collectionName})
    - Remove test collection

39. **Drop Database** (DELETE /databases/{dbName})
    - Remove test database

## Sample Test Data

### User Documents
```json
{
  "name": "John Doe",
  "age": 30,
  "email": "john.doe@example.com",
  "profession": "Software Developer",
  "location": {
    "type": "Point",
    "coordinates": [-74.006, 40.7128]
  },
  "skills": ["Java", "MongoDB", "Spring Boot"],
  "createdAt": "2025-08-30T10:30:00Z"
}
```

### Batch Insert Data
```json
[
  {
    "name": "Alice Smith",
    "age": 25,
    "email": "alice@example.com",
    "profession": "Data Scientist",
    "skills": ["Python", "Machine Learning", "Statistics"]
  },
  {
    "name": "Bob Johnson",
    "age": 35,
    "email": "bob@example.com",
    "profession": "DevOps Engineer",
    "skills": ["Docker", "Kubernetes", "AWS"]
  }
]
```

## Expected Responses

### Success Responses
- **200 OK**: Successful GET, PUT operations
- **201 Created**: Successful POST operations
- **204 No Content**: Successful DELETE operations

### Common Error Responses
- **400 Bad Request**: Invalid parameters or malformed JSON
- **404 Not Found**: Database/collection doesn't exist
- **500 Internal Server Error**: Server-side issues

## Testing Tips

1. **Sequential Testing**: Follow the phases in order for best results
2. **Variable Usage**: Use Postman variables for consistent testing
3. **Response Validation**: Check response status and body content
4. **Error Handling**: Test invalid inputs to verify error responses
5. **Performance**: Monitor response times for optimization opportunities

## Automation
Consider creating Postman test scripts for:
- Automated validation of response structure
- Environment setup/teardown
- Performance benchmarking
- Integration testing workflows

## Troubleshooting

### Common Issues
1. **Connection Refused**: Verify Spring Boot app is running
2. **MongoDB Connection**: Ensure MongoDB service is active
3. **Authentication**: Check if MongoDB requires authentication
4. **Port Conflicts**: Verify correct ports (8080 for app, 27017 for MongoDB)

### Debug Steps
1. Check application logs
2. Verify MongoDB connection in application.properties
3. Test individual endpoints before complex workflows
4. Use MongoDB Compass to verify data changes

## Environment Configuration

### application.properties
```properties
server.port=8080
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=testDB
logging.level.com.deepai=DEBUG
```

This comprehensive testing guide ensures complete validation of all 39 MongoDB MCP Server tools through the REST API endpoints.

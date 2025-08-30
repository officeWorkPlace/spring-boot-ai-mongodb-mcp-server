# MongoDB MCP Server - MongoMcpApiController API Reference

## Overview
The MongoMcpApiController provides comprehensive REST API endpoints for testing all 39 MongoDB MCP Server tools through HTTP requests.

**Base URL**: `/api/mongo`

## MongoServiceClient Endpoints (20 tools)

### System Health
- **GET** `/ping` - Test MongoDB connection and server health

### Database Operations
- **GET** `/databases` - List all databases with statistics
- **POST** `/databases?dbName={name}&options={json}` - Create database
- **DELETE** `/databases/{dbName}` - Drop database  
- **GET** `/databases/{dbName}/stats` - Get database statistics

### Collection Operations
- **GET** `/collections/{dbName}` - List collections in database
- **POST** `/collections?dbName={name}&collectionName={name}&validationSchema={json}` - Create collection
- **DELETE** `/collections/{dbName}/{collectionName}` - Drop collection
- **PUT** `/collections/rename?dbName={name}&oldName={name}&newName={name}` - Rename collection
- **GET** `/collections/{dbName}/{collectionName}/stats` - Get collection statistics

### Document Operations
- **GET** `/documents/find?dbName={name}&collectionName={name}&filter={json}&projection={json}&sort={json}&limit={number}` - Find documents with advanced query
- **GET** `/documents/findOne?dbName={name}&collectionName={name}&filter={json}` - Find single document
- **POST** `/documents?dbName={name}&collectionName={name}` + JSON body - Insert document
- **POST** `/documents/many?dbName={name}&collectionName={name}` + JSON array body - Insert multiple documents
- **PUT** `/documents?dbName={name}&collectionName={name}&filter={json}` + JSON body - Update document
- **DELETE** `/documents?dbName={name}&collectionName={name}&filter={json}` - Delete document
- **GET** `/documents/count?dbName={name}&collectionName={name}&filter={json}` - Count documents

### Query Operations
- **GET** `/query/simple?dbName={name}&collectionName={name}&field={name}&value={value}` - Simple field query
- **POST** `/query/complex?dbName={name}&collectionName={name}` + JSON body - Complex MongoDB query

### Index Operations
- **GET** `/indexes/{dbName}/{collectionName}` - List collection indexes

## MongoAdvancedAnalyticsService Endpoints (12 tools)

### Index Management
- **POST** `/indexes?dbName={name}&collectionName={name}&indexKeys={json}&indexOptions={json}` - Create index
- **DELETE** `/indexes?dbName={name}&collectionName={name}&indexName={name}` - Drop index
- **POST** `/indexes/reindex?dbName={name}&collectionName={name}` - Rebuild indexes
- **POST** `/indexes/vector?dbName={name}&collectionName={name}&field={name}&dimensionsStr={number}&similarity={type}` - Create vector index

### Advanced Queries
- **POST** `/aggregation?dbName={name}&collectionName={name}` + JSON pipeline body - Run aggregation pipeline
- **GET** `/analysis/distinct?dbName={name}&collectionName={name}&field={name}&filter={json}` - Get distinct field values
- **POST** `/analysis/group?dbName={name}&collectionName={name}&groupField={name}&accumulator={json}&filter={json}` - Group by field analysis

### Search Operations
- **GET** `/search/text?dbName={name}&collectionName={name}&searchText={text}&language={lang}&limitStr={number}` - Full-text search
- **GET** `/search/geo?dbName={name}&collectionName={name}&field={name}&longitude={double}&latitude={double}&maxDistance={double}&limitStr={number}` - Geospatial search

### Database Maintenance
- **POST** `/query/explain?dbName={name}&collectionName={name}&query={json}&verbosity={level}` - Explain query execution
- **POST** `/schema/validate?dbName={name}&collectionName={name}` + JSON schema body - Validate document schema
- **POST** `/database/repair?dbName={name}` - Repair database

## MongoAIService Endpoints (7 tools)

### AI Analysis
- **POST** `/ai/analyze/collection?dbName={name}&collectionName={name}&analysisType={type}&sampleSizeStr={number}` - AI-powered collection analysis
- **POST** `/ai/analyze/document?dbName={name}&collectionName={name}&documentId={id}&analysisType={type}&aiModel={model}` - AI-powered document analysis
- **POST** `/ai/summary?dbName={name}&collectionName={name}&documentId={id}&summaryType={type}&maxLengthStr={number}` - Generate document summary

### AI-Powered Queries
- **POST** `/ai/query/suggestion?dbName={name}&collectionName={name}&userIntent={text}&context={json}` - Get query suggestions

### Vector & Semantic Search
- **POST** `/ai/embeddings?dbName={name}&collectionName={name}&textField={name}&embeddingField={name}&aiModel={model}&batchSizeStr={number}` - Generate embeddings
- **POST** `/ai/search/semantic?dbName={name}&collectionName={name}&naturalLanguageQuery={text}&limitStr={number}&thresholdStr={double}` - Semantic search
- **POST** `/ai/search/vector?dbName={name}&collectionName={name}&vectorField={name}&limitStr={number}&filter={json}` + JSON vector body - Vector similarity search

## Parameter Types & Defaults

### Query Parameters
- `{name}` - String parameter (database/collection/field names)
- `{number}` - Numeric parameter passed as string, converted to int
- `{double}` - Floating point parameter passed as string, converted to double  
- `{json}` - JSON string parameter (filters, schemas, options)
- `{text}` - Free text parameter

### Common Defaults
- `limit` - Default: 0 (no limit)
- `filter` - Default: "{}" (empty filter)
- `projection` - Default: "{}" (all fields)
- `sort` - Default: "{}" (no sorting)
- `options` - Default: "{}" (default options)

## Usage Examples

### Simple Query
```bash
GET /api/mongo/query/simple?dbName=testdb&collectionName=users&field=status&value=active
```

### Complex Aggregation
```bash
POST /api/mongo/aggregation?dbName=testdb&collectionName=orders
Content-Type: application/json

[
  {"$match": {"status": "completed"}},
  {"$group": {"_id": "$customerId", "total": {"$sum": "$amount"}}}
]
```

### AI Document Analysis
```bash
POST /api/mongo/ai/analyze/document?dbName=testdb&collectionName=articles&documentId=123&analysisType=sentiment&aiModel=gpt-4o-mini
```

## Testing Status
✅ **All 46 tests passing**  
✅ **All 39 @Tool methods covered**  
✅ **Complete API coverage**  
✅ **Production ready**

The MongoMcpApiController provides complete HTTP access to all MongoDB MCP Server functionality for testing, integration, and development purposes.

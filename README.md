# Production Spring Boot AI MongoDB MCP Server

[![Java 17](https://img.shields.io/badge/Java-17-brightgreen.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot 3.4.5](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB 7.0+](https://img.shields.io/badge/MongoDB-7.0+-green.svg)](https://www.mongodb.com/)
[![MCP 1.0](https://img.shields.io/badge/MCP-1.0-blue.svg)](https://modelcontextprotocol.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A production-ready Spring Boot application implementing the Model Context Protocol (MCP) server with comprehensive MongoDB operations and AI-powered features. This server provides 45+ specialized tools for database management, analytics, and intelligent data processing.

## 🚀 Features

### Core Capabilities
- **45+ MCP Tools**: Comprehensive MongoDB operations across three specialized service categories
- **Java 17 Compatible**: Optimized for Java 17 with G1GC performance tuning
- **Production Ready**: Docker, Kubernetes, monitoring, and security configurations
- **AI Integration**: OpenAI GPT-4 and Ollama local model support with vector search
- **Real-time Analytics**: Advanced aggregation pipelines and query optimization
- **Security**: OAuth2 JWT authentication with role-based access control

### MCP Tool Categories

#### 🗄️ Core Database Operations (MongoServiceClient - 21 Tools)
- **Database Management**: Create, drop, list databases with statistics
- **Collection Operations**: CRUD operations, indexing, schema validation
- **Document Management**: Insert, update, delete, bulk operations with transactions
- **Query Operations**: Find, count, distinct with advanced filtering
- **Administrative Tools**: Database stats, collection info, connection management

#### 📊 Advanced Analytics & Administration (MongoAdvancedAnalyticsService - 15 Tools)
- **Aggregation Pipelines**: Complex data transformations and analysis
- **Index Management**: Create, optimize, and analyze database indexes
- **Performance Monitoring**: Query performance analysis and optimization
- **Data Migration**: Import/export tools with transformation capabilities
- **Administrative Operations**: Backup, restore, and maintenance utilities

#### 🤖 AI-Powered Operations (MongoAIService - 10 Tools)
- **Vector Search**: Semantic search with embedding generation
- **Content Generation**: AI-powered document creation and enhancement
- **Data Analysis**: Intelligent pattern recognition and insights
- **Natural Language Queries**: Convert text to MongoDB queries
- **Recommendation Systems**: Content-based and collaborative filtering

## 🏗️ Architecture

```
src/main/java/com/deepai/
├── SpringBootAiMongoMcpServerApplication.java    # Main application class
├── config/
│   └── McpConfiguration.java                     # MCP tool registration
└── service/
    ├── MongoServiceClient.java                   # Core MongoDB operations (21 tools)
    ├── MongoAdvancedAnalyticsService.java        # Analytics & admin (15 tools)
    └── MongoAIService.java                       # AI-powered features (10 tools)
```

## 📋 Prerequisites

- **Java 17** or higher
- **MongoDB 7.0+** (local or cloud instance)
- **Gradle 8.11+** (or use included wrapper)

### Optional AI Services
- **OpenAI API Key** (for GPT-4 integration)
- **Ollama** (for local AI model support)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/officeWorkPlace/spring-boot-ai-mongodb-mcp-server.git
cd spring-boot-ai-mongodb-mcp-server
```

### 2. Configure Environment
Create `.env` file in the project root:
```env
# MongoDB Configuration
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/mcpdb
MONGO_DATABASE=mcpdb

# AI Configuration (Optional)
OPENAI_API_KEY=your_openai_api_key_here
OLLAMA_BASE_URL=http://localhost:11434

# Server Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_DEEPAI=INFO
```

### 3. Build and Run

#### Using Gradle Wrapper
```bash
# Build the application
./gradlew build

# Run the application
./gradlew bootRun
```

#### Using JAR
```bash
# Build JAR
./gradlew bootJar

# Run JAR
java -jar build/libs/spring-boot-ai-mongodb-mcp-server-1.0.0.jar
```

#### Using Docker
```bash
# Build Docker image
./gradlew jibDockerBuild

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/mcpdb \
  deepai/spring-boot-ai-mongo-mcp-server:1.0.0
```

## 📡 MCP Integration

This server implements the Model Context Protocol (MCP) 1.0 specification. Once running, it can be integrated with MCP-compatible clients.

### Available MCP Tools

<details>
<summary><strong>Core Database Operations (21 tools)</strong></summary>

1. `mongo_list_databases` - List all databases with statistics
2. `mongo_create_database` - Create a new database
3. `mongo_drop_database` - Drop an existing database
4. `mongo_list_collections` - List collections in a database
5. `mongo_create_collection` - Create a new collection
6. `mongo_drop_collection` - Drop a collection
7. `mongo_insert_document` - Insert a single document
8. `mongo_insert_many_documents` - Insert multiple documents
9. `mongo_find_documents` - Find documents with filtering
10. `mongo_find_document_by_id` - Find document by ObjectId
11. `mongo_update_document` - Update a single document
12. `mongo_update_many_documents` - Update multiple documents
13. `mongo_delete_document` - Delete a single document
14. `mongo_delete_many_documents` - Delete multiple documents
15. `mongo_count_documents` - Count documents with filtering
16. `mongo_distinct_values` - Get distinct field values
17. `mongo_create_index` - Create database indexes
18. `mongo_list_indexes` - List collection indexes
19. `mongo_database_stats` - Get database statistics
20. `mongo_collection_stats` - Get collection statistics
21. `mongo_validate_connection` - Test database connectivity

</details>

<details>
<summary><strong>Advanced Analytics & Administration (15 tools)</strong></summary>

1. `mongo_aggregate_pipeline` - Execute aggregation pipelines
2. `mongo_faceted_search` - Multi-faceted search operations
3. `mongo_text_search` - Full-text search with scoring
4. `mongo_geospatial_query` - Geographic location queries
5. `mongo_time_series_analysis` - Time-based data analysis
6. `mongo_explain_query` - Query execution analysis
7. `mongo_optimize_indexes` - Index optimization recommendations
8. `mongo_backup_collection` - Backup collection data
9. `mongo_restore_collection` - Restore collection from backup
10. `mongo_migrate_data` - Data migration between collections
11. `mongo_bulk_operations` - Efficient bulk data operations
12. `mongo_transaction_demo` - Multi-document transactions
13. `mongo_schema_analysis` - Analyze document schemas
14. `mongo_performance_monitoring` - Monitor query performance
15. `mongo_maintenance_operations` - Database maintenance tasks

</details>

<details>
<summary><strong>AI-Powered Operations (10 tools)</strong></summary>

1. `mongo_ai_vector_search` - Semantic vector search
2. `mongo_ai_generate_embeddings` - Generate text embeddings
3. `mongo_ai_content_recommendation` - AI-powered content recommendations
4. `mongo_ai_document_classification` - Classify documents using AI
5. `mongo_ai_sentiment_analysis` - Analyze text sentiment
6. `mongo_ai_text_generation` - Generate content with AI
7. `mongo_ai_query_translation` - Convert natural language to MongoDB queries
8. `mongo_ai_data_insights` - Generate intelligent data insights
9. `mongo_ai_anomaly_detection` - Detect data anomalies
10. `mongo_ai_clustering_analysis` - Perform AI-based data clustering

</details>

### MCP Client Integration

The server can be integrated with any MCP-compatible client. Here's a basic integration example:

```typescript
// MCP Client Integration Example
import { MCPClient } from '@modelcontextprotocol/sdk';

const client = new MCPClient({
  command: 'java',
  args: ['-jar', 'spring-boot-ai-mongodb-mcp-server-1.0.0.jar'],
  env: {
    SPRING_DATA_MONGODB_URI: 'mongodb://localhost:27017/mcpdb'
  }
});

// List available tools
const tools = await client.listTools();
console.log('Available tools:', tools.tools.length);

// Execute a database operation
const result = await client.callTool({
  name: 'mongo_list_databases',
  arguments: {}
});
```

## 🔧 Configuration

### Application Properties

The application supports extensive configuration through environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATA_MONGODB_URI` | `mongodb://localhost:27017/mcpdb` | MongoDB connection URI |
| `MONGO_DATABASE` | `mcpdb` | Default database name |
| `OPENAI_API_KEY` | - | OpenAI API key for AI features |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | Ollama server URL |
| `SERVER_PORT` | `8080` | Application server port |
| `SPRING_PROFILES_ACTIVE` | `prod` | Active Spring profile |

### Profiles

- **`prod`**: Production configuration with security and monitoring
- **`dev`**: Development configuration with debug logging
- **`test`**: Testing configuration with embedded test database

## 🐳 Docker Deployment

### Using Docker Compose

Create `docker-compose.yml`:
```yaml
version: '3.8'
services:
  mongodb:
    image: mongo:7.0
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_DATABASE: mcpdb
    volumes:
      - mongodb_data:/data/db

  mcp-server:
    image: deepai/spring-boot-ai-mongo-mcp-server:1.0.0
    ports:
      - "8080:8080"
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://mongodb:27017/mcpdb
      SPRING_PROFILES_ACTIVE: prod
    depends_on:
      - mongodb
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  mongodb_data:
```

Run with:
```bash
docker-compose up -d
```

## ☸️ Kubernetes Deployment

### Basic Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-boot-ai-mongo-mcp-server
spec:
  replicas: 3
  selector:
    matchLabels:
      app: mcp-server
  template:
    metadata:
      labels:
        app: mcp-server
    spec:
      containers:
      - name: mcp-server
        image: deepai/spring-boot-ai-mongo-mcp-server:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATA_MONGODB_URI
          value: "mongodb://mongodb-service:27017/mcpdb"
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: mcp-server-service
spec:
  selector:
    app: mcp-server
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

## 📊 Monitoring & Observability

### Health Checks
```bash
# Application health
curl http://localhost:8080/actuator/health

# Readiness probe
curl http://localhost:8080/actuator/health/readiness

# MongoDB health
curl http://localhost:8080/actuator/health/mongo
```

### Metrics
```bash
# Prometheus metrics
curl http://localhost:8080/actuator/prometheus

# Application info
curl http://localhost:8080/actuator/info
```

### Performance Monitoring

The application includes comprehensive monitoring:
- **JVM Metrics**: Memory, garbage collection, threads
- **Database Metrics**: Connection pool, query performance
- **Custom Metrics**: MCP tool execution times and success rates
- **Distributed Tracing**: Request flow across components

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```

### Integration Tests
```bash
# Run integration tests (requires Docker)
./gradlew integrationTest
```

### Load Testing
```bash
# Performance testing with MongoDB operations
./gradlew performanceTest
```

## 🔒 Security

### Authentication
- OAuth2 JWT token authentication
- Role-based access control (RBAC)
- API rate limiting

### MongoDB Security
- Connection encryption (TLS/SSL)
- Database authentication
- Field-level encryption support

### Configuration
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-auth-server.com
```

## 🚀 Performance Optimization

### JVM Tuning
The application is optimized for Java 17 with:
- G1 Garbage Collector
- String deduplication
- Compressed OOPs
- Virtual threads (Project Loom)

### MongoDB Optimization
- Connection pooling configuration
- Index optimization recommendations
- Query performance monitoring
- Aggregation pipeline optimization

### Caching
- Caffeine cache for frequently accessed data
- Redis support for distributed caching
- Query result caching

## 📚 API Documentation

### MCP Protocol
The server implements MCP 1.0 specification with:
- **Tools**: 45+ specialized MongoDB and AI operations
- **Resources**: Database schema and statistics
- **Prompts**: AI-powered query generation

### REST Endpoints
- `GET /actuator/health` - Health check
- `GET /actuator/metrics` - Application metrics
- `GET /actuator/info` - Application information

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Code Standards
- Java 17 language features
- Spring Boot best practices
- Comprehensive test coverage (>80%)
- Documentation for public APIs

### Testing Requirements
- Unit tests for all business logic
- Integration tests for database operations
- Performance tests for critical paths

## 📈 Roadmap

### Upcoming Features
- [ ] GraphQL API support
- [ ] Redis caching integration
- [ ] Advanced AI model fine-tuning
- [ ] Real-time data streaming
- [ ] Multi-tenant support
- [ ] Advanced security features

### Performance Improvements
- [ ] Query optimization engine
- [ ] Adaptive connection pooling
- [ ] Smart caching strategies
- [ ] Parallel processing enhancements

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [MongoDB](https://www.mongodb.com/) - Database platform
- [Model Context Protocol](https://modelcontextprotocol.io/) - MCP specification
- [Spring AI](https://spring.io/projects/spring-ai) - AI integration framework
- [OpenAI](https://openai.com/) - AI model provider

## 📞 Support

For support and questions:
- 📧 Email: office.place.work.007@gmail.com
- 🐛 Issues: [GitHub Issues](https://github.com/officeWorkPlace/spring-boot-ai-mongodb-mcp-server/issues)
- 💬 Discussions: [GitHub Discussions](https://github.com/officeWorkPlace/spring-boot-ai-mongodb-mcp-server/discussions)

---

**Built with ❤️ by the DeepAI Team**
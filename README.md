# Production Spring Boot AI MongoDB MCP Server

[![Java 17](https://img.shields.io/badge/Java-17-brightgreen.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot 3.4.5](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB 7.0+](https://img.shields.io/badge/MongoDB-7.0+-green.svg)](https://www.mongodb.com/)
[![MCP 1.0](https://img.shields.io/badge/MCP-1.0-blue.svg)](https://modelcontextprotocol.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A production-ready Spring Boot application implementing the Model Context Protocol (MCP) server with comprehensive MongoDB operations and AI-powered features. This server provides **41 specialized tools** across 3 service classes for database management, analytics, and intelligent data processing.

## 🚀 Features

### Core Capabilities
- **41 MCP Tools**: Comprehensive MongoDB operations across three specialized service categories
- **Java 17 Compatible**: Optimized for Java 17 with Spring Boot 3.4.5
- **Production Ready**: Docker, security, monitoring, and testing configurations
- **Spring AI Integration**: Built with Spring AI MCP Server starter (v1.0.0-M7)
- **Real-time Operations**: Advanced aggregation pipelines and query optimization
- **Security**: Spring Security with basic authentication and role-based access control

### MCP Tool Categories

#### 🗄️ Core Database Operations (MongoServiceClient - 22 Tools)
- **Database Management**: Create, drop, list databases with statistics
- **Collection Operations**: CRUD operations, indexing, schema validation
- **Document Management**: Insert, update, delete, bulk operations
- **Query Operations**: Find, count, distinct with advanced filtering
- **Administrative Tools**: Database stats, collection info, connection management

#### 📊 Advanced Analytics & Administration (MongoAdvancedAnalyticsService - 12 Tools)
- **Aggregation Pipelines**: Complex data transformations and analysis
- **Index Management**: Create, optimize, and analyze database indexes
- **Performance Monitoring**: Query performance analysis and optimization
- **Search Operations**: Text search, geospatial queries, and schema validation
- **Administrative Operations**: Database maintenance and repair utilities

#### 🤖 AI-Powered Operations (MongoAIService - 7 Tools)
- **Vector Search**: Semantic search with embedding generation
- **Content Analysis**: AI-powered document insights and pattern recognition
- **Data Analysis**: Intelligent collection structure analysis
- **Natural Language Queries**: Semantic search across collections
- **AI-Enhanced Operations**: Document summarization and query suggestions

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

## 🔧 Maven Project Information

```xml
<groupId>com.deepai</groupId>
<artifactId>spring-boot-ai-mongo-mcp-server</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>jar</packaging>
```

**Built JAR:** `target/spring-boot-ai-mongo-mcp-server-0.0.1-SNAPSHOT.jar`

### Maven Profiles
- **`dev`** - Development profile with debug logging
- **`prod`** - Production profile with optimized settings
- **`test`** - Testing profile with embedded MongoDB
- **`integration-test`** - Integration testing with Docker
- **`performance-test`** - Performance testing profile

## 📋 Prerequisites

- **Java 17** or higher
- **MongoDB 7.0+** (local or cloud instance)
- **Maven 3.6+** (or use included wrapper)

### Dependencies
- **Spring Boot**: 3.4.5 (Latest stable release)
- **Spring AI MCP Server**: 1.0.0-M7 (MCP Protocol implementation)
- **Spring Data MongoDB**: Latest stable for database operations
- **Spring Security**: Basic authentication support
- **Spring Boot Actuator**: Health monitoring and metrics
- **MongoDB Java Driver**: Official MongoDB connectivity
- **Java**: 17+ (LTS version)
- **Maven**: 3.6+ (Build automation)

### Optional Configuration
- **MongoDB URI**: Default `mongodb://localhost:27017/mcpserver`
- **Basic Auth**: Default admin/admin credentials
- **Docker**: For containerization support
- **Testcontainers**: Integration testing with embedded MongoDB

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/officeWorkPlace/spring-boot-ai-mongodb-mcp-server.git
cd spring-boot-ai-mongodb-mcp-server
```

### 2. Configure Environment
Create `.env` file in the project root or set environment variables:
```env
# MongoDB Configuration
MONGODB_URI=mongodb://localhost:27017/mcpserver
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/mcpserver
SPRING_DATA_MONGODB_DATABASE=mcpserver

# MCP Server Configuration
SPRING_AI_MCP_SERVER_NAME=mongo-mcp-server
SPRING_AI_MCP_SERVER_VERSION=0.0.1

# Optional AI Configuration
# SPRING_AI_OPENAI_API_KEY=your_openai_api_key_here

# Server Configuration
SERVER_PORT=8080

# Security Configuration (Basic Auth)
SPRING_SECURITY_USER_NAME=admin
SPRING_SECURITY_USER_PASSWORD=admin

# Logging
LOGGING_FILE_NAME=./logs/spring-boot-ai-mongo-mcp-server.log
```

### 3. Build and Run

#### Using Maven Wrapper
```bash
# Build the application (Linux/Mac)
./mvnw clean compile

# Build the application (Windows)
mvnw.cmd clean compile

# Run the application (Linux/Mac)
./mvnw spring-boot:run

# Run the application (Windows)
mvnw.cmd spring-boot:run

# Build and run tests
./mvnw clean install    # Linux/Mac
mvnw.cmd clean install  # Windows

# Skip tests during build
./mvnw clean package -DskipTests    # Linux/Mac
mvnw.cmd clean package -DskipTests  # Windows

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev    # Linux/Mac
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev  # Windows
```

#### Using JAR
```bash
# Build JAR
./mvnw clean package

# Run JAR
java -jar target/spring-boot-ai-mongo-mcp-server-0.0.1-SNAPSHOT.jar

# Run JAR with specific profile
java -jar target/spring-boot-ai-mongo-mcp-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

#### Using Docker
```bash
# Build Docker image
docker build -t deepai/spring-boot-ai-mongo-mcp-server:1.0.0 .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/mcpdb \
  deepai/spring-boot-ai-mongo-mcp-server:1.0.0
```

## 📡 MCP Integration

This server implements the Model Context Protocol (MCP) 1.0 specification. Once running, it can be integrated with MCP-compatible clients.

### Available MCP Tools

<details>
<summary><strong>Core Database Operations (22 tools)</strong></summary>

1. `listDatabases` - List all databases with statistics
2. `createDatabase` - Create a new database with initial collection
3. `dropDatabase` - Drop an existing database
4. `getDatabaseStats` - Get comprehensive database statistics
5. `ping` - Test database connectivity
6. `listCollections` - List collections in a database with metadata
7. `createCollection` - Create a new collection with schema validation
8. `dropCollection` - Drop a collection
9. `getCollectionStats` - Get detailed collection statistics
10. `renameCollection` - Rename a collection safely
11. `insertDocument` - Insert a single document with validation
12. `insertMany` - Insert multiple documents in bulk
13. `findDocument` - Advanced queries with projection and sorting
14. `findOne` - Find a single document by criteria
15. `updateDocument` - Update documents matching criteria
16. `deleteDocument` - Delete documents matching criteria
17. `countDocuments` - Count documents with filtering
18. `simpleQuery` - Execute simple field-value queries
19. `complexQuery` - Execute complex MongoDB queries
20. `listIndexes` - List all indexes for a collection

</details>

<details>
<summary><strong>Advanced Analytics & Administration (12 tools)</strong></summary>

1. `aggregatePipeline` - Execute complex aggregation pipelines
2. `distinctValues` - Get distinct field values with filtering
3. `groupByField` - Group documents by field with counts
4. `textSearch` - Full-text search with scoring
5. `geoSearch` - Geospatial queries and operations
6. `createIndex` - Create single or compound indexes
7. `createVectorIndex` - Create vector search indexes for AI
8. `dropIndex` - Drop/delete indexes by name
9. `reIndex` - Rebuild all indexes for optimization
10. `explainQuery` - Analyze query execution plans
11. `validateSchema` - Validate document schemas
12. `repairDatabase` - Database maintenance and repair

</details>

<details>
<summary><strong>AI-Powered Operations (7 tools)</strong></summary>

1. `vectorSearch` - Semantic similarity search using embeddings
2. `aiAnalyzeDocument` - AI-powered document content analysis
3. `aiAnalyzeCollection` - Intelligent collection structure analysis
4. `aiQuerySuggestion` - Get AI suggestions for optimal queries
5. `aiDocumentSummary` - Generate AI-powered document summaries
6. `semanticSearch` - Natural language search across collections
7. `generateEmbeddings` - Generate vector embeddings using AI models

</details>

### MCP Client Integration

The server can be integrated with any MCP-compatible client. Here's a basic integration example:

```typescript
// MCP Client Integration Example
import { MCPClient } from '@modelcontextprotocol/sdk';

const client = new MCPClient({
  command: 'java',
  args: ['-jar', 'spring-boot-ai-mongo-mcp-server-0.0.1-SNAPSHOT.jar'],
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
# Run all tests (Linux/Mac)
./mvnw test

# Run all tests (Windows)
mvnw.cmd test

# Run tests with coverage (Linux/Mac)
./mvnw test jacoco:report

# Run tests with coverage (Windows)
mvnw.cmd test jacoco:report

# View coverage report (Linux/Mac)
open target/site/jacoco/index.html

# View coverage report (Windows)
start target/site/jacoco/index.html
```

### Integration Tests
```bash
# Run integration tests (requires Docker) - Linux/Mac
./mvnw verify -P integration-test

# Run integration tests (requires Docker) - Windows
mvnw.cmd verify -P integration-test
```

### Load Testing
```bash
# Performance testing with MongoDB operations (Linux/Mac)
./mvnw test -P performance-test

# Performance testing with MongoDB operations (Windows)
mvnw.cmd test -P performance-test
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
# MongoDB MCP Server API - One Click Testing Suite

## 🚀 Quick Start (One Click Execution)

### Windows (Recommended)
```powershell
# PowerShell (recommended for Windows)
.\run-api-tests.ps1

# Or Command Prompt
run-api-tests.bat
```

### Linux/Mac
```bash
# Make executable and run
chmod +x run-api-tests.sh
./run-api-tests.sh
```

## ✅ Validation Features

### 🔄 **Automated Test Execution**
- **39 endpoints** tested automatically
- **Sequential execution** with proper dependencies
- **Dynamic test data** generation
- **Response validation** for all endpoints
- **Performance monitoring** (response times)

### 📊 **Comprehensive Reporting**
- **HTML Report**: Visual test results with charts
- **JSON Results**: Machine-readable test data
- **Console Output**: Real-time execution feedback
- **Success/Failure Summary**: Quick overview

### 🛡️ **Built-in Validations**
- ✅ Response status codes (200, 201, 204)
- ✅ Response time limits (< 5 seconds per request)
- ✅ JSON structure validation
- ✅ Data integrity checks
- ✅ Error handling verification

### 🔗 **Dependency Management**
- ✅ Automatic Newman installation
- ✅ Prerequisites verification
- ✅ Service health checks
- ✅ Environment setup validation

## 📋 Prerequisites

### Required Services
1. **MongoDB** running on `localhost:27017`
2. **Spring Boot Application** running on `localhost:8080`
3. **Node.js** installed (for Newman CLI)

### Installation Commands
```bash
# Install Node.js (if not installed)
# Download from: https://nodejs.org/

# Newman will be auto-installed by the scripts
# Or install manually:
npm install -g newman
npm install -g newman-reporter-html
```

## 🎯 What Gets Tested

### **Phase 1: Connection & Setup** (3 tests)
- MongoDB connectivity validation
- Database listing and creation
- Basic service health checks

### **Phase 2: Collection Management** (5 tests)
- Collection creation and listing
- Collection statistics and metadata
- Collection renaming operations

### **Phase 3: Document Operations** (10 tests)
- Single and batch document insertion
- Document querying (find, findOne)
- Document updates and deletions
- Document counting operations

### **Phase 4: Query Operations** (5 tests)
- Simple field-value queries
- Complex MongoDB queries with filters
- Aggregation pipelines
- Query performance analysis

### **Phase 5: Index Management** (4 tests)
- Index creation (standard and vector)
- Index listing and metadata
- Index deletion and rebuilding
- Vector search index configuration

### **Phase 6: Advanced Analytics** (6 tests)
- Distinct value analysis
- Field grouping operations
- Full-text search capabilities
- Geospatial queries
- Query execution planning
- Schema validation

### **Phase 7: AI-Powered Operations** (4 tests)
- Collection analysis with AI
- Document analysis and summarization
- Embedding generation
- Semantic and vector search

### **Phase 8: Administrative** (2 tests)
- Database statistics and health
- Database repair operations

## 📈 Test Results Analysis

### **Success Criteria**
- ✅ **Success Rate**: ≥ 95% (37+ out of 39 tests pass)
- ✅ **Performance**: Average response time < 3 seconds
- ✅ **Reliability**: No connection timeouts or server errors

### **Report Locations**
```
test-results/
├── mongodb-mcp-api-report.html    # Visual HTML report
├── mongodb-mcp-api-results.json   # Detailed JSON results
└── newman-run-summary.json        # Newman execution summary
```

### **Key Metrics Tracked**
- Total requests executed
- Pass/fail rates per endpoint
- Response times (min, max, average)
- Data transfer statistics
- Error categorization

## 🔧 Customization Options

### **Environment Variables**
Modify these in the collection or environment file:
```json
{
  "baseUrl": "http://localhost:8080",
  "dbName": "testDB_{{$randomInt}}",
  "collectionName": "testCollection_{{$timestamp}}"
}
```

### **Test Data Customization**
The collection uses dynamic test data:
- Random user names and emails
- Timestamp-based unique identifiers
- Configurable sample sizes
- Realistic data patterns

### **Execution Options**
```bash
# Custom Newman execution
newman run MongoDB-MCP-Server-Postman-Collection.json \
  --delay-request 1000 \
  --timeout-request 15000 \
  --iteration-count 2 \
  --bail
```

## 🚨 Troubleshooting

### Common Issues and Solutions

#### **1. Connection Refused**
```
❌ Error: connect ECONNREFUSED 127.0.0.1:8080
```
**Solution**: Start the Spring Boot application
```bash
mvn spring-boot:run
```

#### **2. MongoDB Not Running**
```
❌ MongoServerSelectionException: Unable to connect to MongoDB
```
**Solution**: Start MongoDB service
```bash
# Windows
net start MongoDB

# Linux/Mac
sudo systemctl start mongod
# or
mongod --dbpath /data/db
```

#### **3. Newman Not Found**
```
❌ 'newman' is not recognized as an internal or external command
```
**Solution**: Install Newman globally
```bash
npm install -g newman
```

#### **4. Test Failures**
```
❌ Some tests failed - check detailed report
```
**Solution**: Review the HTML report for specific failure details:
- Check endpoint-specific error messages
- Verify test data format
- Confirm service configurations

### **Debug Mode**
For detailed debugging, run Newman with additional flags:
```bash
newman run MongoDB-MCP-Server-Postman-Collection.json \
  --verbose \
  --debug \
  --reporter-cli-silent=false
```

## 📚 Additional Resources

### **Newman CLI Documentation**
- [Newman GitHub Repository](https://github.com/postmanlabs/newman)
- [Newman Command Line Options](https://github.com/postmanlabs/newman#command-line-options)

### **Postman Collection Format**
- [Postman Collection SDK](https://github.com/postmanlabs/postman-collection)
- [Collection Format v2.1](https://schema.postman.com/json/collection/v2.1.0/docs/index.html)

### **MongoDB MCP Server Documentation**
- Review the generated `MongoDB-MCP-API-Testing-Guide.md` for detailed endpoint documentation
- Check `MongoMcpApiController.java` for implementation details

## 🎉 Success Indicators

When the test suite runs successfully, you should see:

```
✅ MongoDB MCP Server API - One Click Testing Complete!
📊 39/39 tests passed (100% success rate)
⚡ Average response time: 1.2 seconds
🎯 All MongoDB MCP Server tools validated successfully!
```

This indicates your MongoDB MCP Server is production-ready and all 39 tools are functioning correctly! 🚀

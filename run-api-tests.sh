#!/bin/bash

echo "===================================================="
echo "MongoDB MCP Server API - One Click Test Runner"
echo "===================================================="
echo

# Check if Newman is installed
if ! command -v newman &> /dev/null; then
    echo "❌ Newman is not installed. Installing Newman..."
    npm install -g newman
    npm install -g newman-reporter-html
fi

# Create test results directory
mkdir -p test-results

echo "🚀 Starting automated test execution..."
echo

# Run the Postman collection with Newman
newman run "MongoDB-MCP-Server-Postman-Collection.json" \
    --reporters cli,json,html \
    --reporter-json-export "test-results/mongodb-mcp-api-results.json" \
    --reporter-html-export "test-results/mongodb-mcp-api-report.html" \
    --delay-request 500 \
    --timeout-request 10000 \
    --timeout-script 5000 \
    --verbose

echo
echo "===================================================="
echo "Test Execution Complete!"
echo "===================================================="
echo "📊 Results available in: test-results/"
echo "📄 HTML Report: test-results/mongodb-mcp-api-report.html"
echo "📄 JSON Results: test-results/mongodb-mcp-api-results.json"
echo "===================================================="

# Check exit status and open report if successful
if [ $? -eq 0 ]; then
    echo "🌐 Opening test report in browser..."
    if command -v xdg-open &> /dev/null; then
        xdg-open "test-results/mongodb-mcp-api-report.html"
    elif command -v open &> /dev/null; then
        open "test-results/mongodb-mcp-api-report.html"
    else
        echo "Please manually open: test-results/mongodb-mcp-api-report.html"
    fi
else
    echo "❌ Test execution failed. Check the output above for details."
fi

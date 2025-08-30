@echo off
echo ====================================================
echo MongoDB MCP Server API - One Click Test Runner
echo ====================================================
echo.

REM Check if Newman is installed
newman --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Newman is not installed. Installing Newman...
    npm install -g newman
    npm install -g newman-reporter-html
)

REM Create test results directory
if not exist "test-results" mkdir test-results

echo 🚀 Starting automated test execution...
echo.

REM Run the Postman collection with Newman
newman run "MongoDB-MCP-Server-Postman-Collection.json" ^
    --reporters cli,json,html ^
    --reporter-json-export "test-results/mongodb-mcp-api-results.json" ^
    --reporter-html-export "test-results/mongodb-mcp-api-report.html" ^
    --delay-request 500 ^
    --timeout-request 10000 ^
    --timeout-script 5000 ^
    --verbose

echo.
echo ====================================================
echo Test Execution Complete!
echo ====================================================
echo 📊 Results available in: test-results/
echo 📄 HTML Report: test-results/mongodb-mcp-api-report.html
echo 📄 JSON Results: test-results/mongodb-mcp-api-results.json
echo ====================================================

REM Open HTML report if execution was successful
if %errorlevel% equ 0 (
    echo 🌐 Opening test report in browser...
    start "" "test-results/mongodb-mcp-api-report.html"
) else (
    echo ❌ Test execution failed. Check the output above for details.
)

pause

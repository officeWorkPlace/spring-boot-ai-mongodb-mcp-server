# MongoDB MCP Server API - One Click Test Runner (PowerShell)
# ====================================================================

Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "MongoDB MCP Server API - One Click Test Runner" -ForegroundColor Yellow
Write-Host "====================================================" -ForegroundColor Cyan
Write-Host ""

# Function to check if command exists
function Test-Command($command) {
    try {
        Get-Command $command -ErrorAction Stop
        return $true
    }
    catch {
        return $false
    }
}

# Check prerequisites
Write-Host "Checking prerequisites..." -ForegroundColor Blue

# Check if Node.js is installed
if (-not (Test-Command "node")) {
    Write-Host "Node.js is not installed. Please install Node.js first." -ForegroundColor Red
    Write-Host "Download from: https://nodejs.org/" -ForegroundColor Yellow
    exit 1
}

# Check if Newman is installed
if (-not (Test-Command "newman")) {
    Write-Host "Newman is not installed. Installing Newman..." -ForegroundColor Yellow
    npm install -g newman
    npm install -g newman-reporter-html
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Failed to install Newman. Please check your npm configuration." -ForegroundColor Red
        exit 1
    }
    Write-Host "Newman installed successfully!" -ForegroundColor Green
}

# Check if MongoDB is running
Write-Host "Checking MongoDB connection..." -ForegroundColor Blue
try {
    # Create credentials for basic auth
    $credential = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("admin:admin"))
    $headers = @{Authorization="Basic $credential"}
    
    Invoke-WebRequest -Uri "http://localhost:8080/api/mongo/ping" -Headers $headers -TimeoutSec 5 -ErrorAction Stop | Out-Null
    Write-Host "MongoDB MCP Server is running and authenticated!" -ForegroundColor Green
}
catch {
    Write-Host "Cannot connect to MongoDB MCP Server at http://localhost:8080" -ForegroundColor Red
    Write-Host "Please ensure:" -ForegroundColor Yellow
    Write-Host "   - MongoDB is running (default port 27017)" -ForegroundColor White
    Write-Host "   - Spring Boot application is running (port 8080)" -ForegroundColor White
    Write-Host "   - Authentication is configured (admin:admin)" -ForegroundColor White
    Write-Host "   - Run: mvn spring-boot:run" -ForegroundColor White
    Write-Host ""
    Write-Host "   in different terminal" -ForegroundColor White
    exit 1
}

# Create test results directory
if (-not (Test-Path "test-results")) {
    New-Item -ItemType Directory -Path "test-results" | Out-Null
    Write-Host "Created test-results directory" -ForegroundColor Green
}

Write-Host ""
Write-Host "Starting automated test execution..." -ForegroundColor Green
Write-Host "This may take a few minutes to complete all 39 endpoints..." -ForegroundColor Yellow
Write-Host ""

# Run the Postman collection with Newman
$newmanArgs = @(
    "run", "MongoDB-MCP-Server-Postman-Collection.json",
    "--reporters", "cli,json,html",
    "--reporter-json-export", "test-results/mongodb-mcp-api-results.json",
    "--reporter-html-export", "test-results/mongodb-mcp-api-report.html",
    "--delay-request", "500",
    "--timeout-request", "10000",
    "--timeout-script", "5000",
    "--verbose"
)

& newman @newmanArgs

$exitCode = $LASTEXITCODE

# Generate enhanced HTML report
Write-Host ""
Write-Host "🎨 Generating enhanced UI report..." -ForegroundColor Yellow
if (Test-Path "test-results/generate-enhanced-report.js") {
    try {
        & node "test-results/generate-enhanced-report.js"
        Write-Host "✅ Enhanced report generated successfully!" -ForegroundColor Green
    }
    catch {
        Write-Host "⚠️ Could not generate enhanced report, using basic report" -ForegroundColor Yellow
    }
} else {
    Write-Host "⚠️ Enhanced report generator not found, using basic report" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "Test Execution Complete!" -ForegroundColor Yellow
Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "Results available in: test-results/" -ForegroundColor Blue
Write-Host "Enhanced HTML Report: test-results/mongodb-mcp-api-report.html" -ForegroundColor Blue
Write-Host "JSON Results: test-results/mongodb-mcp-api-results.json" -ForegroundColor Blue
Write-Host "====================================================" -ForegroundColor Cyan

# Check results and open report
if ($exitCode -eq 0) {
    Write-Host "All tests completed successfully!" -ForegroundColor Green
    Write-Host "Opening test report in browser..." -ForegroundColor Blue
    Start-Process "test-results/mongodb-mcp-api-report.html"
} else {
    Write-Host "Some tests failed. Check the output above for details." -ForegroundColor Red
    Write-Host "Review the HTML report for detailed failure information." -ForegroundColor Yellow
}

# Display summary information
if (Test-Path "test-results/mongodb-mcp-api-results.json") {
    try {
        $results = Get-Content "test-results/mongodb-mcp-api-results.json" | ConvertFrom-Json
        $totalTests = $results.run.stats.tests.total
        $failedTests = $results.run.stats.tests.failed
        $passedTests = $totalTests - $failedTests
        $totalAssertions = $results.run.stats.assertions.total
        $failedAssertions = $results.run.stats.assertions.failed
        $passedAssertions = $totalAssertions - $failedAssertions
        $avgResponseTime = [math]::Round($results.run.timings.responseAverage, 2)
        $successRate = if ($totalTests -gt 0) { [math]::Round(($passedTests/$totalTests)*100, 2) } else { 0 }
        
        Write-Host ""
        Write-Host "Test Execution Summary:" -ForegroundColor Yellow
        Write-Host "   Total Tests: $totalTests" -ForegroundColor White
        Write-Host "   Passed Tests: $passedTests" -ForegroundColor Green
        Write-Host "   Failed Tests: $failedTests" -ForegroundColor Red
        Write-Host "   Total Assertions: $totalAssertions" -ForegroundColor White
        Write-Host "   Passed Assertions: $passedAssertions" -ForegroundColor Green
        Write-Host "   Failed Assertions: $failedAssertions" -ForegroundColor Red
        Write-Host "   Success Rate: $successRate%" -ForegroundColor $(if ($successRate -eq 100) { "Green" } elseif ($successRate -ge 90) { "Yellow" } else { "Red" })
        Write-Host "   Average Response Time: ${avgResponseTime}ms" -ForegroundColor Blue
    }
    catch {
        Write-Host "Could not parse test results for summary." -ForegroundColor Yellow
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

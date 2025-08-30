# Test script to verify Write-Host formatting
$totalTests = 39
$passedTests = 39
$failedTests = 0
$totalAssertions = 86
$passedAssertions = 86
$failedAssertions = 0
$successRate = 100
$avgResponseTime = 199.85

Write-Host ""
Write-Host "Test Execution Summary:" -ForegroundColor Yellow
Write-Host "   Total Tests: $totalTests" -ForegroundColor White
Write-Host "   Passed Tests: $passedTests" -ForegroundColor Green
Write-Host "   Failed Tests: $failedTests" -ForegroundColor Red
Write-Host "   Total Assertions: $totalAssertions" -ForegroundColor White
Write-Host "   Passed Assertions: $passedAssertions" -ForegroundColor Green
Write-Host "   Failed Assertions: $failedAssertions" -ForegroundColor Red
Write-Host "   Success Rate: $successRate%" -ForegroundColor Green
Write-Host "   Average Response Time: ${avgResponseTime}ms" -ForegroundColor Blue

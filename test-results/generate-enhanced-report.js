const fs = require('fs');
const path = require('path');

// Read the Newman test results
const resultsPath = path.join(__dirname, 'mongodb-mcp-api-results.json');
const templatePath = path.join(__dirname, 'mongodb-mcp-api-report-enhanced.html');
const outputPath = path.join(__dirname, 'mongodb-mcp-api-report.html');

try {
    // Load test results
    const results = JSON.parse(fs.readFileSync(resultsPath, 'utf8'));
    
    // Extract test data from Newman results
    const testData = [];
    let globalStats = {
        totalTests: 0,
        totalAssertions: 0,
        passedAssertions: 0,
        failedAssertions: 0
    };

    // Process each execution in the results
    if (results.run && results.run.executions) {
        results.run.executions.forEach((execution, index) => {
            const item = execution.item;
            const request = execution.request;
            const response = execution.response;
            
            // Extract test results
            const tests = [];
            let passedTests = 0;
            let failedTests = 0;
            
            if (execution.assertions) {
                execution.assertions.forEach(assertion => {
                    const passed = !assertion.error;
                    tests.push({
                        name: assertion.assertion,
                        passed: passed
                    });
                    
                    if (passed) {
                        passedTests++;
                        globalStats.passedAssertions++;
                    } else {
                        failedTests++;
                        globalStats.failedAssertions++;
                    }
                    globalStats.totalAssertions++;
                });
            }
            
            // Determine overall test status
            const testPassed = failedTests === 0;
            
            // Extract request details
            const requestDetails = {
                headers: request ? request.header.reduce((acc, h) => {
                    acc[h.key] = h.value;
                    return acc;
                }, {}) : {},
                body: request ? (request.body ? request.body.raw : null) : null,
                query: request ? (request.url.query || []).reduce((acc, q) => {
                    acc[q.key] = q.value;
                    return acc;
                }, {}) : {}
            };
            
            // Extract response details
            const responseDetails = {
                headers: response ? response.header.reduce((acc, h) => {
                    acc[h.key] = h.value;
                    return acc;
                }, {}) : {},
                body: response ? parseResponseBody(response.stream, response.header) : null,
                status: response ? response.code : 0,
                statusText: response ? response.status : 'Unknown'
            };
            
            // Build the test data object
            const testItem = {
                id: item.id || `test-${index}`,
                name: item.name || `Test ${index + 1}`,
                method: request ? request.method : 'UNKNOWN',
                url: request ? buildUrl(request.url) : 'Unknown URL',
                status: response ? response.code : 0,
                passed: testPassed,
                responseTime: execution.response ? execution.response.responseTime : 0,
                responseSize: response ? formatBytes(response.responseSize || 0) : '0B',
                tests: tests,
                request: requestDetails,
                response: responseDetails
            };
            
            testData.push(testItem);
            globalStats.totalTests++;
        });
    }
    
    // Calculate success rate
    const successRate = globalStats.totalAssertions > 0 
        ? ((globalStats.passedAssertions / globalStats.totalAssertions) * 100).toFixed(1)
        : '0.0';
    
    // Read the HTML template
    let htmlTemplate = fs.readFileSync(templatePath, 'utf8');
    
    // Replace the test data in the template
    const testDataJson = JSON.stringify(testData, null, 2);
    
    // Process the template to replace ${testIndex} placeholders with proper JavaScript concatenation
    htmlTemplate = htmlTemplate.replace(/\\\$\{testIndex\}/g, '${testIndex}');
    
    // Debug: Log the test data to see what we have
    console.log('DEBUG: Test data length:', testData.length);
    console.log('DEBUG: First test response body:', testData[0]?.response?.body);
    
    // Set JSON format as default (toggle should be unchecked by default)
    htmlTemplate = htmlTemplate.replace(
        /type="checkbox" checked id="requestToggle_/g,
        'type="checkbox" id="requestToggle_'
    );
    htmlTemplate = htmlTemplate.replace(
        /type="checkbox" checked id="responseToggle_/g,
        'type="checkbox" id="responseToggle_'
    );
    
    // Change default active state to JSON instead of Human
    htmlTemplate = htmlTemplate.replace(
        /<span class="toggle-label">JSON<\/span>/g,
        '<span class="toggle-label active">JSON</span>'
    );
    htmlTemplate = htmlTemplate.replace(
        /<span class="toggle-label active">Human<\/span>/g,
        '<span class="toggle-label">Human</span>'
    );
    
    // Set JSON as default display (block) and human-readable as hidden (none)
    htmlTemplate = htmlTemplate.replace(
        /<div class="human-readable" style="display: block;">/g,
        '<div class="human-readable" style="display: none;">'
    );
    htmlTemplate = htmlTemplate.replace(
        /<div class="code-block" style="display: none;">/g,
        '<div class="code-block" style="display: block;">'
    );
    
    // Generate HTML for test items
    const testItemsHtml = generateTestItemsHtml(testData);
    
    // Replace the loading placeholder with actual test items
    htmlTemplate = htmlTemplate.replace(
        /<div id="test-results">[\s\S]*?<\/div>/,
        `<div id="test-results">${testItemsHtml}</div>`
    );

    // Inject the actual data into the HTML
    htmlTemplate = htmlTemplate.replace(
        /const testData = \[[\s\S]*?\];/m,
        `const testData = ${testDataJson};`
    );

    // Update statistics in the HTML
    htmlTemplate = htmlTemplate.replace(
        /<div class="stat-number" id="total-tests">\d+<\/div>/,
        `<div class="stat-number" id="total-tests">${globalStats.totalTests}</div>`
    );
    htmlTemplate = htmlTemplate.replace(
        /<div class="stat-number" id="passed-tests">\d+<\/div>/,
        `<div class="stat-number" id="passed-tests">${globalStats.passedAssertions}</div>`
    );
    htmlTemplate = htmlTemplate.replace(
        /<div class="stat-number" id="failed-tests">\d+<\/div>/,
        `<div class="stat-number" id="failed-tests">${globalStats.failedAssertions}</div>`
    );
    htmlTemplate = htmlTemplate.replace(
        /<div class="stat-number" id="success-rate">[\d.]+%<\/div>/,
        `<div class="stat-number" id="success-rate">${successRate}%</div>`
    );
    
    // Write the enhanced report
    fs.writeFileSync(outputPath, htmlTemplate);
    
    console.log('✅ Enhanced MongoDB MCP API Report generated successfully!');
    console.log(`📊 Statistics:`);
    console.log(`   • Total Tests: ${globalStats.totalTests}`);
    console.log(`   • Total Assertions: ${globalStats.totalAssertions}`);
    console.log(`   • Passed Assertions: ${globalStats.passedAssertions}`);
    console.log(`   • Failed Assertions: ${globalStats.failedAssertions}`);
    console.log(`   • Success Rate: ${successRate}%`);
    console.log(`📁 Report saved to: ${outputPath}`);
    
} catch (error) {
    console.error('❌ Error generating enhanced report:', error);
    process.exit(1);
}

// Helper functions
function buildUrl(urlObj) {
    if (!urlObj) return 'Unknown URL';
    
    const protocol = urlObj.protocol || 'http';
    const host = Array.isArray(urlObj.host) ? urlObj.host.join('.') : (urlObj.host || 'localhost');
    const port = urlObj.port ? `:${urlObj.port}` : '';
    const path = Array.isArray(urlObj.path) ? '/' + urlObj.path.join('/') : (urlObj.path || '');
    
    let url = `${protocol}://${host}${port}${path}`;
    
    if (urlObj.query && urlObj.query.length > 0) {
        const queryString = urlObj.query
            .map(q => `${encodeURIComponent(q.key)}=${encodeURIComponent(q.value)}`)
            .join('&');
        url += `?${queryString}`;
    }
    
    return url;
}

function parseResponseBody(stream, headers) {
    if (!stream) return null;
    
    try {
        // Convert stream buffer to string
        const bodyString = Buffer.from(stream).toString('utf8');
        
        // Check if it's JSON based on content type or try to parse
        const contentType = headers.find(h => h.key.toLowerCase() === 'content-type');
        const isJson = contentType && contentType.value.includes('application/json');
        
        if (isJson || (bodyString.trim().startsWith('{') || bodyString.trim().startsWith('['))) {
            try {
                return JSON.parse(bodyString);
            } catch (e) {
                return bodyString;
            }
        }
        
        return bodyString;
    } catch (e) {
        return 'Unable to parse response body';
    }
}

function formatBytes(bytes) {
    if (bytes === 0) return '0B';
    
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + sizes[i];
}

function parseResponseBody(stream, headers) {
    if (!stream) return null;
    
    try {
        // Convert stream buffer to string
        const bodyString = Buffer.from(stream).toString('utf8');
        
        // Check if it's JSON based on content type or try to parse
        const contentType = headers.find(h => h.key.toLowerCase() === 'content-type');
        const isJson = contentType && contentType.value.includes('application/json');
        
        if (isJson || (bodyString.trim().startsWith('{') || bodyString.trim().startsWith('['))) {
            try {
                return JSON.parse(bodyString);
            } catch (e) {
                return bodyString;
            }
        }
        
        return bodyString;
    } catch (e) {
        return 'Unable to parse response body';
    }
}

function generateTestItemsHtml(testData) {
    return testData.map((test, index) => {
        // Debug: Check if response body exists
        console.log(`Test ${index}: ${test.name}, Response body exists: ${!!test.response.body}, Body value: ${JSON.stringify(test.response.body).substring(0, 100)}`);
        
        return `
        <div class="test-item" data-method="${test.method.toLowerCase()}" data-status="${test.passed ? 'passed' : 'failed'}">
            <div class="test-header" onclick="toggleTest(this)">
                <div class="test-title">
                    <span class="method-badge method-${test.method.toLowerCase()}">${test.method}</span>
                    <span class="test-name">${test.name}</span>
                    <span class="status-badge ${test.passed ? 'status-success' : 'status-error'}">
                        <i class="fas ${test.passed ? 'fa-check' : 'fa-times'}"></i>
                        ${test.passed ? 'Passed' : 'Failed'}
                    </span>
                </div>
                <div class="test-meta">
                    <span class="response-time">${test.responseTime}ms</span>
                    <span class="response-size">${test.responseSize}</span>
                    <span class="test-count">${test.tests.filter(t => t.passed).length}/${test.tests.length} Tests Passed</span>
                    <i class="fas fa-chevron-down expand-icon"></i>
                </div>
            </div>
            
            <div class="test-content">
                <!-- Test Details -->
                <div class="test-details">
                    <h4><i class="fas fa-info-circle"></i> Test Results</h4>
                    <div class="test-list">
                        ${test.tests.map(t => `
                            <div class="test-assertion ${t.passed ? 'passed' : 'failed'}">
                                <i class="fas ${t.passed ? 'fa-check' : 'fa-times'}"></i>
                                <span>${t.name}</span>
                            </div>
                        `).join('')}
                    </div>
                </div>

                <!-- Request and Response -->
                <div class="request-response-grid">
                    <!-- Request Details -->
                    <div class="section">
                        <div class="section-title">
                            <i class="fas fa-arrow-right"></i>
                            Request Details
                        </div>
                        
                        <div class="info-item">
                            <strong>Method:</strong>
                            <span class="method-badge method-${test.method.toLowerCase()}">${test.method}</span>
                        </div>
                        
                        <div class="info-item">
                            <strong>URL:</strong>
                            <code class="url">${test.url}</code>
                        </div>

                        ${test.request.headers ? `
                        <div class="collapsible-section">
                            <button class="collapsible-btn" onclick="toggleCollapsible(this)">
                                <i class="fas fa-chevron-right"></i> Headers
                            </button>
                            <div class="collapsible-content">
                                <div class="code-block">
                                    <pre><code class="language-json">${JSON.stringify(test.request.headers, null, 2)}</code></pre>
                                </div>
                            </div>
                        </div>
                        ` : ''}

                        ${test.request.body ? `
                        <div class="body-section">
                            <div class="body-header">
                                <h4>Request Body</h4>
                            </div>
                            <div class="body-container" data-test-index="${index}">
                                <div class="code-block">
                                    <button class="copy-btn" title="Copy to clipboard"><i class="fas fa-copy"></i></button>
                                    <pre class="json-content"><code class="language-json">${JSON.stringify(test.request.body, null, 2)}</code></pre>
                                </div>
                            </div>
                        </div>
                        ` : ''}
                    </div>

                    <!-- Response Details -->
                    <div class="section">
                        <div class="section-title">
                            <i class="fas fa-arrow-left"></i>
                            Response Details
                        </div>
                        
                        <div class="info-item">
                            <strong>Status:</strong>
                            <span class="status-badge ${test.passed ? 'status-success' : 'status-error'}">
                                ${test.status}
                            </span>
                        </div>
                        
                        <div class="info-item">
                            <strong>Size:</strong>
                            <span>${test.responseSize}</span>
                        </div>
                        
                        <div class="info-item">
                            <strong>Time:</strong>
                            <span>${test.responseTime}ms</span>
                        </div>

                        ${test.response.headers ? `
                        <div class="collapsible-section">
                            <button class="collapsible-btn" onclick="toggleCollapsible(this)">
                                <i class="fas fa-chevron-right"></i> Response Headers
                            </button>
                            <div class="collapsible-content">
                                <div class="code-block">
                                    <button class="copy-btn" title="Copy to clipboard"><i class="fas fa-copy"></i></button>
                                    <pre class="json-content"><code class="language-json">${JSON.stringify(test.response.headers, null, 2)}</code></pre>
                                </div>
                            </div>
                        </div>
                        ` : ''}

                        ${test.response.body ? `
                        <div class="body-section">
                            <div class="body-header">
                                <h4>Response Body</h4>
                            </div>
                            <div class="body-container" data-test-index="${index}">
                                <div class="code-block">
                                    <button class="copy-btn" title="Copy to clipboard"><i class="fas fa-copy"></i></button>
                                    <pre class="json-content"><code class="language-json">${typeof test.response.body === 'string' ? test.response.body : JSON.stringify(test.response.body, null, 2)}</code></pre>
                                </div>
                            </div>
                        </div>
                        ` : ''}
                    </div>
                </div>
            </div>
        </div>
    `;}).join('');
}

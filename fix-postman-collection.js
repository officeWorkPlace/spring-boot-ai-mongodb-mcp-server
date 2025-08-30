const fs = require('fs');
const path = require('path');

// Read the Postman collection
const collectionPath = path.join(__dirname, 'MongoDB-MCP-Server-Postman-Collection.json');
const collection = JSON.parse(fs.readFileSync(collectionPath, 'utf8'));

console.log('🔧 Fixing MongoDB MCP API Postman Collection...');

// Helper function to add authentication inheritance to a request
function addAuthInheritance(request) {
    if (!request.auth) {
        request.auth = {
            "type": "inherit"
        };
    }
}

// Helper function to encode JSON objects in URL parameters
function encodeJsonParams(query) {
    if (!query) return query;
    
    return query.map(param => {
        if (param.value === '{}') {
            param.value = '%7B%7D';
        } else if (param.value.startsWith('{') && param.value.endsWith('}')) {
            param.value = encodeURIComponent(param.value);
        }
        return param;
    });
}

// Helper function to add Content-Type header for POST/PUT requests
function addContentTypeHeader(request) {
    if (['POST', 'PUT', 'PATCH'].includes(request.method)) {
        if (!request.header) {
            request.header = [];
        }
        
        // Check if Content-Type already exists
        const hasContentType = request.header.some(h => 
            h.key.toLowerCase() === 'content-type'
        );
        
        if (!hasContentType) {
            if (request.body && request.body.mode === 'raw') {
                request.header.push({
                    "key": "Content-Type",
                    "value": "application/json"
                });
            } else {
                request.header.push({
                    "key": "Content-Type", 
                    "value": "application/x-www-form-urlencoded"
                });
            }
        }
    }
}

// Process all requests in the collection
function processFolder(folder) {
    if (folder.item) {
        folder.item.forEach(item => {
            if (item.request) {
                console.log(`Fixing: ${item.name}`);
                
                // Add authentication inheritance
                addAuthInheritance(item.request);
                
                // Add Content-Type header for write operations
                addContentTypeHeader(item.request);
                
                // Encode JSON parameters in URL
                if (item.request.url && item.request.url.query) {
                    item.request.url.query = encodeJsonParams(item.request.url.query);
                    
                    // Update the raw URL as well
                    if (item.request.url.raw) {
                        item.request.url.raw = item.request.url.raw
                            .replace(/filter=\{\}/g, 'filter=%7B%7D')
                            .replace(/projection=\{\}/g, 'projection=%7B%7D')
                            .replace(/sort=\{\}/g, 'sort=%7B%7D')
                            .replace(/options=\{\}/g, 'options=%7B%7D')
                            .replace(/query=\{\}/g, 'query=%7B%7D')
                            .replace(/update=\{\}/g, 'update=%7B%7D');
                    }
                }
            } else if (item.item) {
                // Recursively process nested folders
                processFolder(item);
            }
        });
    }
}

// Process the collection
if (collection.item) {
    collection.item.forEach(folder => {
        processFolder(folder);
    });
}

// Write the fixed collection back
fs.writeFileSync(collectionPath, JSON.stringify(collection, null, 2));

console.log('✅ Collection fixed successfully!');
console.log('🔧 Applied fixes:');
console.log('   • Added authentication inheritance to all requests');
console.log('   • Added proper Content-Type headers for POST/PUT operations');
console.log('   • URL-encoded JSON parameters ({} -> %7B%7D)');
console.log('   • Fixed raw URL strings with encoded parameters');
console.log('');
console.log('📝 Manual fixes still needed in Spring Boot:');
console.log('   • Add SecurityConfig.java to disable CSRF for API endpoints');
console.log('   • Restart the Spring Boot application');
console.log('');
console.log('🚀 Ready to run tests with: .\\run-api-tests.ps1');

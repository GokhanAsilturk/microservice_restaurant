# Start only Delivery API and its database
Write-Host "Starting Delivery API and Couchbase database..."
docker-compose up -d delivery-api-couchbasedb delivery-api

Write-Host "Delivery API started successfully!"
Write-Host "Access points:"
Write-Host "- Delivery API: http://localhost:8082"
Write-Host "- Couchbase Admin: http://localhost:8091"

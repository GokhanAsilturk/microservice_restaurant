# Individual Service Management Scripts

# Start Delivery API with its database
Write-Host "Starting Delivery API and Couchbase..."
Set-Location "delivery-api"
docker-compose up -d
Set-Location ".."

# Start Order API with ELK stack
Write-Host "Starting Order API with ELK stack..."
Set-Location "order_api"
docker-compose up -d
Set-Location ".."

# Start Restaurant API with PostgreSQL and pgAdmin
Write-Host "Starting Restaurant API with PostgreSQL and pgAdmin..."
Set-Location "restaurant-api"
docker-compose up -d
Set-Location ".."

Write-Host "All services started successfully!"
Write-Host "Access points:"
Write-Host "- Delivery API: http://localhost:8080"
Write-Host "- Order API: http://localhost:8081"
Write-Host "- Restaurant API: http://localhost:8082"
Write-Host "- Couchbase Admin: http://localhost:8091"
Write-Host "- Elasticsearch: http://localhost:9200"
Write-Host "- Kibana: http://localhost:5601"
Write-Host "- pgAdmin: http://localhost:5050"

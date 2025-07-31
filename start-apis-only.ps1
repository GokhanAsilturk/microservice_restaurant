# Start only APIs (assumes databases are already running)
Write-Host "Starting only API services (databases should be running)..."

Write-Host "Starting Order API..."
docker-compose up -d order-api

Write-Host "Starting Restaurant API..."
docker-compose up -d restaurant-api

Write-Host "Starting Delivery API..."
docker-compose up -d delivery-api

Write-Host "All API services started successfully!"
Write-Host ""
Write-Host "=== API ACCESS POINTS ==="
Write-Host "- Order API: http://localhost:8080"
Write-Host "- Restaurant API: http://localhost:8081"
Write-Host "- Delivery API: http://localhost:8082"

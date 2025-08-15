# Microservices Docker Management
Write-Host "Starting all microservices and databases..."

try {
    # Clean up existing containers first
    Write-Host "Cleaning up existing containers..."
    docker-compose down --volumes --remove-orphans

    # Pull latest images
    Write-Host "Pulling latest Docker images..."
    docker-compose pull

    # Check docker-compose syntax
    Write-Host "Validating docker-compose configuration..."
    docker-compose config --quiet
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Invalid docker-compose configuration!"
        exit 1
    }

    # Run go mod tidy first (for delivery-api)
    if (Test-Path "delivery-api") {
        Write-Host "Updating Go modules..."
        Push-Location "delivery-api"
        go mod tidy
        Pop-Location
    }

    # Start services in stages for better dependency management
    Write-Host "Starting database services first..."
    docker-compose up -d restaurant-api-postgres order-api-elasticsearch delivery-api-couchbasedb

    Write-Host "Waiting for databases to be ready..."
    Start-Sleep -Seconds 30

    Write-Host "Starting supporting services..."
    docker-compose up -d order-api-logstash order-api-kibana restaurant-api-pgadmin delivery-api-couchbase-init

    Write-Host "Waiting for supporting services..."
    Start-Sleep -Seconds 20

    Write-Host "Starting application services..."
    docker-compose up -d --build restaurant-api delivery-api order-api

    Write-Host "Waiting for all services to be ready..."
    Start-Sleep -Seconds 30

    # Check container status
    Write-Host "Checking container status..."
    docker-compose ps

    # Show logs for failed containers
    $failedContainers = docker-compose ps --filter "status=exited" --format "{{.Name}}"
    if ($failedContainers) {
        Write-Host "WARNING: Some containers failed to start:"
        foreach ($container in $failedContainers) {
            Write-Host "- $container"
        }
        Write-Host "Check logs with: docker-compose logs [container-name]"
    }

    Write-Host ""
    Write-Host "=== Service Status ==="
    docker-compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"

} catch {
    Write-Host "ERROR: $_"
    Write-Host "Checking container logs for troubleshooting..."
    docker-compose logs --tail=50
    exit 1
}

Write-Host ""
Write-Host "=== Access Points ==="
Write-Host "APIs:"
Write-Host "- Order API: http://localhost:8080"
Write-Host "- Restaurant API: http://localhost:8081"
Write-Host "- Delivery API: http://localhost:8082"
Write-Host ""
Write-Host "Databases & Management Tools:"
Write-Host "- Couchbase Admin: http://localhost:8091 (admin/password)"
Write-Host "- Elasticsearch: http://localhost:9200"
Write-Host "- Kibana: http://localhost:5601"
Write-Host "- pgAdmin: http://localhost:5050 (admin@admin.com/admin)"
Write-Host ""
Write-Host "Database Connections:"
Write-Host "- PostgreSQL: localhost:5432 (postgres/1234)"
Write-Host "- Logstash: localhost:5000"
Write-Host ""
Write-Host "=== Useful Commands ==="
Write-Host "- Check logs: docker-compose logs -f [service-name]"
Write-Host "- Stop all services: docker-compose down"
Write-Host "- Restart service: docker-compose restart [service-name]"

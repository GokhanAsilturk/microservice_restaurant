# Microservices Docker Management
Write-Host "Starting all microservices with their databases..."
Write-Host "Running docker-compose up -d..."

try {
    # Önce mevcut container'ları temizle
    Write-Host "Cleaning up existing containers..."
    docker-compose down

    # Docker-compose syntax'ını kontrol et
    Write-Host "Validating docker-compose configuration..."
    docker-compose config --quiet
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Docker-compose configuration is invalid!"
        exit 1
    }

    # Servisleri başlat
    Write-Host "Starting services..."
    docker-compose up -d --build

    Write-Host "Waiting for containers to initialize..."
    Start-Sleep -Seconds 10

    # Container durumlarını kontrol et
    Write-Host "`n=== CONTAINER STATUS ==="
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

    # Başarısız olan container'ları göster
    Write-Host "`n=== FAILED CONTAINERS ==="
    $failedContainers = docker ps -a --filter "status=exited" --format "{{.Names}}"
    if ($failedContainers) {
        docker ps -a --filter "status=exited" --format "table {{.Names}}\t{{.Status}}\t{{.ExitCode}}"
        Write-Host "`n=== FAILED CONTAINER LOGS ==="
        foreach ($container in $failedContainers) {
            Write-Host "`n--- Logs for $container ---"
            docker logs $container --tail 20

            # Özel olarak couchbase init container için ek bilgi
            if ($container -eq "delivery-api-couchbase-init") {
                Write-Host "`n--- Couchbase Init Container Extra Info ---"
                Write-Host "Checking if Couchbase main container is running..."
                docker ps --filter "name=delivery-api-couchbasedb" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

                Write-Host "`nChecking Couchbase health endpoint..."
                try {
                    $couchbaseHealth = docker exec delivery-api-couchbasedb curl -f -s http://localhost:8091 2>$null
                    if ($LASTEXITCODE -eq 0) {
                        Write-Host "Couchbase endpoint is accessible"
                    } else {
                        Write-Host "Couchbase endpoint is NOT accessible"
                    }
                } catch {
                    Write-Host "Failed to check Couchbase endpoint: $_"
                }

                Write-Host "`nChecking Couchbase cluster status..."
                try {
                    docker exec delivery-api-couchbasedb curl -s http://localhost:8091/pools/default 2>$null | Select-String -Pattern '"name":"default"' -Quiet
                    if ($?) {
                        Write-Host "Couchbase cluster appears to be initialized"
                    } else {
                        Write-Host "Couchbase cluster may not be initialized"
                    }
                } catch {
                    Write-Host "Failed to check cluster status: $_"
                }
            }

            # Özel olarak delivery-api container için ek bilgi
            if ($container -eq "delivery-api") {
                Write-Host "`n--- Delivery API Container Extra Info ---"
                Write-Host "Checking Go application build logs..."
                try {
                    Write-Host "Build context and Go module info:"
                    docker-compose build delivery-api --no-cache
                } catch {
                    Write-Host "Failed to rebuild delivery-api: $_"
                }

                Write-Host "`nChecking dependencies status..."
                try {
                    docker run --rm -v ${PWD}/delivery-api:/app -w /app golang:1.23-alpine go mod verify
                } catch {
                    Write-Host "Failed to verify Go modules: $_"
                }
            }
        }
    } else {
        Write-Host "No failed containers found."
    }

    # Sağlık durumu kontrolleri
    Write-Host "`n=== HEALTH CHECK STATUS ==="
    docker ps --format "table {{.Names}}\t{{.Status}}" | Where-Object { $_ -match "health" }

    Write-Host "`nAll services startup completed!"
}
catch {
    Write-Host "Error occurred: $_"
    Write-Host "Checking for any errors in docker-compose..."
    docker-compose config
}

Write-Host ""
Write-Host "=== ACCESS POINTS ==="
Write-Host "APIs:"
Write-Host "- Order API: http://localhost:8080"
Write-Host "- Restaurant API: http://localhost:8081"
Write-Host "- Delivery API: http://localhost:8082"
Write-Host ""
Write-Host "Databases & Admin Tools:"
Write-Host "- Couchbase Admin: http://localhost:8091 (admin/password)"
Write-Host "- Elasticsearch: http://localhost:9200"
Write-Host "- Kibana: http://localhost:5601"
Write-Host "- pgAdmin: http://localhost:5050 (admin@admin.com/admin)"
Write-Host ""
Write-Host "Database Connections:"
Write-Host "- PostgreSQL: localhost:5432 (postgres/1234)"
Write-Host "- Logstash: localhost:5000"

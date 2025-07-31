# Stop all microservices and their databases
Write-Host "Stopping all microservices and databases..."
docker-compose down

Write-Host "All services stopped successfully!"
Write-Host "Volumes are preserved. Use 'docker-compose down -v' to remove volumes as well."

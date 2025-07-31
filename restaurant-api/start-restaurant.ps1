# Start only Restaurant API with PostgreSQL and pgAdmin
Write-Host "Starting Restaurant API with PostgreSQL and pgAdmin..."
docker-compose up -d restaurant-api-postgres restaurant-api-pgadmin restaurant-api

Write-Host "Restaurant API started successfully!"
Write-Host "Access points:"
Write-Host "- Restaurant API: http://localhost:8081"
Write-Host "- pgAdmin: http://localhost:5050 (admin@admin.com / admin)"
Write-Host "- PostgreSQL: localhost:5432"

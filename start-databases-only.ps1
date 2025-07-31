# Start only databases without APIs
Write-Host "Starting only databases..."

Write-Host "Starting Couchbase for Delivery API..."
docker-compose up -d delivery-api-couchbasedb

Write-Host "Starting ELK stack for Order API..."
docker-compose up -d order-api-elasticsearch order-api-logstash order-api-kibana

Write-Host "Starting PostgreSQL and pgAdmin for Restaurant API..."
docker-compose up -d restaurant-api-postgres restaurant-api-pgadmin

Write-Host "All databases started successfully!"
Write-Host ""
Write-Host "=== DATABASE ACCESS POINTS ==="
Write-Host "- Couchbase Admin: http://localhost:8091 (admin/password)"
Write-Host "- Elasticsearch: http://localhost:9200"
Write-Host "- Kibana: http://localhost:5601"
Write-Host "- pgAdmin: http://localhost:5050 (admin@admin.com/admin)"
Write-Host "- PostgreSQL: localhost:5432 (postgres/1234)"

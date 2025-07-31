# Start only Order API and its ELK stack
Write-Host "Starting Order API with Elasticsearch, Logstash and Kibana..."
docker-compose up -d order-api-elasticsearch order-api-logstash order-api-kibana order-api

Write-Host "Order API started successfully!"
Write-Host "Access points:"
Write-Host "- Order API: http://localhost:8080"
Write-Host "- Elasticsearch: http://localhost:9200"
Write-Host "- Kibana: http://localhost:5601"
Write-Host "- Logstash: http://localhost:9600"

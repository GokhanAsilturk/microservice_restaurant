# Order API - Mikroservis Test Projesi

Sipariş yönetimi için geliştirilmiş Java Spring Boot tabanlı mikroservis uygulaması. Sipariş oluşturma, takip ve durum yönetimi sağlar. ELK Stack entegrasyonu ile gelişmiş log analizi sunar.

## 🛠 Teknolojiler

- **Java 17**
- **Spring Boot 3.3.0**
- **Spring Data JPA**
- **H2 Database** (In-memory)
- **Maven**
- **JUnit 5**
- **JaCoCo** (Test Coverage)
- **Docker**
- **Spring Boot Actuator** (Health Check & Metrics)
- **ELK Stack** (Elasticsearch + Logstash + Kibana)
- **Logback** (Logging Framework)

## 📋 Gereksinimler

- Java 17 veya üzeri
- Maven 3.6 veya üzeri
- Docker ve Docker Compose (ELK Stack için gerekli)
- Git

## 🚀 Kurulum ve Çalıştırma

### 1. Proje Klonlama ve Bağımlılık İndirme

```powershell
cd order_api
.\mvnw.cmd clean install
```

### 2. ELK Stack'i Başlatma (Önerilen)

Order API'nin log analizi özelliklerini kullanmak için önce ELK Stack'i başlatın:

```powershell
# Ana dizinden ELK Stack'i başlat
docker-compose up -d elasticsearch logstash kibana
```

### 3. Uygulamayı Başlatma

#### PowerShell Script ile (Önerilen):
```powershell
.\start-order.ps1
```

#### Geliştirme Ortamında (Maven ile):
```powershell
.\mvnw.cmd spring-boot:run
```

#### JAR dosyası ile:
```powershell
.\mvnw.cmd clean package
java -jar target/order-api-1.0.0.jar
```

#### Docker ile:
```powershell
# Dockerfile kullanarak
docker build -t order-api .
docker run -p 8080:8080 order-api

# Docker Compose ile (ELK Stack dahil)
docker-compose up -d
```

### 4. Uygulamanın Çalıştığını Doğrulama

Uygulama başarıyla başladıktan sonra:

- **Ana API**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **H2 Console**: http://localhost:8080/h2-console
- **Actuator Metrics**: http://localhost:8080/actuator/metrics

### 5. ELK Stack Erişimi

- **Elasticsearch**: http://localhost:9200
- **Kibana Dashboard**: http://localhost:5601
- **Logstash**: http://localhost:5000 (TCP input)

## 📚 API Endpointleri

### Sipariş İşlemleri

| Method | Endpoint | Açıklama | Request Body |
|--------|----------|----------|--------------|
| POST | `/api/orders` | Yeni sipariş oluşturur | Order JSON |
| GET | `/api/orders` | Tüm siparişleri listeler | - |
| GET | `/api/orders/{id}` | Belirli siparişi getirir | - |
| PUT | `/api/orders/{id}` | Sipariş günceller | Order JSON |
| PUT | `/api/orders/{id}/status` | Sipariş durumunu günceller | Status JSON |
| DELETE | `/api/orders/{id}` | Sipariş siler | - |

### Health & Monitoring

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/actuator/health` | Uygulama sağlık durumu |
| GET | `/actuator/metrics` | Uygulama metrikleri |
| GET | `/actuator/info` | Uygulama bilgileri |
| GET | `/actuator/loggers` | Log seviyesi yönetimi |

## 📄 JSON Şemaları

### Order (Sipariş)
```json
{
  "id": "order-123",
  "customerId": "customer-456",
  "restaurantId": "restaurant-789",
  "items": [
    {
      "productId": "1",
      "productName": "Margherita Pizza",
      "quantity": 2,
      "price": 45.90,
      "totalPrice": 91.80
    }
  ],
  "totalAmount": 91.80,
  "status": "PENDING",
  "deliveryAddress": "Atatürk Cad. No:123, Ankara",
  "notes": "Ekstra baharatlı",
  "createdAt": "2024-01-08T10:00:00Z",
  "updatedAt": "2024-01-08T10:00:00Z"
}
```

### Order Status Update
```json
{
  "status": "PREPARING"
}
```

### Order Status Enum
- `PENDING` - Beklemede
- `CONFIRMED` - Onaylandı
- `PREPARING` - Hazırlanıyor
- `READY` - Hazır
- `DELIVERED` - Teslim Edildi
- `CANCELLED` - İptal Edildi

## 🧪 Test Çalıştırma

### Tüm Testleri Çalıştırma
```powershell
.\mvnw.cmd test
```

### Test Coverage Raporu Oluşturma
```powershell
.\mvnw.cmd test jacoco:report
```

Test coverage raporu: `target/site/jacoco/index.html`

### Belirli Test Sınıfını Çalıştırma
```powershell
.\mvnw.cmd test -Dtest=OrderControllerTest
```

### Test Kategorileri

- **Unit Tests**: Controller, Service, Repository katmanları
- **Integration Tests**: Database ve ELK Stack entegrasyonu
- **API Tests**: REST endpoint testleri
- **Performance Tests**: Yük ve stres testleri

## 🗄️ Veritabanı

### H2 Database Console

- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:orderdb`
- **Kullanıcı**: `sa`
- **Şifre**: (boş)

### Örnek Veriler

Uygulama başladığında otomatik olarak örnek siparişler yüklenir.

## 📊 ELK Stack Entegrasyonu

### Elasticsearch

Order API tüm loglarını Elasticsearch'e gönderir:

```bash
# Order loglarını arama
curl "http://localhost:9200/order-api-*/_search?q=level:INFO&size=10"

# Hata loglarını arama
curl "http://localhost:9200/order-api-*/_search?q=level:ERROR&size=10"

# Belirli sipariş ID'si ile arama
curl "http://localhost:9200/order-api-*/_search?q=orderId:order-123"
```

### Logstash Konfigürasyonu

Logstash konfigürasyonu: `logstash/config/logstash.conf`

```ruby
input {
  tcp {
    port => 5000
    codec => json_lines
  }
}

filter {
  # JSON parsing ve alan ekleme
  if [logger_name] =~ "OrderController" {
    mutate {
      add_tag => ["order-controller"]
    }
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "order-api-%{+YYYY.MM.dd}"
  }
}
```

### Kibana Dashboard

Kibana'da önceden yapılandırılmış dashboard'lar:

1. **Order API Logs Overview**
   - Log seviyesi dağılımı
   - Zaman bazlı log analizi
   - Hata oranları

2. **Order Performance Metrics**
   - API response time'ları
   - İstek sayıları
   - Başarılı/başarısız sipariş oranları

3. **Error Monitoring**
   - Exception stack trace'leri
   - Hata trend analizi
   - Alert konfigürasyonları

Kibana'ya erişim: http://localhost:5601

## 📝 Örnek API Çağrıları

### 1. Yeni Sipariş Oluştur
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-123",
    "restaurantId": "restaurant-456",
    "items": [
      {
        "productId": "1",
        "productName": "Margherita Pizza",
        "quantity": 2,
        "price": 45.90
      }
    ],
    "deliveryAddress": "Atatürk Cad. No:123, Ankara",
    "notes": "Kapıda ödeme"
  }'
```

### 2. Tüm Siparişleri Listele
```bash
curl -X GET http://localhost:8080/api/orders
```

### 3. Sipariş Durumunu Güncelle
```bash
curl -X PUT http://localhost:8080/api/orders/order-123/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "PREPARING"
  }'
```

### 4. Elasticsearch'te Log Arama
```bash
# Son 1 saatteki siparişleri ara
curl "http://localhost:9200/order-api-*/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "bool": {
        "must": [
          {"match": {"message": "Order created"}},
          {"range": {"@timestamp": {"gte": "now-1h"}}}
        ]
      }
    }
  }'
```

## 🐳 Docker Kullanımı

### Sadece Order API
```powershell
docker build -t order-api .
docker run -p 8080:8080 --name order-api-container order-api
```

### ELK Stack ile Birlikte
```powershell
# Tüm servisleri başlat (ELK Stack dahil)
docker-compose up -d

# Sadece Order API ve ELK Stack
docker-compose up -d elasticsearch logstash kibana order-api
```

### Container Yönetimi
```powershell
# Container durumunu kontrol et
docker-compose ps

# Order API loglarını izle
docker-compose logs -f order-api

# Elasticsearch durumunu kontrol et
curl http://localhost:9200/_cluster/health
```

## 🔍 Monitoring ve Debugging

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Metrics
```bash
curl http://localhost:8080/actuator/metrics/http.server.requests
```

### Log Seviyeleri Yönetimi
```bash
# DEBUG seviyesine geç
curl -X POST http://localhost:8080/actuator/loggers/com.example.orderapi \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```

### Real-time Log İzleme

#### Docker Logs
```powershell
docker-compose logs -f order-api
```

#### Kibana Discover
1. http://localhost:5601 adresine gidin
2. "Discover" sekmesini açın
3. Index pattern: `order-api-*`
4. Real-time log akışını izleyin

## 🛠️ Geliştirme

### IDE Kurulumu

**IntelliJ IDEA (Önerilen):**
1. Projeyi açın
2. Maven projesini import edin
3. JDK 17'yi seçin
4. Lombok plugin'ini aktif edin

### Debug Modunda Çalıştırma
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```

### Environment Variables
```powershell
# ELK Stack konfigürasyonu
$env:ELASTICSEARCH_HOST="localhost"
$env:ELASTICSEARCH_PORT="9200"
$env:LOGSTASH_HOST="localhost"
$env:LOGSTASH_PORT="5000"

# Veritabanı konfigürasyonu
$env:SPRING_DATASOURCE_URL="jdbc:h2:mem:orderdb"
$env:SPRING_PROFILES_ACTIVE="dev"
$env:SERVER_PORT="8080"

# Uygulamayı başlat
.\mvnw.cmd spring-boot:run
```

## 📊 Test Coverage Hedefleri

- **Minimum Coverage**: %85
- **Controller Coverage**: %95+
- **Service Coverage**: %90+
- **Repository Coverage**: %80+

### Coverage Raporu
```powershell
# Test coverage raporu oluştur
.\mvnw.cmd test jacoco:report

# Raporu görüntüle
start target/site/jacoco/index.html
```

## 🔧 Sorun Giderme

### Port Çakışması
```powershell
# Port 8080 kullanımını kontrol et
netstat -an | findstr 8080

# Farklı port ile başlat
.\mvnw.cmd spring-boot:run -Dserver.port=8083
```

### ELK Stack Sorunları
```powershell
# Elasticsearch durumunu kontrol et
curl http://localhost:9200/_cluster/health

# Logstash pipeline'ını kontrol et
curl http://localhost:9600/_node/stats/pipelines

# Kibana'nın hazır olup olmadığını kontrol et
curl http://localhost:5601/api/status
```

### Log Gönderimi Sorunları
```powershell
# Logstash TCP bağlantısını test et
Test-NetConnection -ComputerName localhost -Port 5000

# Application.yml'de Logstash konfigürasyonunu kontrol et
```

## 🔄 Mikroservis Entegrasyonu

### Restaurant API Entegrasyonu
Order API, sipariş oluştururken Restaurant API'den stok kontrolü yapar:

```bash
# Stok kontrolü örneği
curl -X POST http://localhost:8081/api/stock/check \
  -H "Content-Type: application/json" \
  -d '{
    "items": [{"productId": 1, "requiredQuantity": 2}]
  }'
```

### Delivery API Entegrasyonu
Sipariş onaylandıktan sonra Delivery API'ye teslimat talebi gönderilir:

```bash
# Teslimat oluşturma örneği
curl -X POST http://localhost:8082/api/delivery \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order-123",
    "customerId": "customer-456",
    "deliveryAddress": {"street": "Test St", "city": "Ankara"}
  }'
```

## 📈 Performance Monitoring

### Elasticsearch Query Performance
```bash
# Slow query'leri bul
curl "http://localhost:9200/order-api-*/_search?q=response_time:>1000"

# API endpoint performance
curl "http://localhost:9200/order-api-*/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "aggs": {
      "avg_response_time": {
        "avg": {"field": "response_time"}
      }
    }
  }'
```

### Actuator Metrics
```bash
# HTTP request metrics
curl http://localhost:8080/actuator/metrics/http.server.requests

# JVM memory usage
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Database connection pool
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active
```

## 📦 Dağıtım

### Production Build
```powershell
.\mvnw.cmd clean package -Pprod
```

### Production Deployment
```powershell
# Production profili ile çalıştır
java -jar target/order-api-1.0.0.jar --spring.profiles.active=prod

# Environment variables ile
$env:SPRING_PROFILES_ACTIVE="prod"
$env:ELASTICSEARCH_HOST="prod-elasticsearch.company.com"
java -jar target/order-api-1.0.0.jar
```

## 📋 Yapılacaklar (TODO)

- [ ] PostgreSQL entegrasyonu
- [ ] Redis cache implementasyonu
- [ ] Message Queue entegrasyonu (RabbitMQ/Kafka)
- [ ] API rate limiting
- [ ] Authentication & Authorization (JWT)
- [ ] Swagger/OpenAPI dokümantasyonu
- [ ] Prometheus metrics
- [ ] Circuit breaker pattern
- [ ] Distributed tracing (Zipkin/Jaeger)

## 📞 Destek

Sorun yaşadığınızda:
1. Bu README dosyasını kontrol edin
2. ELK Stack loglarını inceleyin
3. Health check endpoint'ini kontrol edin
4. Test coverage raporuna bakın
5. Kibana dashboard'larını kontrol edin

## 📜 Lisans

Bu proje eğitim amaçlıdır ve MIT lisansı altında lisanslanmıştır.

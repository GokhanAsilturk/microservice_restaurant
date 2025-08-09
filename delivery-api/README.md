# Delivery API - Mikroservis Test Projesi

Teslimat yönetimi için geliştirilmiş Go tabanlı mikroservis uygulaması. Teslimat oluşturma, kurye atama ve durum takibi sağlar. Couchbase NoSQL veritabanı ve Swagger dokümantasyonu içerir.

## 🛠 Teknolojiler

- **Go 1.23**
- **Gin Framework** (HTTP Router)
- **Couchbase** (NoSQL Database)
- **Swagger/OpenAPI 3.0** (API Dokümantasyonu)
- **Docker**
- **Testify** (Test Framework)
- **Go Modules** (Dependency Management)

## 📋 Gereksinimler

- Go 1.23 veya üzeri
- Docker ve Docker Compose (Couchbase için gerekli)
- Git

## 🚀 Kurulum ve Çalıştırma

### 1. Proje Klonlama ve Bağımlılık İndirme

```powershell
cd delivery-api
go mod download
go mod tidy
```

### 2. Couchbase'i Başlatma (Önerilen)

Delivery API'nin veritabanı özelliklerini kullanmak için önce Couchbase'i başlatın:

```powershell
# Ana dizinden Couchbase'i başlat
docker-compose up -d couchbase

# Couchbase kurulumunu bekleyin (yaklaşık 30 saniye)
timeout /t 30

# Bucket ve kullanıcı oluşturma script'ini çalıştırın
.\setup-couchbase.ps1
```

### 3. Uygulamayı Başlatma

#### PowerShell Script ile (Önerilen):
```powershell
.\start-delivery.ps1
```

#### Go ile doğrudan:
```powershell
go run main.go
```

#### Build edip çalıştırma:
```powershell
go build -o delivery-api.exe
.\delivery-api.exe
```

#### Docker ile:
```powershell
# Dockerfile kullanarak
docker build -t delivery-api .
docker run -p 8082:8082 delivery-api

# Docker Compose ile (Couchbase dahil)
docker-compose up -d
```

### 4. Uygulamanın Çalıştığını Doğrulama

Uygulama başarıyla başladıktan sonra:

- **Ana API**: http://localhost:8082
- **Health Check**: http://localhost:8082/health
- **Swagger UI**: http://localhost:8082/swagger/index.html
- **Swagger JSON**: http://localhost:8082/swagger/doc.json

### 5. Couchbase Erişimi

- **Couchbase Console**: http://localhost:8091
- **Kullanıcı**: `Administrator`
- **Şifre**: `password`
- **Bucket**: `delivery`

## 📚 API Endpointleri

### Teslimat İşlemleri

| Method | Endpoint | Açıklama | Request Body |
|--------|----------|----------|--------------|
| POST | `/api/delivery` | Yeni teslimat oluşturur | Delivery JSON |
| GET | `/api/delivery/{id}` | Teslimat durumunu sorgular | - |
| PUT | `/api/delivery/{id}/status` | Teslimat durumunu günceller | Status JSON |
| GET | `/api/delivery/order/{orderId}` | Sipariş ID'sine göre teslimat getirir | - |
| GET | `/api/delivery/customer/{customerId}` | Müşteri teslimatlarını listeler | - |

### Health & Monitoring

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/health` | Uygulama sağlık durumu |
| GET | `/ping` | Basit ping kontrolü |

### Dokümantasyon

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/swagger/index.html` | Swagger UI |
| GET | `/swagger/doc.json` | OpenAPI JSON |

## 📄 JSON Şemaları

### Delivery (Teslimat)
```json
{
  "id": "delivery-123",
  "orderId": "order-456",
  "customerId": "customer-789",
  "restaurantId": "restaurant-001",
  "deliveryAddress": {
    "street": "Atatürk Caddesi",
    "number": "123",
    "district": "Çankaya",
    "city": "Ankara",
    "postalCode": "06100",
    "country": "Turkey"
  },
  "customerPhone": "+90 555 123 45 67",
  "deliveryNotes": "3. kat, daire 8",
  "status": "PENDING",
  "driverName": "",
  "driverPhone": "",
  "estimatedDeliveryTime": "2024-01-08T14:30:00Z",
  "actualDeliveryTime": null,
  "createdAt": "2024-01-08T13:00:00Z",
  "updatedAt": "2024-01-08T13:00:00Z"
}
```

### Delivery Status Update
```json
{
  "status": "IN_TRANSIT",
  "notes": "Kurye yola çıktı",
  "driverName": "Mehmet Yılmaz",
  "driverPhone": "+90 555 987 65 43"
}
```

### Delivery Status Enum
- `PENDING` - Beklemede
- `ASSIGNED` - Kurye Atandı
- `PICKED_UP` - Alındı
- `IN_TRANSIT` - Yolda
- `DELIVERED` - Teslim Edildi
- `FAILED` - Teslimat Başarısız
- `CANCELLED` - İptal Edildi

## 🧪 Test Çalıştırma

### Tüm Testleri Çalıştırma
```powershell
go test ./...
```

### Test Coverage ile
```powershell
go test -cover ./...
```

### Detaylı Test Coverage Raporu
```powershell
go test -coverprofile=coverage.out ./...
go tool cover -html=coverage.out -o coverage.html
start coverage.html
```

### Belirli Paket Testini Çalıştırma
```powershell
go test ./controllers -v
go test ./models -v
```

### Benchmark Testleri
```powershell
go test -bench=. ./...
```

### Test Kategorileri

- **Unit Tests**: Controller, Model katmanları
- **Integration Tests**: Couchbase entegrasyonu
- **API Tests**: HTTP endpoint testleri
- **Benchmark Tests**: Performance testleri

## 🗄️ Veritabanı (Couchbase)

### Couchbase Console

- **URL**: http://localhost:8091
- **Kullanıcı**: `Administrator`
- **Şifre**: `password`
- **Bucket**: `delivery`
- **Scope**: `_default`
- **Collection**: `_default`

### Couchbase Kurulumu

Otomatik kurulum script'i:
```powershell
.\setup-couchbase.ps1
```

Manuel kurulum:
```powershell
# Bucket oluştur
curl -X POST http://localhost:8091/pools/default/buckets \
  -u Administrator:password \
  -d name=delivery \
  -d bucketType=couchbase \
  -d ramQuotaMB=256

# Index oluştur
curl -X POST http://localhost:8093/query/service \
  -u Administrator:password \
  -d 'statement=CREATE INDEX idx_order_id ON delivery(orderId)'
```

### Örnek Veriler

Uygulama başladığında otomatik olarak örnek teslimat kayıtları yüklenir.

## 📝 Örnek API Çağrıları

### 1. Yeni Teslimat Oluştur
```bash
curl -X POST http://localhost:8082/api/delivery \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order-123",
    "customerId": "customer-456",
    "restaurantId": "restaurant-789",
    "deliveryAddress": {
      "street": "Atatürk Caddesi",
      "number": "123",
      "district": "Çankaya",
      "city": "Ankara",
      "postalCode": "06100",
      "country": "Turkey"
    },
    "customerPhone": "+90 555 123 45 67",
    "deliveryNotes": "3. kat, daire 8",
    "estimatedDeliveryTime": "2024-01-08T14:30:00Z"
  }'
```

### 2. Teslimat Durumunu Sorgula
```bash
curl -X GET http://localhost:8082/api/delivery/delivery-123
```

### 3. Teslimat Durumunu Güncelle
```bash
curl -X PUT http://localhost:8082/api/delivery/delivery-123/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "IN_TRANSIT",
    "notes": "Kurye yola çıktı",
    "driverName": "Mehmet Yılmaz",
    "driverPhone": "+90 555 987 65 43"
  }'
```

### 4. Sipariş ID'sine Göre Teslimat Getir
```bash
curl -X GET http://localhost:8082/api/delivery/order/order-123
```

### 5. Müşteri Teslimatlarını Listele
```bash
curl -X GET http://localhost:8082/api/delivery/customer/customer-456
```

## 🐳 Docker Kullanımı

### Sadece Delivery API
```powershell
docker build -t delivery-api .
docker run -p 8082:8082 --name delivery-api-container delivery-api
```

### Couchbase ile Birlikte
```powershell
# Tüm servisleri başlat (Couchbase dahil)
docker-compose up -d

# Sadece Delivery API ve Couchbase
docker-compose up -d couchbase delivery-api
```

### Container Yönetimi
```powershell
# Container durumunu kontrol et
docker-compose ps

# Delivery API loglarını izle
docker-compose logs -f delivery-api

# Couchbase durumını kontrol et
curl http://localhost:8091/pools/default
```

## 🔍 Monitoring ve Debugging

### Health Check
```bash
curl http://localhost:8082/health
```

Yanıt:
```json
{
  "status": "UP",
  "timestamp": "2024-01-08T10:00:00Z",
  "database": "connected",
  "version": "1.0.0"
}
```

### Ping Test
```bash
curl http://localhost:8082/ping
```

### Swagger UI

Swagger UI'da tüm API endpoint'lerini test edebilirsiniz:
http://localhost:8082/swagger/index.html

### Couchbase Monitoring

```bash
# Cluster durumu
curl -u Administrator:password http://localhost:8091/pools/default

# Bucket istatistikleri
curl -u Administrator:password http://localhost:8091/pools/default/buckets/delivery/stats

# Query service durumu
curl -u Administrator:password http://localhost:8093/admin/ping
```

## 🛠️ Geliştirme

### IDE Kurulumu

**Visual Studio Code (Önerilen):**
1. Go extension yükleyin
2. Projeyi açın
3. Go: Install/Update Tools komutu çalıştırın

**GoLand:**
1. Projeyi açın
2. Go SDK'sını ayarlayın
3. Modules'i etkinleştirin

### Debug Modunda Çalıştırma
```powershell
# Delve debugger ile
go install github.com/go-delve/delve/cmd/dlv@latest
dlv debug main.go
```

### Environment Variables
```powershell
# Couchbase konfigürasyonu
$env:COUCHBASE_HOST="localhost"
$env:COUCHBASE_USERNAME="Administrator"
$env:COUCHBASE_PASSWORD="password"
$env:COUCHBASE_BUCKET="delivery"

# Server konfigürasyonu
$env:PORT="8082"
$env:GIN_MODE="debug"

# Uygulamayı başlat
go run main.go
```

### Live Reload (Air)
```powershell
# Air tool'u yükle
go install github.com/cosmtrek/air@latest

# Live reload ile çalıştır
air
```

## 📊 Test Coverage Hedefleri

- **Minimum Coverage**: %80
- **Controller Coverage**: %90+
- **Model Coverage**: %85+
- **Database Coverage**: %75+

### Coverage Raporu Görüntüleme
```powershell
go test -coverprofile=coverage.out ./...
go tool cover -html=coverage.out
```

## 🔧 Sorun Giderme

### Port Çakışması
```powershell
# Port 8082 kullanımını kontrol et
netstat -an | findstr 8082

# Farklı port ile başlat
$env:PORT="8083"
go run main.go
```

### Couchbase Bağlantı Sorunları
```powershell
# Couchbase durumunu kontrol et
curl http://localhost:8091/pools/default

# Bucket varlığını kontrol et
curl -u Administrator:password http://localhost:8091/pools/default/buckets/delivery

# Network bağlantısını test et
Test-NetConnection -ComputerName localhost -Port 8091
```

### Go Module Sorunları
```powershell
# Module cache temizle
go clean -modcache

# Dependencies güncelle
go mod download
go mod tidy

# Vendor klasörü ile çalış
go mod vendor
go run -mod=vendor main.go
```

### Swagger Dokümantasyon Sorunları
```powershell
# Swagger docs yeniden oluştur
swag init

# Swagger dependency kontrol et
go mod tidy
```

## 🔄 Mikroservis Entegrasyonu

### Order API Entegrasyonu
Order API'den teslimat talebi geldiğinde otomatik teslimat oluşturulur:

```bash
# Order API'den gelen örnek istek
curl -X POST http://localhost:8082/api/delivery \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order-from-order-api",
    "customerId": "customer-123",
    "deliveryAddress": {"street": "Test St", "city": "Ankara"}
  }'
```

### Restaurant API Entegrasyonu
Teslimat durumu güncellendiğinde Restaurant API'ye bilgi gönderilebilir.

## 📈 Performance Monitoring

### Couchbase Query Performance
```bash
# Slow query'leri bul
curl -u Administrator:password http://localhost:8093/admin/stats

# Query execution plan
curl -X POST http://localhost:8093/query/service \
  -u Administrator:password \
  -d 'statement=EXPLAIN SELECT * FROM delivery WHERE orderId = "order-123"'
```

### API Performance Metrics
```bash
# Response time monitoring
curl -w "@curl-format.txt" -o /dev/null -s http://localhost:8082/health

# Concurrent request test
for i in {1..10}; do curl http://localhost:8082/ping & done
```

## 📦 Dağıtım

### Production Build
```powershell
# Static binary oluştur
$env:CGO_ENABLED="0"
$env:GOOS="linux"
go build -a -installsuffix cgo -o delivery-api main.go
```

### Production Deployment
```powershell
# Production environment variables
$env:GIN_MODE="release"
$env:COUCHBASE_HOST="prod-couchbase.company.com"
$env:PORT="8082"

# Binary'yi çalıştır
.\delivery-api.exe
```

### Docker Production
```dockerfile
# Multi-stage build örneği
FROM golang:1.23-alpine AS builder
WORKDIR /app
COPY . .
RUN go mod download
RUN CGO_ENABLED=0 go build -o delivery-api main.go

FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /root/
COPY --from=builder /app/delivery-api .
EXPOSE 8082
CMD ["./delivery-api"]
```

## 📋 Yapılacaklar (TODO)

- [ ] PostgreSQL dual database support
- [ ] Redis cache implementasyonu
- [ ] Message Queue entegrasyonu (RabbitMQ)
- [ ] Authentication & Authorization (JWT)
- [ ] Prometheus metrics
- [ ] Circuit breaker pattern
- [ ] Rate limiting
- [ ] Distributed tracing
- [ ] WebSocket real-time tracking

## 📞 Destek

Sorun yaşadığınızda:
1. Bu README dosyasını kontrol edin
2. Couchbase loglarını inceleyin
3. Health check endpoint'ini kontrol edin
4. Test coverage raporuna bakın
5. Swagger dokümantasyonunu kontrol edin

## 📜 Lisans

Bu proje eğitim amaçlıdır ve MIT lisansı altında lisanslanmıştır.

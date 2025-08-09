# Restaurant API - Mikroservis Test Projesi

Restoran menüsü ve stok yönetimi için geliştirilmiş Spring Boot ve Kotlin tabanlı mikroservis uygulaması. Ürün katalogunu yönetir ve stok kontrolü sağlar.

## 🛠 Teknolojiler

- **Java 17**
- **Kotlin 1.8.21**
- **Spring Boot 3.1.0**
- **Spring Data JPA**
- **H2 Database** (In-memory)
- **Maven** 
- **JUnit 5** 
- **JaCoCo** (Test Coverage)
- **Docker** 
- **Spring Boot Actuator** (Health Check & Metrics)

## 📋 Gereksinimler

- Java 17 veya üzeri
- Maven 3.6 veya üzeri
- Docker ve Docker Compose (opsiyonel)
- Git

## 🚀 Kurulum ve Çalıştırma

### 1. Proje Klonlama ve Bağımlılık İndirme

```bash
cd restaurant-api
./mvnw clean install
```

Windows'ta:
```powershell
cd restaurant-api
.\mvnw.cmd clean install
```

### 2. Uygulamayı Başlatma

#### Geliştirme Ortamında (Maven ile):
```bash
./mvnw spring-boot:run
```

Windows'ta:
```powershell
.\mvnw.cmd spring-boot:run
```

#### JAR dosyası ile:
```bash
./mvnw clean package
java -jar target/restaurant-api-1.0.0.jar
```

Windows'ta:
```powershell
.\mvnw.cmd clean package
java -jar target/restaurant-api-1.0.0.jar
```

#### PowerShell Script ile (Önerilen):
```powershell
.\start-restaurant.ps1
```

#### Docker ile:
```bash
# Dockerfile kullanarak
docker build -t restaurant-api .
docker run -p 8081:8081 restaurant-api

# Docker Compose ile
docker-compose up -d
```

### 3. Uygulamanın Çalıştığını Doğrulama

Uygulama başarıyla başladıktan sonra:

- **Ana API**: http://localhost:8081
- **Health Check**: http://localhost:8081/actuator/health
- **H2 Console**: http://localhost:8081/h2-console
- **Actuator Metrics**: http://localhost:8081/actuator/metrics

## 📚 API Endpointleri

### Ürün İşlemleri

| Method | Endpoint | Açıklama | Request Body |
|--------|----------|----------|--------------|
| GET | `/api/products` | Tüm ürünleri listeler | - |
| GET | `/api/products/{id}` | Belirli ürünü getirir | - |
| POST | `/api/products` | Yeni ürün ekler | Product JSON |
| PUT | `/api/products/{id}` | Ürün günceller | Product JSON |
| DELETE | `/api/products/{id}` | Ürün siler | - |

### Stok İşlemleri

| Method | Endpoint | Açıklama | Request Body |
|--------|----------|----------|--------------|
| GET | `/api/stock/{productId}` | Ürün stok miktarını getirir | - |
| POST | `/api/stock/check` | Çoklu stok kontrolü yapar | StockCheckRequest JSON |
| POST | `/api/stock/reduce` | Stok miktarını azaltır | StockUpdateRequest JSON |
| POST | `/api/stock/increase` | Stok miktarını artırır | StockUpdateRequest JSON |

### Health & Monitoring

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/actuator/health` | Uygulama sağlık durumu |
| GET | `/actuator/metrics` | Uygulama metrikleri |
| GET | `/actuator/info` | Uygulama bilgileri |

## 📄 JSON Şemaları

### Product (Ürün)
```json
{
  "id": 1,
  "name": "Margherita Pizza",
  "description": "Klasik domates sos ve mozzarella peyniri",
  "price": 45.90,
  "category": "Pizza",
  "stockQuantity": 100,
  "isAvailable": true,
  "createdAt": "2024-01-08T10:00:00Z",
  "updatedAt": "2024-01-08T10:00:00Z"
}
```

### StockCheckRequest (Stok Kontrol İsteği)
```json
{
  "items": [
    {
      "productId": 1,
      "requiredQuantity": 2
    },
    {
      "productId": 2,
      "requiredQuantity": 1
    }
  ]
}
```

### StockUpdateRequest (Stok Güncelleme İsteği)
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

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
.\mvnw.cmd test -Dtest=ProductControllerTest
```

### Test Kategorileri

- **Unit Tests**: Controller, Service, Repository katmanları
- **Integration Tests**: Database entegrasyonu
- **API Tests**: REST endpoint testleri

## 🗄️ Veritabanı

### H2 Database Console

- **URL**: http://localhost:8081/h2-console
- **JDBC URL**: `jdbc:h2:mem:restaurantdb`
- **Kullanıcı**: `sa`
- **Şifre**: (boş)

### Örnek Veriler

Uygulama başladığında otomatik olarak örnek ürünler yüklenir:

1. **Margherita Pizza** - ₺45.90 (Stok: 100)
2. **Pepperoni Pizza** - ₺52.90 (Stok: 75)
3. **Coca Cola** - ₺8.50 (Stok: 200)
4. **Caesar Salad** - ₺28.90 (Stok: 50)

## 📝 Örnek API Çağrıları

### 1. Tüm Ürünleri Listele
```bash
curl -X GET http://localhost:8081/api/products
```

### 2. Yeni Ürün Ekle
```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Quattro Stagioni Pizza",
    "description": "Dört mevsim pizzası",
    "price": 58.90,
    "category": "Pizza",
    "stockQuantity": 80
  }'
```

### 3. Stok Kontrolü Yap
```bash
curl -X POST http://localhost:8081/api/stock/check \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {"productId": 1, "requiredQuantity": 2},
      {"productId": 3, "requiredQuantity": 1}
    ]
  }'
```

### 4. Stok Azalt (Sipariş Sonrası)
```bash
curl -X POST http://localhost:8081/api/stock/reduce \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {"productId": 1, "quantity": 2}
    ]
  }'
```

## 🐳 Docker Kullanımı

### Dockerfile ile Build
```powershell
docker build -t restaurant-api .
docker run -p 8081:8081 --name restaurant-api-container restaurant-api
```

### Docker Compose ile
```powershell
# Sadece Restaurant API'yi başlat
docker-compose up -d restaurant-api

# Tüm servisleri başlat
docker-compose up -d
```

### Container Yönetimi
```powershell
# Container durumunu kontrol et
docker ps

# Logları görüntüle
docker logs restaurant-api-container

# Container'ı durdur
docker stop restaurant-api-container

# Container'ı sil
docker rm restaurant-api-container
```

## 🔍 Monitoring ve Debugging

### Health Check
```bash
curl http://localhost:8081/actuator/health
```

Yanıt:
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

### Metrics
```bash
curl http://localhost:8081/actuator/metrics
```

### Uygulama Logları

Loglama seviyeleri:
- **INFO**: Genel bilgi mesajları
- **DEBUG**: Detaylı debug bilgileri
- **ERROR**: Hata mesajları

## 🛠️ Geliştirme

### IDE Kurulumu

**IntelliJ IDEA (Önerilen):**
1. Projeyi açın
2. Kotlin plugin'inin aktif olduğundan emin olun
3. Maven projesini import edin
4. JDK 17'yi seçin

**Visual Studio Code:**
1. Java Extension Pack yükleyin
2. Kotlin Language Support extension yükleyin
3. Maven for Java extension yükleyin

### Debug Modunda Çalıştırma
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```

### Profil Yönetimi

**Development Profili:**
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

**Production Profili:**
```powershell
java -jar target/restaurant-api-1.0.0.jar --spring.profiles.active=prod
```

## 📊 Test Coverage Hedefleri

- **Minimum Coverage**: %80
- **Controller Coverage**: %90+
- **Service Coverage**: %85+
- **Repository Coverage**: %75+

### Coverage Raporunu Görüntüleme
Test coverage raporu: `target/site/jacoco/index.html`

## 🔧 Sorun Giderme

### Port Çakışması
```powershell
# Port 8081 kullanımını kontrol et
netstat -an | findstr 8081

# Farklı port ile başlat
.\mvnw.cmd spring-boot:run -Dserver.port=8083
```

### Maven Sorunları
```powershell
# Maven cache temizle
.\mvnw.cmd dependency:purge-local-repository

# Temiz build
.\mvnw.cmd clean install
```

### H2 Database Sorunları
```powershell
# H2 console erişimi için application.properties kontrol et
spring.h2.console.enabled=true
spring.h2.console.settings.web-allow-others=true
```

## 🔄 Mikroservis Entegrasyonu

Bu servis diğer mikroservislerle şu şekilde entegre olur:

1. **Order API (Port: 8080)** - Sipariş oluşturulurken stok kontrolü yapar
2. **Delivery API (Port: 8082)** - Teslimat sürecinde ürün bilgilerini alır

### Entegrasyon Testleri
```powershell
# Tüm mikroservislerin çalıştığından emin olun
curl http://localhost:8081/actuator/health  # Restaurant API
curl http://localhost:8080/actuator/health  # Order API
curl http://localhost:8082/health           # Delivery API
```

## 📦 Dağıtım

### Production Build
```powershell
.\mvnw.cmd clean package -Pprod
```

### JAR Dosyası Çalıştırma
```powershell
java -jar target/restaurant-api-1.0.0.jar --spring.profiles.active=prod
```

### Environment Variables
```powershell
# Veritabanı konfigürasyonu
$env:SPRING_DATASOURCE_URL="jdbc:h2:mem:restaurantdb"
$env:SPRING_PROFILES_ACTIVE="prod"
$env:SERVER_PORT="8081"

# Uygulamayı başlat
java -jar target/restaurant-api-1.0.0.jar
```

## 📋 Yapılacaklar (TODO)

- [ ] PostgreSQL entegrasyonu
- [ ] Redis cache implementasyonu
- [ ] API rate limiting
- [ ] Authentication & Authorization
- [ ] Swagger/OpenAPI dokümantasyonu
- [ ] Prometheus metrics
- [ ] Circuit breaker pattern

## 📞 Destek

Sorun yaşadığınızda:
1. Bu README dosyasını kontrol edin
2. Logları inceleyin
3. Health check endpoint'ini kontrol edin
4. Test coverage raporuna bakın

## 📜 Lisans

Bu proje eğitim amaçlıdır ve MIT lisansı altında lisanslanmıştır.

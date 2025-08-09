# Restaurant API

Restoran menüsü ve stok yönetimi için geliştirilmiş Spring Boot ve Kotlin tabanlı mikroservis uygulaması. Ürün katalogunu yönetir ve stok kontrolü sağlar.

## Teknolojiler

- **Java 17**
- **Kotlin 1.8.21**
- **Spring Boot 3.1.0**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven** 
- **JUnit 5** 
- **Docker** 

## Gereksinimler

- Java 17 veya üzeri
- Maven 3.6 veya üzeri
- Docker ve Docker Compose
- Git

## Kurulum ve Çalıştırma

### 1. Proje Klonlama ve Bağımlılık İndirme

```bash
cd restaurant-api
./mvnw clean install
```

Windows'ta:
```cmd
mvnw.cmd clean install
```

### 2. Uygulamayı Başlatma

#### Geliştirme Ortamında (Maven ile):
```bash
./mvnw spring-boot:run
```

Windows'ta:
```cmd
mvnw.cmd spring-boot:run
```

#### JAR dosyası ile:
```bash
./mvnw clean package
java -jar target/restaurant-api-0.0.1-SNAPSHOT.jar
```

#### Docker ile:
```powershell
docker-compose up -d
```

#### PowerShell Script ile:
```powershell
.\start-restaurant.ps1
```

Uygulama http://localhost:8082 adresinde çalışacaktır.

## API Endpointleri

### Ürün İşlemleri

- `GET /api/products` - Tüm ürünleri listele
- `GET /api/products/{id}` - Ürün detaylarını getir
- `POST /api/products` - Yeni ürün ekle
- `PUT /api/products/{id}` - Ürün güncelle
- `DELETE /api/products/{id}` - Ürün sil

### Stok İşlemleri

- `GET /api/stock/{productId}` - Ürün stok miktarını getir
- `POST /api/stock/check` - Çoklu ürün stok kontrolü
- `POST /api/stock/reduce` - Stok miktarını azalt
- `POST /api/stock/increase` - Stok miktarını arttır

### Health Check

- `GET /actuator/health` - Uygulama sağlık durumu

### Örnek API Kullanımı

#### Yeni Ürün Ekleme:
```json
POST /api/products
{
    "name": "Margherita Pizza",
    "description": "Klasik domates sos ve mozzarella peyniri",
    "price": 45.90,
    "category": "Pizza",
    "stockQuantity": 100
}
```

#### Stok Kontrolü:
```json
POST /api/stock/check
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

#### Stok Azaltma:
```json
POST /api/stock/reduce
{
    "items": [
        {
            "productId": 1,
            "quantity": 2
        }
    ]
}
```

## Veritabanı

Uygulama H2 in-memory veritabanı kullanır. Geliştirme sırasında veritabanı konsolu şu adreste erişilebilir:
- **URL**: http://localhost:8082/h2-console
- **JDBC URL**: jdbc:h2:mem:restaurantdb
- **Kullanıcı**: sa
- **Şifre**: (boş)

### Başlangıç Verileri

Uygulama başlatıldığında örnek ürünler otomatik olarak yüklenir:
- Pizza çeşitleri
- İçecekler
- Tatlılar
- Ana yemekler

## Test Çalıştırma

### Unit Testler

```bash
# Tüm testleri çalıştır
./mvnw test

# Belirli bir test sınıfını çalıştır
./mvnw test -Dtest=ProductControllerTest

# Test kapsamı raporu ile
./mvnw test jacoco:report
```

Windows'ta:
```cmd
mvnw.cmd test
mvnw.cmd test -Dtest=ProductControllerTest
mvnw.cmd test jacoco:report
```

### Integration Testler

```bash
# Integration testleri çalıştır
./mvnw verify

# Sadece integration testler
./mvnw test -Dtest=*IntegrationTest
```

### Test Raporları

Test sonuçları şu konumlarda bulunur:
- **Surefire Reports**: `target/surefire-reports/`
- **JaCoCo Coverage**: `target/site/jacoco/index.html`

### Test Kategorileri

- **Unit Tests**: Controller, Service ve Repository katmanı testleri
- **Integration Tests**: Tam uygulama context'i ile testler
- **Repository Tests**: JPA repository testleri
- **Stock Tests**: Stok yönetimi business logic testleri

## Geliştirme

### Kod Yapısı

```
restaurant-api/
├── src/main/kotlin/
│   └── com/example/restaurantapi/
│       ├── RestaurantApiApplication.kt # Ana uygulama sınıfı
│       ├── controller/                 # REST controller'ları
│       ├── service/                    # İş mantığı katmanı
│       ├── repository/                 # Veri erişim katmanı
│       ├── model/                      # Entity sınıfları
│       ├── dto/                        # Data Transfer Objects
│       └── config/                     # Konfigürasyon sınıfları
├── src/main/resources/
│   ├── application.yml                 # Uygulama konfigürasyonu
│   └── data.sql                        # Başlangıç verileri
├── src/test/kotlin/                    # Test sınıfları
└── target/                             # Maven build çıktıları
```

### Profiller

- **default**: H2 in-memory veritabanı
- **test**: Test konfigürasyonu
- **docker**: Docker ortamı konfigürasyonu

### Kotlin ve Java Entegrasyonu

Bu proje Kotlin ile yazılmıştır ancak Java ecosystem'i ile tam uyumludur:
- Spring Boot annotations
- JPA entities
- Maven build lifecycle

## Docker Komutları

```powershell
# Uygulamayı başlat
docker-compose up -d

# Logları görüntüle
docker-compose logs restaurant-api

# Uygulamayı durdur
docker-compose down

# Tüm verileri sil ve yeniden başlat
docker-compose down -v ; docker-compose up -d
```

## Maven Komutları

```bash
# Projeyi derle (Kotlin + Java)
./mvnw compile

# Testleri çalıştır
./mvnw test

# Paketleme (JAR oluştur)
./mvnw package

# Bağımlılıkları güncelle
./mvnw dependency:resolve

# Proje temizle
./mvnw clean

# Kotlin compilation check
./mvnw kotlin:compile
```

## Durdurma

```powershell
# PowerShell script ile
.\stop-restaurant.ps1

# Docker Compose ile
docker-compose down

# Maven ile (eğer maven ile başlattıysanız)
# Ctrl+C ile durdurabilirsiniz
```

## Monitoring ve Health Check

### Actuator Endpoints

- `/actuator/health` - Sağlık durumu
- `/actuator/info` - Uygulama bilgileri
- `/actuator/metrics` - Metrikler

### JaCoCo Test Coverage

Test coverage raporunu görüntülemek için:

```bash
./mvnw test jacoco:report
```

Rapor `target/site/jacoco/index.html` dosyasında bulunur.

## API İş Akışı

### Tipik Stok Kontrolü Senaryosu:

1. **Sipariş geldiğinde**:
   ```
   POST /api/stock/check
   ```

2. **Stok varsa, rezerve et**:
   ```
   POST /api/stock/reduce
   ```

3. **Sipariş iptal edilirse, stoku geri al**:
   ```
   POST /api/stock/increase
   ```

## Sorun Giderme

### Port Çakışması
- 8082 portunun kullanımda olmadığından emin olun
- `netstat -an | findstr 8082` komutu ile port kullanımını kontrol edin

### Maven Bağımlılık Sorunları
```bash
./mvnw dependency:purge-local-repository
./mvnw clean install
```

### H2 Database Bağlantı Sorunu
- H2 console erişimi: http://localhost:8082/h2-console
- JDBC URL'in doğru olduğundan emin olun: `jdbc:h2:mem:restaurantdb`

### Kotlin Compilation Sorunu
```bash
./mvnw clean
./mvnw kotlin:compile
./mvnw compile
```

### Java Version Sorunu
```bash
java -version
./mvnw -version
```

Java 17 veya üzeri sürüm gereklidir.

## API Test Örnekleri

### cURL ile Test

```bash
# Tüm ürünleri listele
curl -X GET http://localhost:8082/api/products

# Yeni ürün ekle
curl -X POST http://localhost:8082/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Pizza","price":35.0,"stockQuantity":50}'

# Stok kontrolü
curl -X POST http://localhost:8082/api/stock/check \
  -H "Content-Type: application/json" \
  -d '{"items":[{"productId":1,"requiredQuantity":2}]}'
```

## Lisans

Bu proje eğitim amaçlıdır.

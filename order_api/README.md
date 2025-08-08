# Order API

Sipariş yönetimi için geliştirilmiş Spring Boot tabanlı mikroservis uygulaması. RESTful API tasarımı ile sipariş oluşturma, güncelleme ve takip işlemlerini gerçekleştirir. ELK Stack (Elasticsearch, Logstash, Kibana) ile gelişmiş log analizi ve monitoring sunar.

## Teknolojiler

- **Java 17**
- **Spring Boot 3.3.0**
- **Spring Data JPA**
- **H2 Database** (In-memory veritabanı)
- **Maven** (Bağımlılık yönetimi)
- **JUnit 5** (Test framework)
- **Docker** (Konteynerizasyon)
- **ELK Stack** (Log analizi)
  - **Elasticsearch 9.0.4** (Log depolama ve arama)
  - **Logstash 9.0.4** (Log toplama ve işleme)
  - **Kibana 9.0.4** (Log görselleştirme ve dashboard)

## Gereksinimler

- Java 17 veya üzeri
- Maven 3.6 veya üzeri
- Docker ve Docker Compose
- Git

## Kurulum ve Çalıştırma

### 1. Proje Klonlama ve Bağımlılık İndirme

```bash
cd order_api
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
java -jar target/order_api-0.0.1-SNAPSHOT.jar
```

#### Docker ile:
```powershell
docker-compose up -d
```

#### PowerShell Script ile:
```powershell
.\start-order.ps1
```

Uygulama http://localhost:8080 adresinde çalışacaktır.

## API Endpointleri

### Sipariş İşlemleri

- `POST /api/orders` - Yeni sipariş oluştur
- `GET /api/orders/{id}` - Sipariş detaylarını getir
- `GET /api/orders` - Tüm siparişleri listele
- `PUT /api/orders/{id}` - Sipariş güncelle
- `DELETE /api/orders/{id}` - Sipariş sil
- `PUT /api/orders/{id}/status` - Sipariş durumunu güncelle

### Health Check

- `GET /actuator/health` - Uygulama sağlık durumu

### Örnek API Kullanımı

#### Yeni Sipariş Oluşturma:
```json
POST /api/orders
{
    "customerId": "12345",
    "restaurantId": "rest-001",
    "items": [
        {
            "productId": "prod-001",
            "quantity": 2,
            "price": 25.50
        }
    ]
}
```

#### Sipariş Durumu Güncelleme:
```json
PUT /api/orders/{id}/status
{
    "status": "PREPARING"
}
```

## Veritabanı

Uygulama H2 in-memory veritabanı kullanır. Geliştirme sırasında veritabanı konsolu şu adreste erişilebilir:
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: jdbc:h2:mem:orderdb
- **Kullanıcı**: sa
- **Şifre**: (boş)

## Test Çalıştırma

### Unit Testler

```bash
# Tüm testleri çalıştır
./mvnw test

# Belirli bir test sınıfını çalıştır
./mvnw test -Dtest=OrderControllerTest

# Test kapsamı raporu ile
./mvnw test jacoco:report
```

Windows'ta:
```cmd
mvnw.cmd test
mvnw.cmd test -Dtest=OrderControllerTest
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

## Geliştirme

### Kod Yapısı

```
order_api/
├── src/main/java/
│   └── com/example/orderapi/
│       ├── OrderApiApplication.java    # Ana uygulama sınıfı
│       ├── controller/                 # REST controller'ları
│       ├── service/                    # İş mantığı katmanı
│       ├── repository/                 # Veri erişim katmanı
│       ├── model/                      # Entity sınıfları
│       ├── dto/                        # Data Transfer Objects
│       └── config/                     # Konfigürasyon sınıfları
├── src/main/resources/
│   ├── application.yml                 # Uygulama konfigürasyonu
│   └── application.properties
├── src/test/java/                      # Test sınıfları
└── logstash/config/                    # Logstash konfigürasyonu
```

### Profiller

- **default**: H2 in-memory veritabanı
- **test**: Test konfigürasyonu
- **docker**: Docker ortamı konfigürasyonu

### Loglama

Uygulama Logstash ile entegre çalışır. Loglar JSON formatında şu konuma yazılır:
- **Log Dosyası**: `logs/order-api.log`
- **Logstash Port**: 5044

## Docker Komutları

```powershell
# Uygulamayı başlat
docker-compose up -d

# Logları görüntüle
docker-compose logs order-api

# Uygulamayı durdur
docker-compose down

# Tüm verileri sil ve yeniden başlat
docker-compose down -v ; docker-compose up -d
```

## Maven Komutları

```bash
# Projeyi derle
./mvnw compile

# Testleri çalıştır
./mvnw test

# Paketleme (JAR oluştur)
./mvnw package

# Bağımlılıkları güncelle
./mvnw dependency:resolve

# Proje temizle
./mvnw clean
```

## Durdurma

```powershell
# PowerShell script ile
.\stop-order.ps1

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

## Sorun Giderme

### Port Çakışması
- 8080 portunun kullanımda olmadığından emin olun
- `netstat -an | findstr 8080` komutu ile port kullanımını kontrol edin

### Maven Bağımlılık Sorunları
```bash
./mvnw dependency:purge-local-repository
./mvnw clean install
```

### H2 Database Bağlantı Sorunu
- H2 console erişimi: http://localhost:8080/h2-console
- JDBC URL'in doğru olduğundan emin olun: `jdbc:h2:mem:orderdb`

### Java Version Sorunu
```bash
java -version
./mvnw -version
```

Java 17 veya üzeri sürüm gereklidir.

## ELK Stack (Log Analizi ve Monitoring)

### Elasticsearch

- **URL**: http://localhost:9200
- **Açıklama**: Logların depolandığı ve arandığı ana motor
- **Sağlık Kontrolü**: `curl http://localhost:9200/_cluster/health`

### Logstash

- **Port**: 5000 (TCP input)
- **Port**: 9600 (HTTP API)
- **Açıklama**: Order API'den gelen logları işler ve Elasticsearch'e gönderir
- **Konfigürasyon**: `logstash/config/logstash.conf`

### Kibana Dashboard

- **URL**: http://localhost:5601
- **Açıklama**: Logları görselleştirmek ve dashboard oluşturmak için kullanılır
- **Özellikler**:
  - Gerçek zamanlı log izleme
  - Hata analizi ve raporlama
  - Performans metrikleri
  - Özel dashboard'lar

### Log Formatı

Order API logları JSON formatında şu alanları içerir:
```json
{
  "@timestamp": "2024-01-08T10:30:00.000Z",
  "level": "INFO",
  "logger": "com.example.orderapi.controller.OrderController",
  "thread": "http-nio-8080-exec-1",
  "message": "Order created with ID: 123",
  "traceId": "abc123",
  "spanId": "def456"
}
```

### Kibana'da Log Analizi

1. **Kibana'ya Erişim**: http://localhost:5601
2. **Index Pattern Oluşturma**: 
   - "Stack Management" > "Index Patterns"
   - `order-api-*` pattern'ini oluşturun
3. **Discover Sekmesi**: Gerçek zamanlı log görüntüleme
4. **Dashboard Oluşturma**: Özel görselleştirmeler için

### Örnek Kibana Sorguları

```
# Hata logları
level:ERROR

# Belirli bir controller'dan gelen loglar
logger:*OrderController*

# Belirli zaman aralığındaki loglar
@timestamp:[2024-01-08T00:00:00 TO 2024-01-08T23:59:59]

# Sipariş oluşturma logları
message:"Order created"
```

# Mikroservis Test Projesi

Bu proje, basit bir mikroservis mimarisini göstermek için oluşturulmuştur. Üç farklı servis içerir ve her biri farklı teknolojiler kullanır.

## Mikroservisler

1. **Restaurant API (Port: 8081)** - Restoran menüsü ve stok yönetimi
   - **Teknoloji**: Kotlin + Spring Boot 3.1.0
   - **Veritabanı**: H2 (In-memory)
   - **Özellikler**: Ürün kataloğu, stok kontrolü, stok azaltma/artırma

2. **Order API (Port: 8080)** - Sipariş yönetimi
   - **Teknoloji**: Java 17 + Spring Boot 3.3.0
   - **Veritabanı**: H2 (In-memory)
   - **Log Stack**: Elasticsearch + Logstash + Kibana (ELK)
   - **Özellikler**: Sipariş oluşturma, sipariş takibi, durum yönetimi, log analizi

3. **Delivery API (Port: 8082)** - Teslimat yönetimi
   - **Teknoloji**: Go 1.23 + Gin Framework
   - **Veritabanı**: Couchbase (NoSQL)
   - **Özellikler**: Teslimat oluşturma, durum takibi

## Mikroservis README'leri

Her mikroservis için detaylı dokümantasyon içeren ayrı README dosyaları bulunmaktadır:

| Servis | README Bağlantısı|
|--------|-------------------|
| **Restaurant API** | [Restaurant API README](./restaurant-api/README.md) |
| **Order API** | [Order API README](./order_api/README.md) |
| **Delivery API** | [Delivery API README](./delivery-api/README.md) |

Bu dokümanlardan her servisi ayrı ayrı nasıl çalıştıracağınız, test edeceğiniz ve geliştireceğiniz hakkında detaylı bilgi edinebilirsiniz.

## Sistem Gereksinimleri

- **Java 17** veya üzeri
- **Maven 3.6+**
- **Go 1.23+**
- **Docker ve Docker Compose**
- **Git**

## Hızlı Başlangıç

### 1. Projeyi Klonlayın
```bash
git clone <repository-url>
cd microservice_test
```

### 2. Tüm Servisleri Başlatın

#### Windows Batch ile:
```batch
start-all.bat
```

#### PowerShell ile:
```powershell
.\start-all.ps1
```

#### Docker Compose ile:
```powershell
docker-compose up -d
```

> **Not**: PowerShell kullanırken komut zincirlemelerinde `&&` yerine `;` kullanımına dikkat ediniz.

### 3. Servislerin Durumunu Kontrol Edin

Tüm servisler başladıktan sonra şu adreslerde erişilebilir olacaktır:

- **Restaurant API**: http://localhost:8081
- **Order API**: http://localhost:8080  
- **Delivery API**: http://localhost:8082
- **Delivery API Swagger**: http://localhost:8082/swagger/index.html

## ELK Stack (Order API için Log Analizi)

- **Elasticsearch**: http://localhost:9200
- **Kibana Dashboard**: http://localhost:5601
- **Logstash**: http://localhost:5000 (TCP input)

## Detaylı Kurulum ve Çalıştırma

### Her Servisi Ayrı Ayrı Çalıştırma

Her mikroservisin kendi README dosyası vardır:

- [Restaurant API README](./restaurant-api/README.md)
- [Order API README](./order_api/README.md)
- [Delivery API README](./delivery-api/README.md)

### Sadece Veritabanlarını Başlatma
```powershell
.\start-databases-only.ps1
```

### Sadece API'leri Başlatma
```powershell
.\start-apis-only.ps1
```

## API Endpointleri

### Restaurant API (Port: 8081)

**Ürün İşlemleri:**
- `GET /api/products` - Tüm ürünleri listeler
- `GET /api/products/{id}` - Ürün detaylarını getirir
- `POST /api/products` - Yeni ürün ekler
- `PUT /api/products/{id}` - Ürün günceller
- `DELETE /api/products/{id}` - Ürün siler

**Stok İşlemleri:**
- `GET /api/stock/{productId}` - Ürün stok miktarını getirir
- `POST /api/stock/check` - Çoklu stok kontrolü yapar
- `POST /api/stock/reduce` - Stok miktarını azaltır
- `POST /api/stock/increase` - Stok miktarını artırır

### Order API (Port: 8080)

**Sipariş İşlemleri:**
- `POST /api/orders` - Yeni sipariş oluşturur
- `GET /api/orders` - Tüm siparişleri listeler
- `GET /api/orders/{id}` - Belirli bir siparişi getirir
- `PUT /api/orders/{id}` - Sipariş günceller
- `PUT /api/orders/{id}/status` - Sipariş durumunu günceller

**Health Check:**
- `GET /actuator/health` - Servis sağlık durumu

### Delivery API (Port: 8082)

**Teslimat İşlemleri:**
- `POST /api/delivery` - Yeni teslimat oluşturur
- `GET /api/delivery/{id}` - Teslimat durumunu sorgular
- `PUT /api/delivery/{id}/status` - Teslimat durumunu günceller
- `GET /api/delivery/order/{orderId}` - Sipariş ID'sine göre teslimat getirir

**Dokümantasyon:**
- `GET /swagger/index.html` - Swagger API dokümantasyonu

## Test Çalıştırma

### Tüm Servislerin Testlerini Çalıştırma

```powershell
# Restaurant API testleri
cd restaurant-api ; .\mvnw.cmd test ; cd ..

# Order API testleri  
cd order_api ; .\mvnw.cmd test ; cd ..

# Delivery API testleri
cd delivery-api ; go test ./... ; cd ..
```

### Test Coverage Raporları

**Java/Kotlin Servisleri (JaCoCo):**
```powershell
# Restaurant API
cd restaurant-api ; .\mvnw.cmd test jacoco:report ; cd ..

# Order API
cd order_api ; .\mvnw.cmd test jacoco:report ; cd ..
```

**Go Servisi:**
```powershell
cd delivery-api ; go test -cover ./... ; cd ..
```

Test raporları şu konumlarda bulunur:
- Restaurant API: `restaurant-api/target/site/jacoco/index.html`
- Order API: `order_api/target/site/jacoco/index.html`

## Veritabanı Erişimi

### H2 Database Konsolu

**Restaurant API:**
- URL: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:restaurantdb`
- Kullanıcı: `sa`
- Şifre: (boş)

**Order API:**
- URL: http://localhost:8080/h2-console  
- JDBC URL: `jdbc:h2:mem:orderdb`
- Kullanıcı: `sa`
- Şifre: (boş)

### Couchbase (Delivery API)

- URL: http://localhost:8091
- Kullanıcı: `Administrator`
- Şifre: `password`
- Bucket: `delivery`

## Docker ile Yönetim

### Tüm Servisleri Başlatma
```powershell
docker-compose up -d
```

### Logları İzleme
```powershell
# Tüm servis logları
docker-compose logs -f

# Belirli bir servis
docker-compose logs -f restaurant-api
docker-compose logs -f order-api  
docker-compose logs -f delivery-api
```

### Servisleri Durdurma
```powershell
# Graceful stop
docker-compose down

# Verileri de sil
docker-compose down -v

# PowerShell script ile
.\stop-all.ps1
```

## Örnek API Çağrıları

### 1. Ürün Ekleme (Restaurant API)
```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Margherita Pizza","price":45.90,"stockQuantity":100}'
```

### 2. Sipariş Oluşturma (Order API)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"12345","restaurantId":"rest-001","items":[{"productId":"1","quantity":2,"price":45.90}]}'
```

### 3. Teslimat Başlatma (Delivery API)
```bash
curl -X POST http://localhost:8082/api/delivery \
  -H "Content-Type: application/json" \
  -d '{"orderId":"order-123","address":"Test Address","customerId":"12345"}'
```

## Proje Yapısı

```
microservice_test/
├── README.md                    # Ana proje dokümantasyonu
├── docker-compose.yml           # Tüm servisler için Docker Compose
├── DOCKER-README.md             # Docker kullanım rehberi
├── start-all.bat               # Windows Batch başlatma script'i
├── start-all.ps1               # PowerShell başlatma script'i
├── start-apis-only.ps1         # Sadece API'leri başlat
├── start-databases-only.ps1    # Sadece veritabanlarını başlat
├── stop-all.ps1                # Tüm servisleri durdur
├── restaurant-api/             # Restoran API (Kotlin/Spring Boot)
│   ├── README.md               # Restaurant API dokümantasyonu
│   ├── pom.xml                 # Maven konfigürasyonu
│   ├── start-restaurant.ps1    # Servis başlatma script'i
│   └── src/                    # Kaynak kodlar
├── order_api/                  # Sipariş API (Java/Spring Boot)
│   ├── README.md               # Order API dokümantasyonu
│   ├── pom.xml                 # Maven konfigürasyonu
│   ├── start-order.ps1         # Servis başlatma script'i
│   └── src/                    # Kaynak kodlar
└── delivery-api/               # Teslimat API (Go)
    ├── README.md               # Delivery API dokümantasyonu
    ├── go.mod                  # Go modül konfigürasyonu
    ├── start-delivery.ps1      # Servis başlatma script'i
    └── main.go                 # Ana Go dosyası
```

## Geliştirme

### Yeni Özellik Ekleme

1. İlgili mikroservisi durdurun
2. Kod değişikliklerini yapın
3. Testleri çalıştırın
4. Servisi yeniden başlatın

### Debugging

Her servis farklı IDE'lerde debug edilebilir:
- **IntelliJ IDEA**: Restaurant API (Kotlin) ve Order API (Java)
- **Visual Studio Code**: Delivery API (Go)

### Log Dosyaları

- **Restaurant API**: Console ve dosya logging
- **Order API**: Logstash entegrasyonu (`logs/order-api.log`)
- **Delivery API**: Console logging

## Sorun Giderme

### Port Çakışmaları
```powershell
# Port kullanımını kontrol et
netstat -an | findstr 8080
netstat -an | findstr 8081  
netstat -an | findstr 8082
```

### Servis Sağlık Kontrolü
```powershell
# Health check endpoint'leri
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/health
```

### Docker Sorunları
```powershell
# Container durumunu kontrol et
docker-compose ps

# Container loglarını incele
docker-compose logs [service-name]

# Tüm container'ları temizle
docker-compose down -v ; docker system prune -f
```

### Maven Bağımlılık Sorunları
```powershell
# Cache temizle ve yeniden yükle
cd restaurant-api ; .\mvnw.cmd dependency:purge-local-repository ; .\mvnw.cmd clean install ; cd ..
cd order_api ; .\mvnw.cmd dependency:purge-local-repository ; .\mvnw.cmd clean install ; cd ..
```

### Go Modül Sorunları
```powershell
cd delivery-api ; go clean -modcache ; go mod download ; go mod tidy ; cd ..
```

## Performans Metrikleri

### Actuator Endpoints (Java Servisleri)

- **Restaurant API**: http://localhost:8081/actuator/metrics
- **Order API**: http://localhost:8080/actuator/metrics

### Test Coverage Hedefleri

- **Minimum Coverage**: %80
- **Controller Coverage**: %90+
- **Service Coverage**: %85+

## Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## Lisans

Bu proje eğitim amaçlıdır ve MIT lisansı altında lisanslanmıştır.

## Postman Test Collections

Projede her mikroservis için hazır Postman collection'ları bulunmaktadır. Bu collection'ları import ederek API'leri kolayca test edebilirsiniz.

### Ana Collection (Tüm Mikroservisler)

**📁 Mikroservis_Test_Complete_Collection.postman_collection.json**
- Tüm mikroservisleri içeren kapsamlı collection
- End-to-end test senaryoları
- Health check endpoint'leri
- ELK Stack monitoring
- Tam sipariş workflow'u

### Mikroservis Bazlı Collections

**🍕 Restaurant API Collection**
- Dosya: `restaurant-api/Restaurant_API_Collection.postman_collection.json`
- Ürün yönetimi (CRUD işlemleri)
- Stok kontrol ve yönetimi
- H2 console erişimi

**🛍️ Order API Collection**
- Dosya: `order_api/Order_API_Collection.postman_collection.json`
- Sipariş yönetimi (CRUD işlemleri)
- Sipariş durumu güncelleme
- ELK Stack monitoring
- Elasticsearch log arama

**🚚 Delivery API Collection**
- Dosya: `delivery-api/Delivery_API_Collection.postman_collection.json`
- Teslimat yönetimi
- Kurye atama ve durum takibi
- Couchbase monitoring
- Swagger dokümantasyonu

### Postman'e Import Etme

1. **Postman uygulamasını açın**
2. **Import** butonuna tıklayın
3. **File** sekmesini seçin
4. İstediğiniz collection dosyasını seçin:
   - **Tüm servisler için**: `Mikroservis_Test_Complete_Collection.postman_collection.json`
   - **Tek servis için**: İlgili mikroservis klasöründeki collection dosyası

### Environment Variables

Collection'lar otomatik olarak şu environment variable'ları tanımlar:

```
restaurant_url = http://localhost:8081
order_url = http://localhost:8080
delivery_url = http://localhost:8082
elasticsearch_url = http://localhost:9200
kibana_url = http://localhost:5601
couchbase_url = http://localhost:8091
```

### Test Senaryoları

**🔄 Complete Workflow Test:**
1. Ürün stok kontrolü (Restaurant API)
2. Sipariş oluşturma (Order API)
3. Stok azaltma (Restaurant API)
4. Teslimat oluşturma (Delivery API)
5. Durum güncellemeleri

**🏥 Health Check Suite:**
- Tüm mikroservislerin sağlık durumu
- Veritabanı bağlantı kontrolleri
- ELK Stack durumu

**📊 Monitoring Tests:**
- Elasticsearch log sorguları
- Kibana dashboard erişimi
- Couchbase cluster durumu

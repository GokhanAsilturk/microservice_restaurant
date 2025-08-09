# Delivery API

Teslimat yönetimi için geliştirilmiş Go tabanlı mikroservis uygulaması. Gin framework kullanılarak geliştirilmiştir ve Couchbase veritabanı ile çalışır.

## Teknolojiler

- **Go 1.23**
- **Gin Web Framework**
- **Couchbase** (NoSQL veritabanı)
- **Swagger** 
- **Docker** 

## Gereksinimler

- Go 1.23 veya üzeri
- Docker ve Docker Compose
- Git

## Kurulum ve Çalıştırma

### 1. Bağımlılıkları İndirme

```bash
cd delivery-api
go mod tidy
```

### 2. Docker ile Couchbase Başlatma

```powershell
# Sadece Couchbase veritabanını başlat
docker-compose up -d couchbase
```

### 3. Couchbase Kurulumu

Couchbase başladıktan sonra, otomatik kurulum scriptini çalıştırın:

```powershell
# Windows'ta bash script çalıştırmak için Git Bash veya WSL kullanın
bash setup-couchbase.sh
```

### 4. Uygulamayı Başlatma

#### Geliştirme Ortamında:
```bash
go run main.go
```

#### Docker ile:
```powershell
docker-compose up -d
```

#### PowerShell Script ile:
```powershell
.\start-delivery.ps1
```

Uygulama http://localhost:8081 adresinde çalışacaktır.

## API Endpointleri

### Teslimat İşlemleri

- `POST /api/delivery` - Yeni teslimat oluştur
- `GET /api/delivery/{id}` - Teslimat detaylarını getir
- `PUT /api/delivery/{id}/status` - Teslimat durumunu güncelle
- `GET /api/delivery/order/{orderId}` - Sipariş ID'sine göre teslimat getir

### Swagger Dokümantasyonu

API dokümantasyonu şu adreste mevcuttur:
- http://localhost:8081/swagger/index.html

## Test Çalıştırma

### Unit Testler

```bash
# Tüm testleri çalıştır
go test ./...

# Belirli bir paketi test et
go test ./controllers
go test ./models

# Test kapsamı raporu ile
go test -cover ./...

# Detaylı test çıktısı
go test -v ./...
```

### Integration Testler

Integration testleri çalıştırmadan önce Couchbase'in çalıştığından emin olun:

```bash
# Couchbase başlat
docker-compose up -d couchbase

# Integration testleri çalıştır
go test -tags=integration ./...
```

### Postman ile Test

`test_delivery.json` dosyasını Postman'e import ederek API'yi test edebilirsiniz.

## Geliştirme

### Kod Yapısı

```
delivery-api/
├── main.go              # Ana uygulama giriş noktası
├── controllers/         # HTTP controller'ları
├── database/           # Veritabanı bağlantı konfigürasyonu
├── models/             # Veri modelleri
├── routes/             # Route tanımlamaları
├── docs/               # Swagger dokümantasyonu
└── test_delivery.json  # Postman test koleksiyonu
```

### Swagger Dokümantasyonu Güncelleme

```bash
# Swagger dokümantasyonunu yeniden oluştur
swag init
```

## Docker Komutları

```powershell
# Uygulamayı başlat
docker-compose up -d

# Logları görüntüle
docker-compose logs delivery-api

# Uygulamayı durdur
docker-compose down

# Tüm verileri sil ve yeniden başlat
docker-compose down -v ; docker-compose up -d
```

## Durdurma

```powershell
# PowerShell script ile
.\stop-delivery.ps1

# Docker Compose ile
docker-compose down
```

## Sorun Giderme

### Couchbase Bağlantı Sorunu
- Couchbase'in çalıştığından emin olun: `docker-compose ps`
- Couchbase web arayüzünü kontrol edin: http://localhost:8091
- Bucket'ların oluşturulduğundan emin olun

### Port Çakışması
- 8081 portunun kullanımda olmadığından emin olun
- `netstat -an | findstr 8081` komutu ile port kullanımını kontrol edin

### Go Modülleri Sorunu
```bash
go clean -modcache
go mod download
go mod tidy
```

## Lisans

Bu proje eğitim amaçlıdır.

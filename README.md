# Mikroservis Test Projesi

Bu proje, basit bir mikroservis mimarisini göstermek için oluşturulmuştur. Üç farklı servis içerir:

1. **Restaurant API** - Restoran menüsü ve stok yönetimi
2. **Order API** - Sipariş yönetimi
3. **Delivery API** - Teslimat yönetimi

## Kurulum

Projeyi çalıştırmak için aşağıdaki gereksinimlere ihtiyacınız vardır:

- Java 17 veya üzeri
- Maven
- Kotlin
- Go

## Çalıştırma

Projeyi başlatmak için iki farklı yöntem kullanabilirsiniz:

### Windows Batch ile Çalıştırma

```batch
start-all.bat
```

### PowerShell ile Çalıştırma

```powershell
.\start-all.ps1
```

PowerShell kullanırken, komut zincirlemelerinde && yerine ; kullanımına dikkat ediniz.

## Servis Portları

- Restaurant API: 8082
- Order API: 8080
- Delivery API: 8081

## API Endpointleri

### Restaurant API

- `GET /api/products` - Tüm ürünleri listeler
- `POST /api/products` - Yeni ürün ekler
- `POST /api/stock/check` - Stok kontrolü yapar
- `POST /api/stock/reduce` - Stok miktarını azaltır

### Order API

- `POST /api/orders` - Yeni sipariş oluşturur
- `GET /api/orders` - Tüm siparişleri listeler
- `GET /api/orders/{id}` - Belirli bir siparişi getirir

### Delivery API

- `POST /api/delivery/start` - Teslimat başlatır
- `GET /api/delivery/{id}` - Teslimat durumunu sorgular

## Proje Yapısı

```
microservice_test/
├── start-all.bat        # Windows için başlatma script'i
├── start-all.ps1        # PowerShell için başlatma script'i
├── restaurant-api/      # Restoran API (Kotlin/Spring Boot)
├── order_api/           # Sipariş API (Java/Spring Boot)
└── delivery-api/        # Teslimat API (Go)
```

## Geliştirme

Her bir mikroservis kendi bağımsız teknoloji yığınını kullanır ve kendi veritabanı ile çalışır. Servisler arasındaki
iletişim REST API üzerinden sağlanır.

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

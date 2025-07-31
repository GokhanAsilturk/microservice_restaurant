# Microservices Docker Kurulumu

Bu proje, mikroservis mimarisinde çalışan üç servisi ve bunların veritabanlarını Docker containerları içinde yönetir.

## Servisler ve Veritabanları

### 1. Order API (Java/Spring Boot)
- **Port**: 8080
- **Stack**: ELK (Elasticsearch, Logstash, Kibana) 9.0.4
- **Container Adları**:
  - order-api
  - order-api-elasticsearch
  - order-api-logstash
  - order-api-kibana

### 2. Restaurant API (Kotlin/Spring Boot)
- **Port**: 8081
- **Database**: PostgreSQL 15-alpine
- **Admin Tool**: pgAdmin4
- **Container Adları**:
  - restaurant-api
  - restaurant-api-postgres
  - restaurant-api-pgadmin

### 3. Delivery API (Go)
- **Port**: 8082
- **Database**: Couchbase Community 7.6.2
- **Container Adları**: 
  - delivery-api
  - delivery-api-couchbasedb

## Kullanım Komutları

### Tüm Servisleri Yönetme

```powershell
# Tüm servisleri başlat
.\start-all.ps1

# Tüm servisleri durdur
.\stop-all.ps1
```

### Sadece Veritabanlarını Yönetme

```powershell
# Sadece veritabanlarını başlat
.\start-databases-only.ps1

# Sadece API'leri başlat (veritabanları çalışıyor olmalı)
.\start-apis-only.ps1
```

### Tek Tek Servis Yönetimi

#### Order API
```powershell
cd order_api
.\start-order.ps1       # Order API + ELK Stack
.\stop-order.ps1        # Durdur
```

#### Restaurant API
```powershell
cd restaurant-api
.\start-restaurant.ps1  # Restaurant API + PostgreSQL + pgAdmin
.\stop-restaurant.ps1   # Durdur
```

#### Delivery API
```powershell
cd delivery-api
.\start-delivery.ps1    # Delivery API + Couchbase
.\stop-delivery.ps1     # Durdur
```

## Erişim Noktaları

### API Endpointleri
- **Order API**: http://localhost:8080
- **Restaurant API**: http://localhost:8081
- **Delivery API**: http://localhost:8082

### Veritabanı Yönetim Araçları
- **Couchbase Admin**: http://localhost:8091
  - Kullanıcı: admin
  - Şifre: password
- **Kibana (Elasticsearch UI)**: http://localhost:5601
- **pgAdmin**: http://localhost:5050
  - Email: admin@admin.com
  - Şifre: admin

### Direkt Veritabanı Bağlantıları
- **Elasticsearch**: http://localhost:9200
- **PostgreSQL**: localhost:5432
  - Kullanıcı: postgres
  - Şifre: 1234
  - Veritabanı: restaurant_api
- **Logstash**: localhost:5000

## Docker Compose Yapısı

- **Ana Klasör**: Tüm servisleri birlikte yönetir
- **Her Servis Klasörü**: Kendi Docker Compose dosyasına sahip
- **Ayrı Networkler**: Her servis kendi network'ünde çalışır
- **Volume Yönetimi**: Veriler kalıcı volume'larda saklanır

## Önemli Notlar

1. **Port Çakışması**: Servisleri tek tek çalıştırırken port çakışması olmayacak şekilde ayarlanmıştır.

2. **Volume Silme**: Servisleri durdurduğunuzda veriler korunur. Verileri silmek için:
   ```powershell
   docker-compose down -v
   ```

3. **Log Takibi**: Servis loglarını takip etmek için:
   ```powershell
   docker-compose logs -f [servis-adı]
   ```

4. **Container Durumu**: Çalışan containerları görmek için:
   ```powershell
   docker ps
   ```

## Sistem Gereksinimleri

- Docker Desktop (Windows)
- PowerShell 5.0+
- En az 4GB RAM (tüm servisler için)
- Portlar: 5432, 5050, 5601, 8080, 8081, 8082, 8091-8096, 9200, 9300, 11210

## Sorun Giderme

### Container Başlamıyorsa
```powershell
docker-compose logs [container-name]
```

### Port Kullanımda Hatası
```powershell
netstat -ano | findstr :[PORT]
```

### Volume Sorunları
```powershell
docker volume ls
docker volume rm [volume-name]
```

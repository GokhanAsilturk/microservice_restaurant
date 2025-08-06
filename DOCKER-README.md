# Microservices Docker Kurulumu

Bu proje, mikroservis mimarisinde çalışan üç servisi ve bunlar��n veritabanlarını Docker containerları içinde yönetir.

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
    - delivery-api-couchbase-init

## Ana Komutlar

### Tüm Mikroservisleri Yönetme (Önerilen)

```powershell
# Tüm servisleri başlat (ana klasörden)
.\start-all.ps1

# Tüm servisleri durdur
.\stop-all.ps1

# Manuel olarak başlat
docker-compose up -d

# Manuel olarak durdur
docker-compose down
```

### Alternatif Komutlar

```powershell
# Sadece veritabanlarını başlat
.\start-databases-only.ps1

# Sadece API'leri başlat (veritabanları çalışıyor olmalı)
.\start-apis-only.ps1
```

### Tek Tek Servis Yönetimi (İsteğe Bağlı)

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

- **Order API**: http://localhost:8080 (Swagger UI)
- **Restaurant API**: http://localhost:8081 (JSON API)
- **Delivery API**: http://localhost:8082 (JSON API + Swagger)

### Swagger Dokümantasyonu

- **Order API Swagger**: http://localhost:8080/swagger-ui.html
- **Delivery API Swagger**: http://localhost:8082/swagger-ui/index.html

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

- **Ana Klasör**: Optimize edilmiş tek Docker Compose dosyası ile tüm servisleri yönetir
- **Healthcheck'ler**: Tüm veritabanları için sağlık kontrolü
- **Dependency Management**: Servisler veritabanları hazır olduktan sonra başlar
- **Shared Network**: Tüm servisler microservice-network'te çalışır
- **Volume Yönetimi**: Veriler kalıcı volume'larda saklanır

## Önemli Notlar

1. **Container Başlatma Sırası**:
    - Önce veritabanları (Elasticsearch, PostgreSQL, Couchbase)
    - Sonra init container'ları (Couchbase bucket oluşturma)
    - En son API'ler

2. **Build Süresi**: İlk çalıştırmada ~5-8 dakika sürebilir (tüm servislerin build edilmesi)

3. **Memory Kullanımı**: Tüm servisler için en az 6GB RAM önerilir

4. **Port Kullanımı**: Tüm portlar otomatik olarak yönetilir, çakışma yoktur

5. **Volume Silme**: Servisleri durdurduğunuzda veriler korunur. Verileri silmek için:
   ```powershell
   docker-compose down -v
   ```

6. **Log Takibi**: Servis loglarını takip etmek için:
   ```powershell
   docker-compose logs -f [servis-adı]
   ```

7. **Container Durumu**: Çalışan containerları görmek için:
   ```powershell
   docker-compose ps
   ```

## Sistem Gereksinimleri

- Docker Desktop (Windows)
- PowerShell 5.0+
- En az 6GB RAM (tüm servisler için)
- En az 20GB disk alanı
- Portlar: 5000, 5050, 5432, 5601, 8080, 8081, 8082, 8091-8096, 9200, 9300, 9600, 11210

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

### Sistem Temizliği

```powershell
# Kullanılmayan image ve volume'ları temizle
docker system prune -a -f
docker volume prune -f
```

### Container Yeniden Başlatma

```powershell
# Tek container
docker-compose restart [container-name]

# Tüm containerlar
docker-compose restart
```

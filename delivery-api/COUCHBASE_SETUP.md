# Couchbase Server Kurulum Rehberi

## 1. Couchbase Server İndirme ve Kurulum

### Windows için:
1. https://www.couchbase.com/downloads adresine git
2. "Couchbase Server Community Edition" indir
3. .exe dosyasını çalıştır ve kurulumu tamamla

## 2. İlk Yapılandırma

### Web Console'a Erişim:
- Tarayıcıda `http://localhost:8091` adresine git
- "Setup New Cluster" seçeneğini seç

### Cluster Ayarları:
- **Cluster Name**: delivery-cluster
- **Admin Username**: admin
- **Admin Password**: password
- **Memory Quotas**: Varsayılan değerleri kullan (Data: 1024 MB)

### Bucket Oluşturma:
1. Web Console'da "Buckets" sekmesine git
2. "Add Bucket" butonuna tıkla
3. Bucket ayarları:
   - **Bucket Name**: deliveries
   - **Bucket Type**: Couchbase
   - **Memory Quota**: 100 MB (test için yeterli)
   - **Replicas**: 0 (tek node için)

## 3. Test Verisi Ekleme

Web Console'da "Query" sekmesinden test verisi ekleyebilirsiniz:

```sql
INSERT INTO deliveries (KEY, VALUE) VALUES 
("delivery::1", {
  "type": "delivery",
  "deliveryId": 1,
  "orderId": "order-123",
  "customerId": 456,
  "address": "Test Mahallesi, Test Caddesi No:1",
  "status": "DELIVERED",
  "items": [
    {
      "productId": 1,
      "productName": "Pizza Margherita",
      "quantity": 2,
      "price": 45.50
    }
  ],
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T11:15:00Z"
});
```

## 4. Bağlantı Testi

PowerShell ile projeyi çalıştır:
```powershell
cd "C:\Users\gokha\Desktop\Desktop\Projeler\microservice_test\delivery-api"
go run main.go
```

## 5. API Test Komutları

### Teslimat Listesi:
```bash
curl http://localhost:8083/api/delivery/list
```

### Yeni Teslimat:
```bash
curl -X POST http://localhost:8083/api/delivery/start \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order-456",
    "customerId": 789,
    "address": "Yeni Mahalle, Yeni Cadde No:5",
    "items": [
      {
        "productId": 2,
        "productName": "Burger Menu",
        "quantity": 1,
        "price": 35.00
      }
    ]
  }'
```

### Teslimat Durumu:
```bash
curl http://localhost:8083/api/delivery/status/1
```

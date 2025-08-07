#!/bin/bash

echo "Couchbase initialization başlatılıyor..."

# Couchbase server'ın hazır olmasını bekle
echo "Couchbase server'ın hazır olmasını bekliyorum..."
max_attempts=60
attempt=0

while [ $attempt -lt $max_attempts ]; do
    echo "Deneme $((attempt + 1))/$max_attempts..."

    # Couchbase'in çalışıp çalışmadığını kontrol et
    if curl -f -s http://delivery-api-couchbasedb:8091/pools > /dev/null 2>&1; then
        echo "Couchbase server hazır!"
        break
    fi

    if [ $attempt -eq $((max_attempts - 1)) ]; then
        echo "HATA: Couchbase server $max_attempts deneme sonrası hazır değil!"
        exit 1
    fi

    sleep 5
    attempt=$((attempt + 1))
done

echo "Couchbase cluster durumu kontrol ediliyor..."

# Cluster durumunu kontrol et
cluster_status=$(curl -s http://delivery-api-couchbasedb:8091/pools/default 2>/dev/null)
if echo "$cluster_status" | grep -q '"name":"default"'; then
    echo "Cluster zaten initialize edilmiş."
    cluster_initialized=true
else
    echo "Cluster initialize edilmemiş, kurulum başlatılıyor..."
    cluster_initialized=false
fi

if [ "$cluster_initialized" = false ]; then
    echo "Cluster initialization başlatılıyor..."

    # Cluster'ı initialize et
    echo "Memory quota ayarlanıyor..."
    if ! curl -f -X POST http://delivery-api-couchbasedb:8091/pools/default \
      -d memoryQuota=512 \
      -d indexMemoryQuota=256 2>/dev/null; then
        echo "HATA: Memory quota ayarlanamadı!"
        exit 1
    fi

    # Services'i ayarla
    echo "Services ayarlanıyor..."
    if ! curl -f -X POST http://delivery-api-couchbasedb:8091/node/controller/setupServices \
      -d services=kv%2Cn1ql%2Cindex 2>/dev/null; then
        echo "HATA: Services ayarlanamadı!"
        exit 1
    fi

    # Web settings'i ayarla
    echo "Web settings ayarlanıyor..."
    if ! curl -f -X POST http://delivery-api-couchbasedb:8091/settings/web \
      -d username=admin \
      -d password=password \
      -d port=SAME 2>/dev/null; then
        echo "HATA: Web settings ayarlanamadı!"
        exit 1
    fi

    echo "Cluster initialization tamamlandı!"
else
    echo "Cluster initialization atlanıyor (zaten yapılmış)."
fi

# Admin kullanıcısının var olup olmadığını kontrol et
echo "Admin kullanıcısı kontrol ediliyor..."
auth_check=$(curl -s -u admin:password http://delivery-api-couchbasedb:8091/pools/default 2>/dev/null)
if echo "$auth_check" | grep -q '"name":"default"'; then
    echo "Admin kullanıcısı mevcut ve çalışıyor."
else
    echo "Admin kullanıcısı sorunlu, tekrar ayarlanıyor..."
    if ! curl -f -X POST http://delivery-api-couchbasedb:8091/settings/web \
      -d username=admin \
      -d password=password \
      -d port=SAME 2>/dev/null; then
        echo "UYARI: Admin kullanıcısı tekrar ayarlanamadı, devam ediliyor..."
    fi
fi

# Bucket kontrolü ve oluşturma
echo "Bucket kontrolü yapılıyor..."
bucket_check=$(curl -s -u admin:password http://delivery-api-couchbasedb:8091/pools/default/buckets 2>/dev/null)

if echo "$bucket_check" | grep -q '"name":"deliveries"'; then
    echo "Deliveries bucket zaten mevcut."
else
    echo "Deliveries bucket oluşturuluyor..."

    # Birkaç saniye bekle
    sleep 5

    if curl -f -X POST http://admin:password@delivery-api-couchbasedb:8091/pools/default/buckets \
      -d name=deliveries \
      -d bucketType=couchbase \
      -d ramQuotaMB=100 \
      -d replicaNumber=0 2>/dev/null; then
        echo "Deliveries bucket başarıyla oluşturuldu!"
    else
        echo "HATA: Deliveries bucket oluşturulamadı!"
        echo "Bucket listesi kontrol ediliyor..."
        curl -s -u admin:password http://delivery-api-couchbasedb:8091/pools/default/buckets | head -n 3
        exit 1
    fi
fi

# Bucket'ın hazır olmasını bekle
echo "Bucket'ın hazır olmasını bekliyorum..."
max_bucket_attempts=30
bucket_attempt=0

while [ $bucket_attempt -lt $max_bucket_attempts ]; do
    echo "Bucket hazırlık kontrolü $((bucket_attempt + 1))/$max_bucket_attempts..."

    bucket_detail=$(curl -s -u admin:password http://delivery-api-couchbasedb:8091/pools/default/buckets/deliveries 2>/dev/null)

    if echo "$bucket_detail" | grep -q '"status":"healthy"' || echo "$bucket_detail" | grep -q '"basicStats"'; then
        echo "Deliveries bucket hazır!"
        break
    fi

    if [ $bucket_attempt -eq $((max_bucket_attempts - 1)) ]; then
        echo "UYARI: Bucket $max_bucket_attempts deneme sonrası hazır görünmüyor, devam ediliyor..."
        break
    fi

    sleep 2
    bucket_attempt=$((bucket_attempt + 1))
done

echo "Couchbase initialization başarıyla tamamlandı!"
echo "Final durum kontrolleri:"
echo "- Cluster status: $(curl -s -u admin:password http://delivery-api-couchbasedb:8091/pools/default | grep -o '"name":"[^"]*"' | head -1 2>/dev/null || echo 'check failed')"
echo "- Bucket count: $(curl -s -u admin:password http://delivery-api-couchbasedb:8091/pools/default/buckets | grep -o '"name":"[^"]*"' | wc -l 2>/dev/null || echo 'check failed')"

exit 0

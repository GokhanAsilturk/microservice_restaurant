#!/bin/bash

echo "Couchbase initialization başlatılıyor..."

# Couchbase server'ın hazır olmasını bekle
sleep 15

# Cluster'ı initialize et
curl -X POST http://admin:password@delivery-api-couchbasedb:8091/pools/default \
  -d memoryQuota=512 \
  -d indexMemoryQuota=256

# Node'u cluster'a ekle
curl -X POST http://admin:password@delivery-api-couchbasedb:8091/node/controller/setupServices \
  -d services=kv%2Cn1ql%2Cindex

# Admin kullanıcısını ayarla
curl -X POST http://delivery-api-couchbasedb:8091/settings/web \
  -d username=admin \
  -d password=password \
  -d port=SAME

# Deliveries bucket'ını oluştur
curl -X POST http://admin:password@delivery-api-couchbasedb:8091/pools/default/buckets \
  -d name=deliveries \
  -d bucketType=couchbase \
  -d ramQuotaMB=100 \
  -d replicaNumber=0

echo "Couchbase initialization tamamlandı!"

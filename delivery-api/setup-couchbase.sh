#!/bin/bash

# Couchbase Setup Script
echo "Couchbase kurulumu başlatılıyor..."

# Couchbase server'ın tamamen hazır olmasını bekle
sleep 30

# Cluster'ı initialize et
echo "Cluster initialize ediliyor..."
curl -v -X POST http://delivery-api-couchbasedb:8091/pools/default \
  -u admin:password \
  -d memoryQuota=512 \
  -d indexMemoryQuota=256

# Services'leri ayarla
echo "Services ayarlanıyor..."
curl -v -X POST http://delivery-api-couchbasedb:8091/node/controller/setupServices \
  -u admin:password \
  -d services=kv%2Cn1ql%2Cindex

# Deliveries bucket'ını oluştur
echo "Deliveries bucket'ı oluşturuluyor..."
curl -v -X POST http://delivery-api-couchbasedb:8091/pools/default/buckets \
  -u admin:password \
  -d name=deliveries \
  -d bucketType=couchbase \
  -d ramQuotaMB=128 \
  -d replicaNumber=0

echo "Couchbase kurulumu tamamlandı!"

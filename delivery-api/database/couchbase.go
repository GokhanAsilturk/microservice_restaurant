package database

import (
	"fmt"
	"log"
	"os"
	"time"

	"github.com/couchbase/gocb/v2"
)

var (
	Cluster    *gocb.Cluster
	Bucket     *gocb.Bucket
	Collection *gocb.Collection
)

func InitCouchbase() {
	// Docker container içinden Couchbase host'u al
	couchbaseHost := os.Getenv("COUCHBASE_HOST")
	if couchbaseHost == "" {
		couchbaseHost = "localhost" // fallback için
	}

	couchbaseUsername := os.Getenv("COUCHBASE_USERNAME")
	if couchbaseUsername == "" {
		couchbaseUsername = "admin"
	}

	couchbasePassword := os.Getenv("COUCHBASE_PASSWORD")
	if couchbasePassword == "" {
		couchbasePassword = "password"
	}

	connectionString := fmt.Sprintf("couchbase://%s", couchbaseHost)
	log.Printf("Couchbase'e bağlanılıyor: %s", connectionString)

	// Couchbase bağlantısı
	cluster, err := gocb.Connect(connectionString, gocb.ClusterOptions{
		Authenticator: gocb.PasswordAuthenticator{
			Username: couchbaseUsername,
			Password: couchbasePassword,
		},
	})
	if err != nil {
		log.Fatal("Couchbase bağlantı hatası:", err)
	}

	Cluster = cluster

	// Bucket adı
	bucketName := "deliveries"

	log.Printf("Bucket'a bağlanılıyor: %s", bucketName)

	// Bucket'a bağlan (bucket'ın var olduğunu varsayıyoruz)
	bucket := cluster.Bucket(bucketName)

	// Bucket'ın hazır olmasını daha uzun süre bekle
	err = bucket.WaitUntilReady(30*time.Second, nil)
	if err != nil {
		log.Printf("Bucket hazır değil, varsayılan bucket kullanılacak: %v", err)
		// Eğer deliveries bucket'ı yoksa, default bucket'ı deneyelim
		bucket = cluster.Bucket("default")
		err = bucket.WaitUntilReady(10*time.Second, nil)
		if err != nil {
			log.Fatal("Hiçbir bucket hazır değil:", err)
		}
		bucketName = "default"
	}

	Bucket = bucket
	Collection = bucket.DefaultCollection()

	fmt.Printf("Couchbase bağlantısı başarılı - Host: %s, Bucket: %s\n", couchbaseHost, bucketName)
}

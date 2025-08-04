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

	// Bucket'a bağlan
	bucket := cluster.Bucket(bucketName)

	// Bucket'ın hazır olmasını bekle (daha kısa süre)
	err = bucket.WaitUntilReady(15*time.Second, nil)
	if err != nil {
		log.Printf("Bucket bağlantı hatası: %s", err)
		// Fatal yerine warning vererek devam et
		log.Println("Couchbase bucket hazır değil, uygulamayı yine de başlatıyoruz...")
		return
	}

	Bucket = bucket
	Collection = bucket.DefaultCollection()

	log.Printf("Couchbase bağlantısı başarılı - Host: %s, Bucket: %s", couchbaseHost, bucketName)
}

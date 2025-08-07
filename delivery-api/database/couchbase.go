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

	// Goroutine ile asenkron bağlantı denemesi
	go func() {
		maxRetries := 30
		retryDelay := 10 * time.Second

		for i := 0; i < maxRetries; i++ {
			cluster, err := gocb.Connect(connectionString, gocb.ClusterOptions{
				Authenticator: gocb.PasswordAuthenticator{
					Username: couchbaseUsername,
					Password: couchbasePassword,
				},
			})
			if err != nil {
				log.Printf("Couchbase bağlantı denemesi %d/%d başarısız: %v", i+1, maxRetries, err)
				if i < maxRetries-1 {
					log.Printf("%v sonra tekrar denenecek...", retryDelay)
					time.Sleep(retryDelay)
					continue
				}
				log.Printf("Couchbase bağlantısı kurulamadı (tüm denemeler başarısız): %v", err)
				return
			}

			Cluster = cluster

			// Bucket adı
			bucketName := "deliveries"
			log.Printf("Bucket'a bağlanılıyor: %s", bucketName)

			// Bucket'a bağlan
			bucket := cluster.Bucket(bucketName)

			// Bucket'ın hazır olmasını bekle (retry ile)
			for j := 0; j < 5; j++ {
				err = bucket.WaitUntilReady(60*time.Second, nil)
				if err == nil {
					break
				}
				log.Printf("Bucket bağlantı denemesi %d/5 başarısız: %v", j+1, err)
				if j < 4 {
					time.Sleep(20 * time.Second)
				}
			}

			if err != nil {
				log.Printf("Bucket bağlantı hatası: %s", err)
				log.Println("Couchbase bucket hazır değil, bucket olmadan devam ediliyor...")
				Bucket = nil
				Collection = nil
			} else {
				Bucket = bucket
				Collection = bucket.DefaultCollection()
				log.Printf("Couchbase bağlantısı başarılı - Host: %s, Bucket: %s", couchbaseHost, bucketName)
			}
			return
		}
	}()

	log.Println("Couchbase bağlantı işlemi arka planda başlatıldı, uygulama devam ediyor...")
}

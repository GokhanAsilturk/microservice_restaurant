package database

import (
	"fmt"
	"log"
	"time"

	"github.com/couchbase/gocb/v2"
)

var (
	Cluster    *gocb.Cluster
	Bucket     *gocb.Bucket
	Collection *gocb.Collection
)

func InitCouchbase() {
	// Couchbase bağlantısı
	cluster, err := gocb.Connect("couchbase://localhost", gocb.ClusterOptions{
		Authenticator: gocb.PasswordAuthenticator{
			Username: "admin",
			Password: "password",
		},
	})
	if err != nil {
		log.Fatal("Couchbase bağlantı hatası:", err)
	}

	Cluster = cluster

	// Bucket'a bağlan
	bucket := cluster.Bucket("deliveries")

	// Bucket'ın hazır olmasını bekle
	err = bucket.WaitUntilReady(5*time.Second, nil)
	if err != nil {
		log.Fatal("Bucket hazır değil:", err)
	}

	Bucket = bucket
	Collection = bucket.DefaultCollection()

	fmt.Println("Couchbase bağlantısı başarılı")
}

package main

import (
	"delivery-api/database"
	"delivery-api/routes"
	"log"

	_ "delivery-api/docs"
)

func main() {
	log.Println("Delivery API başlatılıyor...")

	database.InitCouchbase()

	r := routes.SetupRouter()

	log.Println("Sunucu başlatılıyor: http://localhost:8082")
	r.Run(":8082")
}

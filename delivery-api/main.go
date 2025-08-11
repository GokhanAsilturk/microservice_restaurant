package main

import (
	"delivery-api/database"
	"delivery-api/routes"
	"fmt"
	"log"

	_ "delivery-api/docs"
)

func main() {
	fmt.Println("Delivery API başlatılıyor...")

	database.InitCouchbase()

	r := routes.SetupRouter()

	log.Println("Sunucu başlatılıyor: http://localhost:8082")
	r.Run(":8082")
}

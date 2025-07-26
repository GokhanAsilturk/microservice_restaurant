package main

import (
	"delivery-api/routes"
	"fmt"
	"log"

	_ "delivery-api/docs" // swagger için gerekli
)

// @title           Delivery API
// @version         1.0
// @description     Teslimat mikro servisi için API dokümantasyonu
// @host            localhost:8083
// @BasePath        /api/delivery
func main() {
	fmt.Println("Delivery API başlatılıyor...")

	r := routes.SetupRouter()

	// 8083 portunda sunucuyu başlat
	log.Println("Sunucu başlatılıyor: http://localhost:8083")
	r.Run(":8083")
}

package routes

import (
	"delivery-api/controllers"
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
	"time"
)

func SetupRouter() *gin.Engine {
	r := gin.Default()

	// CORS middleware - production için daha güvenli ayarlar
	config := cors.DefaultConfig()
	config.AllowOrigins = []string{"http://localhost:3000", "http://localhost:8080", "http://localhost:8081", "http://localhost:8082"}
	config.AllowMethods = []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"}
	config.AllowHeaders = []string{"Origin", "Content-Length", "Content-Type", "Authorization"}
	config.MaxAge = 12 * time.Hour
	r.Use(cors.New(config))

	// Ana sayfa endpoint'i
	r.GET("/", func(c *gin.Context) {
		c.JSON(200, gin.H{
			"status":  "OK",
			"message": "Delivery API çalışıyor",
			"version": "1.0.0",
			"port":    "8082",
			"swagger": "/swagger/index.html",
		})
	})

	// Swagger Dokümantasyon endpoint'i
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	// API endpoint'leri
	api := r.Group("/api/delivery")
	{
		// Teslimat başlatma
		api.POST("/start", controllers.StartDelivery)

		// Teslimat durumu sorgulama
		api.GET("/status/:id", controllers.GetDeliveryStatus)

		// Tüm teslimatları listeleme
		api.GET("/list", controllers.ListDeliveries)
	}

	return r
}

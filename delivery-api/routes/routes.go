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

	// CORS middleware'ini güncelliyoruz
	config := cors.DefaultConfig()
	config.AllowAllOrigins = true
	config.AllowMethods = []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"}
	config.AllowHeaders = []string{"Origin", "Content-Length", "Content-Type", "Authorization", "Accept", "X-Requested-With"}
	config.MaxAge = 12 * time.Hour
	r.Use(cors.New(config))

	// Ana sayfa endpoint'i
	r.GET("/", func(c *gin.Context) {
		c.JSON(200, gin.H{
			"status":  "OK",
			"message": "Delivery API çalışıyor",
			"version": "1.0.0",
			"port":    "8082",
		})
	})

	// Health endpoint'i
	r.GET("/health", func(c *gin.Context) {
		c.JSON(200, gin.H{
			"status": "UP",
			"api":    "Delivery API",
		})
	})

	// Swagger Dokümantasyon endpoint'i
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
	r.GET("/swagger-ui/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

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

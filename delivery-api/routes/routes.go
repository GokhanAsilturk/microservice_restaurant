package routes

import (
	"delivery-api/controllers"
	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
)

func SetupRouter() *gin.Engine {
	r := gin.Default()

	// CORS ayarları
	r.Use(func(c *gin.Context) {
		c.Writer.Header().Set("Access-Control-Allow-Origin", "*")
		c.Writer.Header().Set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE")
		c.Writer.Header().Set("Access-Control-Allow-Headers", "Accept, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization")
		if c.Request.Method == "OPTIONS" {
			c.AbortWithStatus(204)
			return
		}
		c.Next()
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

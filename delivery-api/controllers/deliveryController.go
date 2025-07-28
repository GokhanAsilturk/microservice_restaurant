package controllers

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"math/rand"
	"net/http"
	"strconv"
	"time"

	"delivery-api/models"
)

var deliveries = make(map[int]models.Delivery)
var lastDeliveryId = 100 // ID'ler 101'den başlayacak

func init() {
	rand.Seed(time.Now().UnixNano())
}

// StartDelivery teslimat başlatır
// @Summary      Yeni bir teslimat başlatır
// @Description  Müşteri bilgileri ve sipariş detayları ile yeni bir teslimat kaydı oluşturur
// @Tags         deliveries
// @Accept       json
// @Produce      json
// @Param        request  body  models.DeliveryRequest  true  "Teslimat İsteği"
// @Success      200  {object}  models.DeliveryResponse
// @Failure      400  {object}  models.DeliveryResponse
// @Router       /api/delivery/start [post]
func StartDelivery(c *gin.Context) {
	var request models.DeliveryRequest

	// Gelen JSON'ı request nesnesine çözümle
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, models.DeliveryResponse{
			Success: false,
			Message: "Geçersiz istek formatı: " + err.Error(),
		})
		return
	}

	// Adres kontrolü
	if request.Address == "" {
		c.JSON(http.StatusBadRequest, models.DeliveryResponse{
			Success: false,
			Message: "Teslimat adresi gerekli",
		})
		return
	}

	// Yeni teslimat kaydı oluştur
	lastDeliveryId++
	deliveryID := lastDeliveryId
	delivery := models.Delivery{
		DeliveryId: deliveryID,
		OrderId:    request.OrderId,
		CustomerId: request.CustomerId,
		Address:    request.Address,
		Status:     "PREPARING",
		Items:      request.Items,
	}

	// Teslimat kaydını sakla
	deliveries[deliveryID] = delivery

	fmt.Printf("Yeni teslimat başlatıldı: %d, Müşteri: %d, Adres: %s\n",
		deliveryID, request.CustomerId, request.Address)

	// Yanıt gönder
	c.JSON(http.StatusOK, models.DeliveryResponse{
		Success:    true,
		DeliveryId: deliveryID,
		Message:    "Teslimat başarıyla oluşturuldu ve hazırlanıyor",
	})
}

// GetDeliveryStatus teslimat durumunu kontrol eder
// @Summary      Teslimat durumunu sorgular
// @Description  Teslimat ID'si ile teslimat durumunu ve detaylarını getirir
// @Tags         deliveries
// @Accept       json
// @Produce      json
// @Param        id   path  string  true  "Teslimat ID"
// @Success      200  {object}  models.Delivery
// @Failure      404  {object}  map[string]interface{}
// @Router       /api/delivery/status/{id} [get]
func GetDeliveryStatus(c *gin.Context) {
	deliveryIdStr := c.Param("id")

	deliveryId, err := strconv.Atoi(deliveryIdStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"success": false,
			"message": "Geçersiz teslimat ID formatı",
		})
		return
	}

	if delivery, exists := deliveries[deliveryId]; exists {
		c.JSON(http.StatusOK, delivery)
	} else {
		c.JSON(http.StatusNotFound, gin.H{
			"success": false,
			"message": "Teslimat bulunamadı",
		})
	}
}

// ListDeliveries tüm teslimatları listeler
// @Summary      Tüm teslimatları listeler
// @Description  Sistemdeki tüm teslimatları liste halinde döndürür
// @Tags         deliveries
// @Accept       json
// @Produce      json
// @Success      200  {array}   models.Delivery
// @Router       /api/delivery/list [get]
func ListDeliveries(c *gin.Context) {
	var deliveryList []models.Delivery

	for _, delivery := range deliveries {
		deliveryList = append(deliveryList, delivery)
	}

	c.JSON(http.StatusOK, deliveryList)
}

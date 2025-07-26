package controllers

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"net/http"

	"delivery-api/models"
)

// İşlem başarılı olması için %80 şans veren bir simülasyon
var deliveries = make(map[string]models.Delivery)

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
	deliveryID := uuid.New().String()
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

	fmt.Printf("Yeni teslimat başlatıldı: %s, Müşteri: %d, Adres: %s\n",
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
	deliveryId := c.Param("id")

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

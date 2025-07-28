package controllers

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"math/rand"
	"net/http"
	"strconv"
	"time"

	"delivery-api/database"
	"delivery-api/models"
)

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

	// Yeni teslimat ID'si oluştur
	lastDeliveryId++
	deliveryId := lastDeliveryId

	// Teslimat nesnesini oluştur
	delivery := models.Delivery{
		ID:         fmt.Sprintf("delivery::%d", deliveryId),
		Type:       "delivery",
		DeliveryId: deliveryId,
		OrderId:    request.OrderId,
		CustomerId: request.CustomerId,
		Address:    request.Address,
		Status:     "PREPARING",
		Items:      request.Items,
		CreatedAt:  time.Now(),
		UpdatedAt:  time.Now(),
	}

	// Couchbase'e kaydet
	_, err := database.Collection.Insert(delivery.ID, delivery, nil)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.DeliveryResponse{
			Success: false,
			Message: "Teslimat kaydedilemedi: " + err.Error(),
		})
		return
	}

	// Log yazdır
	fmt.Printf("Yeni teslimat başlatıldı: %d, Müşteri: %d, Adres: %s\n",
		deliveryId, request.CustomerId, request.Address)

	// Başarılı yanıt döndür
	c.JSON(http.StatusOK, models.DeliveryResponse{
		Success:    true,
		DeliveryId: deliveryId,
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

	var delivery models.Delivery

	// Couchbase'den teslimatı getir
	err = database.Collection.Get(fmt.Sprintf("delivery::%d", deliveryId), &delivery)
	if err != nil {
		if err == gocb.ErrDocumentNotFound {
			c.JSON(http.StatusNotFound, gin.H{
				"success": false,
				"message": "Teslimat bulunamadı",
			})
		} else {
			c.JSON(http.StatusInternalServerError, gin.H{
				"success": false,
				"message": "Teslimat bilgileri alınamadı: " + err.Error(),
			})
		}
		return
	}

	c.JSON(http.StatusOK, delivery)
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

	// Tüm teslimatları listele
	query := "SELECT meta().id, deliveryId, orderId, customerId, address, status, items, createdAt, updatedAt " +
		"FROM `delivery-api` " +
		"WHERE type = 'delivery'"

	rows, err := database.Bucket.Query(query, nil)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"success": false,
			"message": "Teslimatlar alınamadı: " + err.Error(),
		})
		return
	}
	defer rows.Close()

	for rows.Next() {
		var delivery models.Delivery
		err := rows.Scan(&delivery.ID, &delivery.DeliveryId, &delivery.OrderId, &delivery.CustomerId, &delivery.Address, &delivery.Status, &delivery.Items, &delivery.CreatedAt, &delivery.UpdatedAt)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{
				"success": false,
				"message": "Teslimat verileri işlenemedi: " + err.Error(),
			})
			return
		}
		deliveryList = append(deliveryList, delivery)
	}

	c.JSON(http.StatusOK, deliveryList)
}

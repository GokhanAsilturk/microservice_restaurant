package controllers

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"net/http"
	"time"

	"delivery-api/database"
	"delivery-api/models"
)

var lastDeliveryId = 100

func StartDelivery(c *gin.Context) {
	var request models.DeliveryRequest

	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, models.DeliveryResponse{
			Success: false,
			Message: "Geçersiz istek formatı: " + err.Error(),
		})
		return
	}

	if request.Address == "" {
		c.JSON(http.StatusBadRequest, models.DeliveryResponse{
			Success: false,
			Message: "Teslimat adresi gerekli",
		})
		return
	}

	lastDeliveryId++
	deliveryId := lastDeliveryId

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

	_, err := database.Collection.Insert(delivery.ID, delivery, nil)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.DeliveryResponse{
			Success: false,
			Message: "Teslimat kaydedilemedi: " + err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, models.DeliveryResponse{
		Success:    true,
		DeliveryId: deliveryId,
		Message:    "Teslimat başarıyla oluşturuldu ve hazırlanıyor",
	})
}

func GetAllDeliveries(c *gin.Context) {
	var deliveryList []models.Delivery

	query := "SELECT meta().id, deliveryId, orderId, customerId, address, status, items, createdAt, updatedAt " +
		"FROM `deliveries` " +
		"WHERE type = 'delivery'"

	queryResult, err := database.Cluster.Query(query, nil)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"success": false,
			"message": "Teslimatlar alınamadı: " + err.Error(),
		})
		return
	}
	defer queryResult.Close()

	for queryResult.Next() {
		var delivery models.Delivery
		err := queryResult.Row(&delivery)
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

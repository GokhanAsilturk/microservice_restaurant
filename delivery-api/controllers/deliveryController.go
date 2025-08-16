package controllers

import (
	"fmt"
	"log"
	"github.com/gin-gonic/gin"
	"net/http"

	"delivery-api/database"
	"delivery-api/models"
)

var lastDeliveryId = 100

func StartDelivery(c *gin.Context) {
	var request models.DeliveryRequest

	if err := c.ShouldBindJSON(&request); err != nil {
		log.Printf("Geçersiz teslimat isteği formatı: %v", err)
		response := models.ErrorResponseWithCode("Geçersiz istek formatı", "INVALID_REQUEST")
		c.JSON(http.StatusBadRequest, response)
		return
	}

	if request.Address == "" {
		log.Printf("Teslimat adresi eksik, sipariş ID: %s", request.OrderId)
		response := models.ErrorResponseWithCode("Teslimat adresi gerekli", "MISSING_ADDRESS")
		c.JSON(http.StatusBadRequest, response)
		return
	}

	deliveryDomain := request.ToDomain()

	lastDeliveryId++
	deliveryDomain.ID = fmt.Sprintf("delivery::%d", lastDeliveryId)

	if !deliveryDomain.CanAssign() {
		log.Printf("Teslimat atama için uygun durumda değil: %s", deliveryDomain.ID)
		response := models.ErrorResponseWithCode("Teslimat durumu uygun değil", "INVALID_STATUS")
		c.JSON(http.StatusBadRequest, response)
		return
	}

	err := deliveryDomain.Assign()
	if err != nil {
		log.Printf("Teslimat atama hatası: %v", err)
		response := models.ErrorResponseWithCode("Teslimat atanamadı", "ASSIGNMENT_FAILED")
		c.JSON(http.StatusInternalServerError, response)
		return
	}

	log.Printf("Yeni teslimat oluşturuluyor, sipariş ID: %s, teslimat ID: %s", deliveryDomain.OrderID, deliveryDomain.ID)

	entity := models.DomainToEntity(deliveryDomain)
	_, err = database.Collection.Insert(entity.ID, entity, nil)
	if err != nil {
		log.Printf("Teslimat kaydedilemedi, teslimat ID: %s, hata: %v", deliveryDomain.ID, err)
		response := models.ErrorResponseWithCode("Teslimat kaydedilemedi", "DATABASE_ERROR")
		c.JSON(http.StatusInternalServerError, response)
		return
	}

	log.Printf("Teslimat başarıyla oluşturuldu, teslimat ID: %d", lastDeliveryId)

	deliveryResponse := models.DeliveryResponse{
		Success:    true,
		DeliveryId: lastDeliveryId,
		Message:    "Teslimat başarıyla oluşturuldu ve hazırlanıyor",
	}

	response := models.SuccessResponseWithMessage(deliveryResponse, "Teslimat başarıyla oluşturuldu")
	c.JSON(http.StatusOK, response)
}

func GetAllDeliveries(c *gin.Context) {
	log.Println("Tüm teslimatlar istendi")
	var deliveryList []models.Delivery

	query := "SELECT meta().id, deliveryId, orderId, customerId, address, status, items, createdAt, updatedAt " +
		"FROM `deliveries` " +
		"WHERE type = 'delivery'"

	queryResult, err := database.Cluster.Query(query, nil)
	if err != nil {
		log.Printf("Teslimatlar sorgulanamadı: %v", err)
		response := models.ErrorResponseWithCode("Teslimatlar alınamadı", "DATABASE_ERROR")
		c.JSON(http.StatusInternalServerError, response)
		return
	}
	defer queryResult.Close()

	for queryResult.Next() {
		var delivery models.Delivery
		err := queryResult.Row(&delivery)
		if err != nil {
			log.Printf("Teslimat verileri işlenemedi: %v", err)
			response := models.ErrorResponseWithCode("Teslimat verileri işlenemedi", "DATA_PROCESSING_ERROR")
			c.JSON(http.StatusInternalServerError, response)
			return
		}
		deliveryList = append(deliveryList, delivery)
	}

	log.Printf("%d adet teslimat döndürüldü", len(deliveryList))
	response := models.SuccessResponseWithMessage(deliveryList, "Teslimatlar başarıyla getirildi")
	c.JSON(http.StatusOK, response)
}

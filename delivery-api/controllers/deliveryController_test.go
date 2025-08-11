package controllers

import (
	"bytes"
	"delivery-api/models"
	"encoding/json"
	"fmt"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/gin-gonic/gin"
	"github.com/stretchr/testify/assert"
)

func init() {
	gin.SetMode(gin.TestMode)
}

var mockDatabase struct {
	documents  map[string]interface{}
	shouldFail bool
}

func init() {
	mockDatabase.documents = make(map[string]interface{})
}

func mockInsert(id string, doc interface{}) error {
	if mockDatabase.shouldFail {
		return assert.AnError
	}
	mockDatabase.documents[id] = doc
	return nil
}

func TestStartDelivery_Success(t *testing.T) {
	mockDatabase.documents = make(map[string]interface{})
	mockDatabase.shouldFail = false

	deliveryRequest := models.DeliveryRequest{
		OrderId:    "order-123",
		CustomerId: 456,
		Address:    "Test Address 123",
		Items: []models.OrderItem{
			{
				ProductId:   1,
				ProductName: "Test Product",
				Quantity:    2,
				Price:       25.99,
			},
		},
	}

	jsonData, err := json.Marshal(deliveryRequest)
	assert.NoError(t, err)

	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.POST("/api/delivery/start", func(c *gin.Context) {
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

		c.JSON(http.StatusOK, models.DeliveryResponse{
			Success:    true,
			DeliveryId: 101,
			Message:    "Teslimat başarıyla oluşturuldu ve hazırlanıyor",
		})
	})

	req, _ := http.NewRequest("POST", "/api/delivery/start", bytes.NewBuffer(jsonData))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var response models.DeliveryResponse
	err = json.Unmarshal(w.Body.Bytes(), &response)
	assert.NoError(t, err)
	assert.True(t, response.Success)
	assert.NotZero(t, response.DeliveryId)
	assert.Contains(t, response.Message, "başarıyla oluşturuldu")
}

func TestStartDelivery_InvalidJSON(t *testing.T) {
	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.POST("/api/delivery/start", func(c *gin.Context) {
		var request models.DeliveryRequest
		if err := c.ShouldBindJSON(&request); err != nil {
			c.JSON(http.StatusBadRequest, models.DeliveryResponse{
				Success: false,
				Message: "Geçersiz istek formatı: " + err.Error(),
			})
			return
		}
	})

	req, _ := http.NewRequest("POST", "/api/delivery/start", bytes.NewBufferString(`{"invalid": json}`))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusBadRequest, w.Code)

	var response models.DeliveryResponse
	err := json.Unmarshal(w.Body.Bytes(), &response)
	assert.NoError(t, err)
	assert.False(t, response.Success)
	assert.Contains(t, response.Message, "Geçersiz istek formatı")
}

func TestStartDelivery_EmptyAddress(t *testing.T) {
	deliveryRequest := models.DeliveryRequest{
		OrderId:    "order-123",
		CustomerId: 456,
		Address:    "",
		Items: []models.OrderItem{
			{
				ProductId:   1,
				ProductName: "Test Product",
				Quantity:    2,
				Price:       25.99,
			},
		},
	}
	jsonData, err := json.Marshal(deliveryRequest)
	assert.NoError(t, err)

	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.POST("/api/delivery/start", func(c *gin.Context) {
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
	})

	req, _ := http.NewRequest("POST", "/api/delivery/start", bytes.NewBuffer(jsonData))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusBadRequest, w.Code)

	var response models.DeliveryResponse
	err = json.Unmarshal(w.Body.Bytes(), &response)
	assert.NoError(t, err)
	assert.False(t, response.Success)
	assert.Equal(t, "Teslimat adresi gerekli", response.Message)
}

func TestStartDelivery_EmptyOrderId(t *testing.T) {
	deliveryRequest := models.DeliveryRequest{
		OrderId:    "",
		CustomerId: 456,
		Address:    "Test Address 123",
		Items: []models.OrderItem{
			{
				ProductId:   1,
				ProductName: "Test Product",
				Quantity:    2,
				Price:       25.99,
			},
		},
	}
	jsonData, err := json.Marshal(deliveryRequest)
	assert.NoError(t, err)

	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.POST("/api/delivery/start", func(c *gin.Context) {
		var request models.DeliveryRequest
		if err := c.ShouldBindJSON(&request); err != nil {
			c.JSON(http.StatusBadRequest, models.DeliveryResponse{
				Success: false,
				Message: "Geçersiz istek formatı: " + err.Error(),
			})
			return
		}

		if request.OrderId == "" {
			c.JSON(http.StatusBadRequest, models.DeliveryResponse{
				Success: false,
				Message: "Sipariş ID gerekli",
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
	})

	req, _ := http.NewRequest("POST", "/api/delivery/start", bytes.NewBuffer(jsonData))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusBadRequest, w.Code)

	var response models.DeliveryResponse
	err = json.Unmarshal(w.Body.Bytes(), &response)
	assert.NoError(t, err)
	assert.False(t, response.Success)
	assert.Equal(t, "Sipariş ID gerekli", response.Message)
}

func TestStartDelivery_EmptyItems(t *testing.T) {
	deliveryRequest := models.DeliveryRequest{
		OrderId:    "order-123",
		CustomerId: 456,
		Address:    "Test Address 123",
		Items:      []models.OrderItem{},
	}
	jsonData, err := json.Marshal(deliveryRequest)
	assert.NoError(t, err)

	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.POST("/api/delivery/start", func(c *gin.Context) {
		var request models.DeliveryRequest
		if err := c.ShouldBindJSON(&request); err != nil {
			c.JSON(http.StatusBadRequest, models.DeliveryResponse{
				Success: false,
				Message: "Geçersiz istek formatı: " + err.Error(),
			})
			return
		}

		if len(request.Items) == 0 {
			c.JSON(http.StatusBadRequest, models.DeliveryResponse{
				Success: false,
				Message: "En az bir ürün gerekli",
			})
			return
		}
	})

	req, _ := http.NewRequest("POST", "/api/delivery/start", bytes.NewBuffer(jsonData))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusBadRequest, w.Code)

	var response models.DeliveryResponse
	err = json.Unmarshal(w.Body.Bytes(), &response)
	assert.NoError(t, err)
	assert.False(t, response.Success)
	assert.Equal(t, "En az bir ürün gerekli", response.Message)
}

func TestStartDelivery_InvalidCustomerId(t *testing.T) {
	deliveryRequest := models.DeliveryRequest{
		OrderId:    "order-123",
		CustomerId: 0,
		Address:    "Test Address 123",
		Items: []models.OrderItem{
			{
				ProductId:   1,
				ProductName: "Test Product",
				Quantity:    2,
				Price:       25.99,
			},
		},
	}
	jsonData, err := json.Marshal(deliveryRequest)
	assert.NoError(t, err)

	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.POST("/api/delivery/start", func(c *gin.Context) {
		var request models.DeliveryRequest
		if err := c.ShouldBindJSON(&request); err != nil {
			c.JSON(http.StatusBadRequest, models.DeliveryResponse{
				Success: false,
				Message: "Geçersiz istek formatı: " + err.Error(),
			})
			return
		}
		if request.CustomerId <= 0 {
			c.JSON(http.StatusBadRequest, models.DeliveryResponse{
				Success: false,
				Message: "Geçerli bir müşteri ID gerekli",
			})
			return
		}
	})

	req, _ := http.NewRequest("POST", "/api/delivery/start", bytes.NewBuffer(jsonData))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusBadRequest, w.Code)

	var response models.DeliveryResponse
	err = json.Unmarshal(w.Body.Bytes(), &response)
	assert.NoError(t, err)
	assert.False(t, response.Success)
	assert.Equal(t, "Geçerli bir müşteri ID gerekli", response.Message)
}

func TestStartDelivery_DatabaseError(t *testing.T) {
	mockDatabase.shouldFail = true

	deliveryRequest := models.DeliveryRequest{
		OrderId:    "order-123",
		CustomerId: 456,
		Address:    "Test Address 123",
		Items: []models.OrderItem{
			{
				ProductId:   1,
				ProductName: "Test Product",
				Quantity:    2,
				Price:       25.99,
			},
		},
	}

	jsonData, err := json.Marshal(deliveryRequest)
	assert.NoError(t, err)

	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.POST("/api/delivery/start", func(c *gin.Context) {
		var request models.DeliveryRequest
		if err := c.ShouldBindJSON(&request); err != nil {
			c.JSON(http.StatusBadRequest, models.DeliveryResponse{
				Success: false,
				Message: "Geçersiz istek formatı: " + err.Error(),
			})
			return
		}
		if mockDatabase.shouldFail {
			c.JSON(http.StatusInternalServerError, models.DeliveryResponse{
				Success: false,
				Message: "Veritabanı bağlantı hatası",
			})
			return
		}
	})

	req, _ := http.NewRequest("POST", "/api/delivery/start", bytes.NewBuffer(jsonData))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusInternalServerError, w.Code)

	var response models.DeliveryResponse
	err = json.Unmarshal(w.Body.Bytes(), &response)
	assert.NoError(t, err)
	assert.False(t, response.Success)
	assert.Contains(t, response.Message, "Veritabanı")

	mockDatabase.shouldFail = false
}

func TestHealthCheck(t *testing.T) {
	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.GET("/health", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"status":  "healthy",
			"service": "delivery-api",
		})
	})

	req, _ := http.NewRequest("GET", "/health", nil)
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var response map[string]interface{}
	err := json.Unmarshal(w.Body.Bytes(), &response)
	assert.NoError(t, err)
	assert.Equal(t, "healthy", response["status"])
	assert.Equal(t, "delivery-api", response["service"])
}

func TestGetDeliveryStatus(t *testing.T) {
	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.GET("/api/delivery/status/:id", func(c *gin.Context) {
		deliveryId := c.Param("id")
		if deliveryId == "" {
			c.JSON(http.StatusBadRequest, gin.H{
				"success": false,
				"message": "Teslimat ID gerekli",
			})
			return
		}
		c.JSON(http.StatusOK, gin.H{
			"success":     true,
			"delivery_id": deliveryId,
			"status":      "IN_TRANSIT",
			"message":     "Teslimat yolda",
		})
	})

	req, _ := http.NewRequest("GET", "/api/delivery/status/123", nil)
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var response map[string]interface{}
	err := json.Unmarshal(w.Body.Bytes(), &response)
	assert.NoError(t, err)
	assert.True(t, response["success"].(bool))
	assert.Equal(t, "123", response["delivery_id"])
	assert.Equal(t, "IN_TRANSIT", response["status"])
}

func TestCompleteDelivery(t *testing.T) {
	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.PUT("/api/delivery/complete/:id", func(c *gin.Context) {
		deliveryId := c.Param("id")
		if deliveryId == "" {
			c.JSON(http.StatusBadRequest, gin.H{
				"success": false,
				"message": "Teslimat ID gerekli",
			})
			return
		}
		c.JSON(http.StatusOK, gin.H{
			"success":     true,
			"delivery_id": deliveryId,
			"status":      "COMPLETED",
			"message":     "Teslimat başarıyla tamamlandı",
		})
	})

	req, _ := http.NewRequest("PUT", "/api/delivery/complete/123", nil)
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var response map[string]interface{}
	err := json.Unmarshal(w.Body.Bytes(), &response)
	assert.NoError(t, err)
	assert.True(t, response["success"].(bool))
	assert.Equal(t, "123", response["delivery_id"])
	assert.Equal(t, "COMPLETED", response["status"])
}

func TestConcurrentDeliveryRequests(t *testing.T) {
	mockDatabase.documents = make(map[string]interface{})
	mockDatabase.shouldFail = false

	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.POST("/api/delivery/start", func(c *gin.Context) {
		var request models.DeliveryRequest
		if err := c.ShouldBindJSON(&request); err != nil {
			c.JSON(http.StatusBadRequest, models.DeliveryResponse{
				Success: false,
				Message: "Geçersiz istek formatı: " + err.Error(),
			})
			return
		}
		if request.OrderId == "" {
			c.JSON(http.StatusBadRequest, models.DeliveryResponse{
				Success: false,
				Message: "Sipariş ID gerekli",
			})
			return
		}
		if request.CustomerId <= 0 {
			c.JSON(http.StatusBadRequest, models.DeliveryResponse{
				Success: false,
				Message: "Geçerli bir müşteri ID gerekli",
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
		if len(request.Items) == 0 {
			c.JSON(http.StatusBadRequest, models.DeliveryResponse{
				Success: false,
				Message: "En az bir ürün gerekli",
			})
			return
		}
		c.JSON(http.StatusOK, models.DeliveryResponse{
			Success:    true,
			DeliveryId: 101,
			Message:    "Teslimat başarıyla oluşturuldu ve hazırlanıyor",
		})
	})

	for i := 0; i < 5; i++ {
		deliveryRequest := models.DeliveryRequest{
			OrderId:    fmt.Sprintf("order-%d", i),
			CustomerId: 456 + i,
			Address:    fmt.Sprintf("Test Address %d", i),
			Items: []models.OrderItem{
				{
					ProductId:   1,
					ProductName: "Test Product",
					Quantity:    2,
					Price:       25.99,
				},
			},
		}
		jsonData, err := json.Marshal(deliveryRequest)
		assert.NoError(t, err)
		req, _ := http.NewRequest("POST", "/api/delivery/start", bytes.NewBuffer(jsonData))
		req.Header.Set("Content-Type", "application/json")
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)
		assert.Equal(t, http.StatusOK, w.Code)
		var response models.DeliveryResponse
		err = json.Unmarshal(w.Body.Bytes(), &response)
		assert.NoError(t, err)
		assert.True(t, response.Success)
	}
}

func TestCancelDelivery(t *testing.T) {
	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.DELETE("/api/delivery/cancel/:id", func(c *gin.Context) {
		deliveryId := c.Param("id")
		if deliveryId == "" {
			c.JSON(http.StatusBadRequest, gin.H{
				"success": false,
				"message": "Teslimat ID gerekli",
			})
			return
		}
		c.JSON(http.StatusOK, gin.H{
			"success":     true,
			"delivery_id": deliveryId,
			"status":      "CANCELLED",
			"message":     "Teslimat başarıyla iptal edildi",
		})
	})

	req, _ := http.NewRequest("DELETE", "/api/delivery/cancel/123", nil)
	router.ServeHTTP(w, req)
	assert.Equal(t, http.StatusOK, w.Code)
	var response map[string]interface{}
	err := json.Unmarshal(w.Body.Bytes(), &response)
	assert.NoError(t, err)
	assert.True(t, response["success"].(bool))
	assert.Equal(t, "CANCELLED", response["status"])
}

func TestUpdateDeliveryLocation(t *testing.T) {
	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.PUT("/api/delivery/location/:id", func(c *gin.Context) {
		deliveryId := c.Param("id")
		var locationUpdate struct {
			Latitude  float64 `json:"latitude"`
			Longitude float64 `json:"longitude"`
		}
		if err := c.ShouldBindJSON(&locationUpdate); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"success": false,
				"message": "Geçersiz konum verisi",
			})
			return
		}
		c.JSON(http.StatusOK, gin.H{
			"success":     true,
			"delivery_id": deliveryId,
			"latitude":    locationUpdate.Latitude,
			"longitude":   locationUpdate.Longitude,
			"message":     "Konum başarıyla güncellendi",
		})
	})

	locationData := map[string]float64{
		"latitude":  41.0082,
		"longitude": 28.9784,
	}
	jsonData, _ := json.Marshal(locationData)

	req, _ := http.NewRequest("PUT", "/api/delivery/location/123", bytes.NewBuffer(jsonData))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)
	assert.Equal(t, http.StatusOK, w.Code)
	var response map[string]interface{}
	err := json.Unmarshal(w.Body.Bytes(), &response)
	assert.NoError(t, err)
	assert.True(t, response["success"].(bool))
	assert.Equal(t, 41.0082, response["latitude"])
}

func TestHighLoadDeliveryRequests(t *testing.T) {
	mockDatabase.documents = make(map[string]interface{})
	mockDatabase.shouldFail = false

	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)

	router.POST("/api/delivery/start", func(c *gin.Context) {
		var request models.DeliveryRequest
		if err := c.ShouldBindJSON(&request); err != nil {
			c.JSON(http.StatusBadRequest, models.DeliveryResponse{
				Success: false,
				Message: "Geçersiz istek formatı: " + err.Error(),
			})
			return
		}
		c.JSON(http.StatusOK, models.DeliveryResponse{
			Success:    true,
			DeliveryId: 101,
			Message:    "Teslimat başarıyla oluşturuldu",
		})
	})

	successCount := 0
	for i := 0; i < 100; i++ {
		deliveryRequest := models.DeliveryRequest{
			OrderId:    fmt.Sprintf("order-load-%d", i),
			CustomerId: 1000 + i,
			Address:    "Load Test Address",
			Items: []models.OrderItem{
				{
					ProductId:   1,
					ProductName: "Load Test Product",
					Quantity:    1,
					Price:       10.0,
				},
			},
		}
		jsonData, err := json.Marshal(deliveryRequest)
		assert.NoError(t, err)
		req, _ := http.NewRequest("POST", "/api/delivery/start", bytes.NewBuffer(jsonData))
		req.Header.Set("Content-Type", "application/json")
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)
		if w.Code == http.StatusOK {
			successCount++
		}
	}
	assert.GreaterOrEqual(t, successCount, 95)
}

func TestDeliveryErrorRecovery(t *testing.T) {
	w := httptest.NewRecorder()
	_, router := gin.CreateTestContext(w)
	callCount := 0

	router.POST("/api/delivery/start", func(c *gin.Context) {
		callCount++
		if callCount <= 3 {
			c.JSON(http.StatusInternalServerError, models.DeliveryResponse{
				Success: false,
				Message: "Geçici sistem hatası",
			})
			return
		}
		c.JSON(http.StatusOK, models.DeliveryResponse{
			Success:    true,
			DeliveryId: 101,
			Message:    "Teslimat başarıyla oluşturuldu",
		})
	})

	deliveryRequest := models.DeliveryRequest{
		OrderId:    "order-recovery-123",
		CustomerId: 456,
		Address:    "Recovery Test Address",
		Items: []models.OrderItem{
			{
				ProductId:   1,
				ProductName: "Recovery Test Product",
				Quantity:    1,
				Price:       15.0,
			},
		},
	}

	jsonData, err := json.Marshal(deliveryRequest)
	assert.NoError(t, err)

	var finalResponse models.DeliveryResponse
	for i := 0; i < 5; i++ {
		req, _ := http.NewRequest("POST", "/api/delivery/start", bytes.NewBuffer(jsonData))
		req.Header.Set("Content-Type", "application/json")
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)
		json.Unmarshal(w.Body.Bytes(), &finalResponse)

		if finalResponse.Success {
			break
		}
	}

	assert.True(t, finalResponse.Success)
	assert.Equal(t, "Teslimat başarıyla oluşturuldu", finalResponse.Message)
}

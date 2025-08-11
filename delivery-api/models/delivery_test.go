package models

import (
	"encoding/json"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
)

func TestDeliveryRequest_JSONMarshaling(t *testing.T) {
	request := DeliveryRequest{
		OrderId:    "order-123",
		CustomerId: 456,
		Address:    "Test Address 123",
		Items: []OrderItem{
			{
				ProductId:   1,
				ProductName: "Test Product",
				Quantity:    2,
				Price:       25.99,
			},
		},
	}

	jsonData, err := json.Marshal(request)
	assert.NoError(t, err)
	assert.Contains(t, string(jsonData), "order-123")
	assert.Contains(t, string(jsonData), "Test Product")

	var unmarshaledRequest DeliveryRequest
	err = json.Unmarshal(jsonData, &unmarshaledRequest)
	assert.NoError(t, err)
	assert.Equal(t, request.OrderId, unmarshaledRequest.OrderId)
	assert.Equal(t, request.CustomerId, unmarshaledRequest.CustomerId)
	assert.Equal(t, request.Address, unmarshaledRequest.Address)
	assert.Len(t, unmarshaledRequest.Items, 1)
}

func TestDeliveryResponse_JSONMarshaling(t *testing.T) {
	response := DeliveryResponse{
		Success:    true,
		DeliveryId: 123,
		Message:    "Test message",
	}

	jsonData, err := json.Marshal(response)
	assert.NoError(t, err)
	assert.Contains(t, string(jsonData), "true")
	assert.Contains(t, string(jsonData), "123")
	assert.Contains(t, string(jsonData), "Test message")

	var unmarshaledResponse DeliveryResponse
	err = json.Unmarshal(jsonData, &unmarshaledResponse)
	assert.NoError(t, err)
	assert.Equal(t, response.Success, unmarshaledResponse.Success)
	assert.Equal(t, response.DeliveryId, unmarshaledResponse.DeliveryId)
	assert.Equal(t, response.Message, unmarshaledResponse.Message)
}

func TestDelivery_JSONMarshaling(t *testing.T) {
	now := time.Now()
	delivery := Delivery{
		ID:         "delivery::123",
		Type:       "delivery",
		DeliveryId: 123,
		OrderId:    "order-456",
		CustomerId: 789,
		Address:    "Test Address",
		Status:     "PREPARING",
		Items: []OrderItem{
			{
				ProductId:   1,
				ProductName: "Test Product",
				Quantity:    2,
				Price:       25.99,
			},
		},
		CreatedAt: now,
		UpdatedAt: now,
	}

	jsonData, err := json.Marshal(delivery)
	assert.NoError(t, err)
	assert.Contains(t, string(jsonData), "delivery::123")
	assert.Contains(t, string(jsonData), "PREPARING")

	var unmarshaledDelivery Delivery
	err = json.Unmarshal(jsonData, &unmarshaledDelivery)
	assert.NoError(t, err)
	assert.Equal(t, delivery.ID, unmarshaledDelivery.ID)
	assert.Equal(t, delivery.Type, unmarshaledDelivery.Type)
	assert.Equal(t, delivery.DeliveryId, unmarshaledDelivery.DeliveryId)
	assert.Equal(t, delivery.Status, unmarshaledDelivery.Status)
}

func TestOrderItem_JSONMarshaling(t *testing.T) {
	item := OrderItem{
		ProductId:   1,
		ProductName: "Test Product",
		Quantity:    2,
		Price:       25.99,
	}

	jsonData, err := json.Marshal(item)
	assert.NoError(t, err)
	assert.Contains(t, string(jsonData), "Test Product")
	assert.Contains(t, string(jsonData), "25.99")

	var unmarshaledItem OrderItem
	err = json.Unmarshal(jsonData, &unmarshaledItem)
	assert.NoError(t, err)
	assert.Equal(t, item.ProductId, unmarshaledItem.ProductId)
	assert.Equal(t, item.ProductName, unmarshaledItem.ProductName)
	assert.Equal(t, item.Quantity, unmarshaledItem.Quantity)
	assert.Equal(t, item.Price, unmarshaledItem.Price)
}

func TestDeliveryRequest_EmptyFields(t *testing.T) {
	request := DeliveryRequest{}

	jsonData, err := json.Marshal(request)
	assert.NoError(t, err)

	var unmarshaledRequest DeliveryRequest
	err = json.Unmarshal(jsonData, &unmarshaledRequest)
	assert.NoError(t, err)
	assert.Empty(t, unmarshaledRequest.OrderId)
	assert.Zero(t, unmarshaledRequest.CustomerId)
	assert.Empty(t, unmarshaledRequest.Address)
	assert.Nil(t, unmarshaledRequest.Items)
}

func TestDeliveryResponse_ErrorCase(t *testing.T) {
	response := DeliveryResponse{
		Success: false,
		Message: "Error occurred",
	}

	jsonData, err := json.Marshal(response)
	assert.NoError(t, err)
	assert.Contains(t, string(jsonData), "false")
	assert.Contains(t, string(jsonData), "Error occurred")
	assert.NotContains(t, string(jsonData), "deliveryId")
}

func TestDelivery_StatusValues(t *testing.T) {
	validStatuses := []string{"PREPARING", "ON_WAY", "DELIVERED", "CANCELLED"}

	for _, status := range validStatuses {
		delivery := Delivery{
			Status: status,
		}

		jsonData, err := json.Marshal(delivery)
		assert.NoError(t, err)
		assert.Contains(t, string(jsonData), status)

		var unmarshaledDelivery Delivery
		err = json.Unmarshal(jsonData, &unmarshaledDelivery)
		assert.NoError(t, err)
		assert.Equal(t, status, unmarshaledDelivery.Status)
	}
}

func TestOrderItem_ZeroValues(t *testing.T) {
	item := OrderItem{
		ProductId:   0,
		ProductName: "",
		Quantity:    0,
		Price:       0.0,
	}

	jsonData, err := json.Marshal(item)
	assert.NoError(t, err)

	var unmarshaledItem OrderItem
	err = json.Unmarshal(jsonData, &unmarshaledItem)
	assert.NoError(t, err)
	assert.Zero(t, unmarshaledItem.ProductId)
	assert.Empty(t, unmarshaledItem.ProductName)
	assert.Zero(t, unmarshaledItem.Quantity)
	assert.Zero(t, unmarshaledItem.Price)
}

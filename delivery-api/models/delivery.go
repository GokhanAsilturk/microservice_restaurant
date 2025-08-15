package models

import (
	"time"
)


type DeliveryStatus string

const (
	StatusPending     DeliveryStatus = "PENDING"
	StatusAssigned    DeliveryStatus = "ASSIGNED"
	StatusPickedUp    DeliveryStatus = "PICKED_UP"
	StatusInTransit   DeliveryStatus = "IN_TRANSIT"
	StatusDelivered   DeliveryStatus = "DELIVERED"
	StatusCancelled   DeliveryStatus = "CANCELLED"
	StatusFailed      DeliveryStatus = "FAILED"
)


func (ds DeliveryStatus) IsValid() bool {
	switch ds {
	case StatusPending, StatusAssigned, StatusPickedUp, StatusInTransit, StatusDelivered, StatusCancelled, StatusFailed:
		return true
	default:
		return false
	}
}

type OrderItem struct {
	ProductId   int     `json:"productId"`
	ProductName string  `json:"productName"`
	Quantity    int     `json:"quantity"`
	Price       float64 `json:"price"`
}

// Teslimat talebi için model
type DeliveryRequest struct {
	OrderId     string      `json:"orderId"`
	CustomerId  int     `json:"customerId"`
	Address     string     `json:"address"`
	Items       []OrderItem `json:"items"`
}

// Teslimat yanıtı için model
type DeliveryResponse struct {
	Success     bool   `json:"success"`
	DeliveryId  int    `json:"deliveryId,omitempty"`
	Message     string `json:"message"`
}

// Teslimat bilgisi modeli (Couchbase için güncellenmiş)
type Delivery struct {
	ID         string         `json:"id,omitempty"`         // Couchbase document ID
	Type       string         `json:"type"`                 // Couchbase document type
	DeliveryId int            `json:"deliveryId"`
	OrderId    string         `json:"orderId"`
	CustomerId int            `json:"customerId"`
	Address    string         `json:"address"`
	Status     DeliveryStatus `json:"status"`
	Items      []OrderItem    `json:"items"`
	CreatedAt  time.Time      `json:"createdAt"`
	UpdatedAt  time.Time      `json:"updatedAt"`
}

package models

import (
	"time"
	"delivery-api/models/domain"
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
	CustomerId  int         `json:"customerId"`
	Address     string      `json:"address"`
	Items       []OrderItem `json:"items"`
}

func (dr *DeliveryRequest) ToDomain() *domain.DeliveryDomain {
	domainItems := make([]domain.OrderItemDomain, len(dr.Items))
	for i, item := range dr.Items {
		domainItems[i] = domain.OrderItemDomain{
			ProductID:   item.ProductId,
			ProductName: item.ProductName,
			Quantity:    item.Quantity,
			Price:       item.Price,
		}
	}

	return &domain.DeliveryDomain{
		OrderID:    dr.OrderId,
		CustomerID: dr.CustomerId,
		Address:    dr.Address,
		Items:      domainItems,
		Status:     domain.DeliveryStatus(StatusPending),
		CreatedAt:  time.Now(),
		UpdatedAt:  time.Now(),
	}
}

func DomainToEntity(d *domain.DeliveryDomain) *DeliveryEntity {
	entityItems := make([]OrderItem, len(d.Items))
	for i, item := range d.Items {
		entityItems[i] = OrderItem{
			ProductId:   item.ProductID,
			ProductName: item.ProductName,
			Quantity:    item.Quantity,
			Price:       item.Price,
		}
	}

	return &DeliveryEntity{
		ID:          d.ID,
		OrderID:     d.OrderID,
		CustomerID:  d.CustomerID,
		Address:     d.Address,
		Items:       entityItems,
		Status:      DeliveryStatus(d.Status),
		CreatedAt:   d.CreatedAt,
		UpdatedAt:   d.UpdatedAt,
		AssignedAt:  d.AssignedAt,
		DeliveredAt: d.DeliveredAt,
	}
}

func EntityToDomain(entity *DeliveryEntity) *domain.DeliveryDomain {
	domainItems := make([]domain.OrderItemDomain, len(entity.Items))
	for i, item := range entity.Items {
		domainItems[i] = domain.OrderItemDomain{
			ProductID:   item.ProductId,
			ProductName: item.ProductName,
			Quantity:    item.Quantity,
			Price:       item.Price,
		}
	}

	return &domain.DeliveryDomain{
		ID:          entity.ID,
		OrderID:     entity.OrderID,
		CustomerID:  entity.CustomerID,
		Address:     entity.Address,
		Items:       domainItems,
		Status:      domain.DeliveryStatus(entity.Status),
		CreatedAt:   entity.CreatedAt,
		UpdatedAt:   entity.UpdatedAt,
		AssignedAt:  entity.AssignedAt,
		DeliveredAt: entity.DeliveredAt,
	}
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

package models

import (
	"time"
	"errors"
)

type DeliveryEntity struct {
	ID          string        `json:"id" couchbase:"id"`
	OrderID     string        `json:"orderId" couchbase:"orderId"`
	CustomerID  int           `json:"customerId" couchbase:"customerId"`
	Address     string        `json:"address" couchbase:"address"`
	Items       []OrderItem   `json:"items" couchbase:"items"`
	Status      DeliveryStatus `json:"status" couchbase:"status"`
	CreatedAt   time.Time     `json:"createdAt" couchbase:"createdAt"`
	UpdatedAt   time.Time     `json:"updatedAt" couchbase:"updatedAt"`
	AssignedAt  *time.Time    `json:"assignedAt,omitempty" couchbase:"assignedAt,omitempty"`
	DeliveredAt *time.Time    `json:"deliveredAt,omitempty" couchbase:"deliveredAt,omitempty"`
}

func NewValidationError(message string) error {
	return errors.New(message)
}

package domain

import (
	"errors"
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

func NewValidationError(message string) error {
	return errors.New(message)
}

type DeliveryDomain struct {
	ID          string
	OrderID     string
	CustomerID  int
	Address     string
	Items       []OrderItemDomain
	Status      DeliveryStatus
	CreatedAt   time.Time
	UpdatedAt   time.Time
	AssignedAt  *time.Time
	DeliveredAt *time.Time
}

type OrderItemDomain struct {
	ProductID   int
	ProductName string
	Quantity    int
	Price       float64
}

func (d *DeliveryDomain) CanAssign() bool {
	return d.Status == StatusPending
}

func (d *DeliveryDomain) CanPickup() bool {
	return d.Status == StatusAssigned
}

func (d *DeliveryDomain) CanDeliver() bool {
	return d.Status == StatusInTransit
}

func (d *DeliveryDomain) CanCancel() bool {
	return d.Status == StatusPending || d.Status == StatusAssigned
}

func (d *DeliveryDomain) Assign() error {
	if !d.CanAssign() {
		return NewValidationError("Teslimat atama için uygun durumda değil")
	}
	d.Status = StatusAssigned
	now := time.Now()
	d.AssignedAt = &now
	d.UpdatedAt = now
	return nil
}

func (d *DeliveryDomain) MarkAsPickedUp() error {
	if !d.CanPickup() {
		return NewValidationError("Teslimat teslim alma için uygun durumda değil")
	}
	d.Status = StatusPickedUp
	d.UpdatedAt = time.Now()
	return nil
}

func (d *DeliveryDomain) MarkAsInTransit() error {
	if d.Status != StatusPickedUp {
		return NewValidationError("Teslimat transit için uygun durumda değil")
	}
	d.Status = StatusInTransit
	d.UpdatedAt = time.Now()
	return nil
}

func (d *DeliveryDomain) MarkAsDelivered() error {
	if !d.CanDeliver() {
		return NewValidationError("Teslimat tamamlama için uygun durumda değil")
	}
	d.Status = StatusDelivered
	now := time.Now()
	d.DeliveredAt = &now
	d.UpdatedAt = now
	return nil
}

func (d *DeliveryDomain) Cancel() error {
	if !d.CanCancel() {
		return NewValidationError("Teslimat iptal için uygun durumda değil")
	}
	d.Status = StatusCancelled
	d.UpdatedAt = time.Now()
	return nil
}

func (d *DeliveryDomain) CalculateTotalAmount() float64 {
	total := 0.0
	for _, item := range d.Items {
		total += item.Price * float64(item.Quantity)
	}
	return total
}

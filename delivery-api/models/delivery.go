package models

type OrderItem struct {
	ProductId   int64   `json:"productId"`
	ProductName string  `json:"productName"`
	Quantity    int     `json:"quantity"`
	Price       float64 `json:"price"`
}

// Teslimat talebi için model
type DeliveryRequest struct {
	OrderId     int64      `json:"orderId"`
	CustomerId  int64      `json:"customerId"`
	Address     string     `json:"address"`
	Items       []OrderItem `json:"items"`
}

// Teslimat yanıtı için model
type DeliveryResponse struct {
	Success     bool   `json:"success"`
	DeliveryId  string `json:"deliveryId,omitempty"`
	Message     string `json:"message"`
}

// Teslimat bilgisi modeli
type Delivery struct {
	DeliveryId  string     `json:"deliveryId"`
	OrderId     int64      `json:"orderId"`
	CustomerId  int64      `json:"customerId"`
	Address     string     `json:"address"`
	Status      string     `json:"status"`
	Items       []OrderItem `json:"items"`
}

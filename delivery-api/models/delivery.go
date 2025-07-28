package models

type OrderItem struct {
	ProductId   int   `json:"productId"`
	ProductName string  `json:"productName"`
	Quantity    int     `json:"quantity"`
	Price       float64 `json:"price"`
}

// Teslimat talebi için model
type DeliveryRequest struct {
	OrderId     int      `json:"orderId"`
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

// Teslimat bilgisi modeli
type Delivery struct {
	DeliveryId  int     `json:"deliveryId"`
	OrderId     int      `json:"orderId"`
	CustomerId  int      `json:"customerId"`
	Address     string     `json:"address"`
	Status      string     `json:"status"`
	Items       []OrderItem `json:"items"`
}

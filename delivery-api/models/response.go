package models

import (
	"time"
)

type ApiResponse struct {
	Success   bool        `json:"success"`
	Message   string      `json:"message,omitempty"`
	Data      interface{} `json:"data,omitempty"`
	Timestamp time.Time   `json:"timestamp"`
	ErrorCode string      `json:"errorCode,omitempty"`
}

func SuccessResponse(data interface{}) ApiResponse {
	return ApiResponse{
		Success:   true,
		Data:      data,
		Timestamp: time.Now(),
	}
}

func SuccessResponseWithMessage(data interface{}, message string) ApiResponse {
	return ApiResponse{
		Success:   true,
		Message:   message,
		Data:      data,
		Timestamp: time.Now(),
	}
}

func ErrorResponse(message string) ApiResponse {
	return ApiResponse{
		Success:   false,
		Message:   message,
		Timestamp: time.Now(),
	}
}

func ErrorResponseWithCode(message string, errorCode string) ApiResponse {
	return ApiResponse{
		Success:   false,
		Message:   message,
		ErrorCode: errorCode,
		Timestamp: time.Now(),
	}
}

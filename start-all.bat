@echo off
REM Restaurant API is starting...
cd restaurant-api
start "Restaurant API" cmd /k mvn spring-boot:run
cd ..

REM Delivery API is starting...
cd delivery-api
start "Delivery API" cmd /k go run main.go
cd ..

REM Order API is starting...
cd order_api
start "Order API" cmd /k mvn spring-boot:run
cd ..

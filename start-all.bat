@echo off
REM Restaurant API is starting...
cd restaurant-api
start mvn spring-boot:run
cd ..

REM Delivery API is starting...
cd delivery-api
start go run main.go
cd ..

REM Order API is starting...
cd order_api
start mvn spring-boot:run
cd ..

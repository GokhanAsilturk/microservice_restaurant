# PowerShell başlat scripti
Write-Host "Restaurant API is starting..." -ForegroundColor Green
Push-Location -Path ".\restaurant-api"
Start-Process powershell -ArgumentList "-NoExit", "-Command", ".\mvnw spring-boot:run"
Pop-Location

Write-Host "Delivery API is starting..." -ForegroundColor Yellow
Push-Location -Path ".\delivery-api"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "go run ."
Pop-Location

Write-Host "Order API is starting..." -ForegroundColor Cyan
Push-Location -Path ".\order_api"
Start-Process powershell -ArgumentList "-NoExit", "-Command", ".\mvnw spring-boot:run"
Pop-Location

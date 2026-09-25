$ErrorActionPreference = "Stop"

# Ensure environment
$env:JAVA_HOME = "C:\Users\mkt.int1\tools\jdk-21.0.12.1+1"
$env:MAVEN_HOME = "C:\Users\mkt.int1\tools\apache-maven-3.9.9"
$env:PATH = "$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:PATH"

Write-Output "=== Starting RideLink Microservices Platform ==="

# 1. Start MongoDB
& "$PSScriptRoot\start-mongodb.ps1"
Start-Sleep -Seconds 2

# Microservices configuration
$services = @(
    @{ Name = "Account Service"; Port = 8081; Dir = "account-service" },
    @{ Name = "Driver Service";  Port = 8082; Dir = "driver-service" },
    @{ Name = "Ride Service";    Port = 8083; Dir = "ride-service" },
    @{ Name = "Fare Service";    Port = 8084; Dir = "fare-service" }
)

$root = Resolve-Path "$PSScriptRoot\.."

foreach ($svc in $services) {
    Write-Output "Starting $($svc.Name) on port $($svc.Port)..."
    $svcPath = Join-Path $root $svc.Dir
    $mvnCmd = "mvn spring-boot:run"
    Start-Process -FilePath "powershell.exe" -ArgumentList "-NoExit -Command `"cd '$svcPath'; `$env:JAVA_HOME='$($env:JAVA_HOME)'; `$env:PATH='$($env:PATH)'; mvn spring-boot:run`"" -WindowStyle Minimized
    Start-Sleep -Seconds 2
}

Write-Output ""
Write-Output "All 4 microservices are launching:"
Write-Output "  - Account Service: http://localhost:8081/swagger-ui.html"
Write-Output "  - Driver Service:  http://localhost:8082/swagger-ui.html"
Write-Output "  - Ride Service:    http://localhost:8083/swagger-ui.html"
Write-Output "  - Fare Service:    http://localhost:8084/swagger-ui.html"
Write-Output ""
Write-Output "Use scripts\stop-all-services.ps1 to shut down."

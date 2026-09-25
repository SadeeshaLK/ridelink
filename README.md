# RideLink - Backend Microservices Platform

[![CI Pipeline](https://github.com/your-org/ridelink/actions/workflows/ci.yml/badge.svg)](https://github.com/your-org/ridelink/actions)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

RideLink is a backend microservices solution engineered for a fictional ride-sharing platform. Built for **IT3130 - Application Development (Group Assignment)** using **Java 21**, **Spring Boot 3.3.4**, and **MongoDB**.

---

## 1. Project Organization and Service Ownership

| Microservice | Port | Database | Primary Owner | Responsibilities |
|---|---|---|---|---|
| **Account Service** | `8081` | `ridelink_account_db` | **Member 1** | Passenger & driver registration, BCrypt password hashing, JWT token issuance & validation, RBAC (`ROLE_PASSENGER`, `ROLE_DRIVER`, `ROLE_ADMIN`), user profile lifecycle, and account status management (`ACTIVE`, `SUSPENDED`, `DEACTIVATED`). |
| **Driver & Vehicle Service** | `8082` | `ridelink_driver_db` | **Member 2** | Driver operational profiles, vehicle registration (SEDAN, SUV, HATCHBACK, MOTORBIKE), availability state toggle (`AVAILABLE`, `BUSY`, `OFFLINE`), simulated GPS tracking, Haversine geospatial proximity search. |
| **Ride Management Service** | `8083` | `ridelink_ride_db` | **Member 3** | Central ride booking orchestrator, pickup/destination coordinate handling, driver matching dispatch, full lifecycle state machine (`REQUESTED` $\rightarrow$ `ASSIGNED` $\rightarrow$ `ACCEPTED` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `COMPLETED` / `CANCELLED`), transition validation. |
| **Fare & Payment Service** | `8084` | `ridelink_fare_db` | **Member 4** | Upfront fare estimation, final fare calculation via documented pricing rule (base fare + distance charge + duration charge * vehicle multiplier * surge), simulated payment processing (`PAID`, `FAILED`), receipt generation. |

---

## 2. Prerequisites
- **Java Development Kit (JDK):** Version 21 (Eclipse Temurin 21 Recommended).
- **Apache Maven:** Version 3.9+ (or use the included `./mvnw` / `mvnw.cmd` wrapper).
- **MongoDB:** Community Server 7.0+ running on port `27017` (Portable user-space MongoDB setup included).
- **Postman:** Version 10+ (for API testing and viva demonstration).

---

## 3. Quick Start Guide

### Step 1: Start MongoDB
If MongoDB is not running as a local service, start the portable MongoDB server:
```powershell
.\scripts\start-mongodb.ps1
```
*MongoDB will listen on `mongodb://localhost:27017`.*

### Step 2: Build All Microservices
Run the Maven multi-module build and test suite:
```powershell
.\mvnw clean test
```
*Or on Windows CMD:*
```bat
mvnw.cmd clean test
```

### Step 3: Run All Microservices
Launch all 4 microservices simultaneously using the provided automated script:
```powershell
.\scripts\run-all-services.ps1
```
Alternatively, each service can be launched individually in separate terminals:
```bash
# Terminal 1: Account Service (Port 8081)
cd account-service
../mvnw spring-boot:run

# Terminal 2: Driver & Vehicle Service (Port 8082)
cd driver-service
../mvnw spring-boot:run

# Terminal 3: Ride Management Service (Port 8083)
cd ride-service
../mvnw spring-boot:run

# Terminal 4: Fare & Payment Service (Port 8084)
cd fare-service
../mvnw spring-boot:run
```

### Step 4: Stop All Microservices
To gracefully shut down all running services and databases:
```powershell
.\scripts\stop-all-services.ps1
```

---

## 4. OpenAPI / Swagger UI Interactive Documentation

Once the services are running, interactive Swagger UI interfaces are available at:

- **Account Service:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)  
  *OpenAPI JSON:* [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)
- **Driver & Vehicle Service:** [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)  
  *OpenAPI JSON:* [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs)
- **Ride Management Service:** [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)  
  *OpenAPI JSON:* [http://localhost:8083/v3/api-docs](http://localhost:8083/v3/api-docs)
- **Fare & Payment Service:** [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)  
  *OpenAPI JSON:* [http://localhost:8084/v3/api-docs](http://localhost:8084/v3/api-docs)

---

## 5. Postman Collection & Automated Scenarios

The `postman/` directory includes the official Postman artifacts:
- **Collection:** `postman/RideLink.postman_collection.json`
- **Environment:** `postman/RideLink.postman_environment.json`

### Import into Postman:
1. Open Postman $\rightarrow$ Click **Import**.
2. Select both `RideLink.postman_collection.json` and `RideLink.postman_environment.json`.
3. Set the active environment to **RideLink Local Environment**.

### Scenarios Covered:
- **01 - Account Service:** Passenger and driver registration, JWT authentication, user profile view with Bearer token, token validation.
- **02 - Driver & Vehicle Service:** Driver operational onboarding, vehicle details, simulated GPS updates, availability toggle (`AVAILABLE`), and nearby proximity search.
- **03 - Fare & Payment Service:** Upfront estimate with formula, final fare calculation, simulated payment, receipt generation.
- **04 - Ride Lifecycle End-to-End Workflow:**
  - Step 1: Request Ride (`REQUESTED`)
  - Step 2: Assign Driver (`ASSIGNED`)
  - Step 3: Driver Accepts Ride (`ACCEPTED`)
  - Step 4: Driver Starts Trip (`IN_PROGRESS`)
  - Step 5: Complete Ride (`COMPLETED`) with automatic payment processing & driver release
  - Step 6: Payment Receipt Verification
- **05 - Negative Scenarios (Mandatory Requirements):**
  - Neg 1: Duplicate Registration (`HTTP 409 Conflict`)
  - Neg 2: Invalid Login Password (`HTTP 401 Unauthorized`)
  - Neg 3: Invalid State Transition - Attempting to cancel already `COMPLETED` ride (`HTTP 400 Bad Request`)
  - Neg 4: Simulated Payment Gateway Decline with `simulateFailure: true` (`HTTP 402 Payment Required`)
  - Neg 5: No Driver Available for High Radius Query (`HTTP 200 / empty list`)

---

## 6. Sample Test Credentials and Data

### Passenger Account:
- **Email:** `passenger1@ridelink.com`
- **Password:** `Pass@1234`
- **Role:** `ROLE_PASSENGER`

### Driver Account:
- **Email:** `driver1@ridelink.com`
- **Password:** `Drive@1234`
- **Role:** `ROLE_DRIVER`
- **Vehicle:** Toyota Prius (2021) | Plate: `WP-CAD-8899` | Tier: `SEDAN`
- **Simulated Location:** Latitude: `6.9271`, Longitude: `79.8612` (Colombo Fort)

---

## 7. Documented Pricing Rules

Fare calculation implemented in `Fare & Payment Service`:

$$\text{Estimated Fare} = \Big(\text{Base Fare} + (\text{Distance}_{\text{km}} \times \text{Rate}_{\text{km}}) + (\text{Duration}_{\text{min}} \times \text{Rate}_{\text{min}})\Big) \times \text{Vehicle Multiplier} \times \text{Surge}$$

$$\text{Final Fare} = \max\Big(\text{Base Fare} \times \text{Vehicle Multiplier},\; \text{Subtotal} - \text{Discount}\Big)$$

- **Base Fare:** $150.00\text{ LKR}$
- **Rate per Kilometer:** $80.00\text{ LKR/km}$
- **Rate per Minute:** $5.00\text{ LKR/min}$
- **Vehicle Multipliers:**
  - `MOTORBIKE`: $0.6\times$
  - `HATCHBACK`: $1.0\times$
  - `SEDAN`: $1.2\times$
  - `SUV`: $1.5\times$

---

## 8. Continuous Integration (CI)
GitHub Actions workflow is located at `.github/workflows/ci.yml`.  
Every commit or pull request automatically:
1. Provisions MongoDB 7.0 container.
2. Compiles all 4 Spring Boot microservices on JDK 21.
3. Executes 20+ automated unit tests.
4. Validates production JAR packaging.

---

## 9. Architecture & Sequence Diagrams
- **System Architecture & Decomposition:** [docs/architecture_diagram.md](docs/architecture_diagram.md)
- **Interservice Sequence Diagrams:** [docs/sequence_diagram.md](docs/sequence_diagram.md)
- **Technical Architecture Report:** [docs/RideLink_Technical_Report.md](docs/RideLink_Technical_Report.md)

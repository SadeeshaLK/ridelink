# IT3130 - Application Development | Group Assignment
# Technical Architecture Report: RideLink Backend Microservices Platform

**Academic Year:** 2026  
**Module:** IT3130 – Application Development  
**Project:** RideLink – Backend Microservices for a Ride-Sharing Platform  
**Target:** 30% Coursework Assessment  

---

## Executive Summary
RideLink is an enterprise-grade backend microservice platform engineered to manage the end-to-end operational workflows of a contemporary on-demand ride-hailing ecosystem. The platform is architected in accordance with domain-driven design (DDD) principles and implemented using **Java 21**, **Spring Boot 3.3.4**, and **MongoDB 7.0**. The platform completely decouples core business domains into four independent services: **Account Service**, **Driver & Vehicle Service**, **Ride Management Service**, and **Fare & Payment Service**. Each service enforces strict data isolation through independent databases, communicates via context-justified synchronous REST interfaces, implements role-based access control and bean validations, and is validated through comprehensive unit test suites, Postman collections, and continuous integration.

---

## 1. Architectural Reasoning & System Decomposition

### 1.1 Microservices Boundaries
The system is decomposed into four discrete, single-responsibility microservices:

1. **Account Service (`:8081`) - Member 1 Ownership:**
   - *Domain Responsibility:* Identity and Access Management (IAM), passenger/driver registration, BCrypt password hashing, stateless JWT issuance/validation, RBAC (`ROLE_PASSENGER`, `ROLE_DRIVER`, `ROLE_ADMIN`), user profile lifecycle, and account status enforcement (`ACTIVE`, `SUSPENDED`, `DEACTIVATED`).
   - *Persistence Boundary:* Dedicated `ridelink_account_db` database.

2. **Driver & Vehicle Service (`:8082`) - Member 2 Ownership:**
   - *Domain Responsibility:* Driver operational registration, vehicle specifications (make, model, year, license plate, vehicle tier: SEDAN, SUV, HATCHBACK, MOTORBIKE), driver availability state machine (`AVAILABLE`, `BUSY`, `OFFLINE`), simulated GPS coordinates, and Haversine geospatial proximity search.
   - *Persistence Boundary:* Dedicated `ridelink_driver_db` database.

3. **Ride Management Service (`:8083`) - Member 3 Ownership (System Orchestrator):**
   - *Domain Responsibility:* Ride booking requests, pickup/destination routing coordinates, driver assignment and dispatching orchestration, strict ride lifecycle state machine (`REQUESTED` $\rightarrow$ `ASSIGNED` $\rightarrow$ `ACCEPTED` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `COMPLETED` / `CANCELLED`), invalid transition prevention, and cross-service workflow coordination.
   - *Persistence Boundary:* Dedicated `ridelink_ride_db` database.

4. **Fare & Payment Service (`:8084`) - Member 4 Ownership:**
   - *Domain Responsibility:* Upfront fare estimation, dynamic final fare calculation via documented pricing rules, simulated payment processing with transaction references (`PAID`, `FAILED`), receipt generation, and payment audit logs.
   - *Persistence Boundary:* Dedicated `ridelink_fare_db` database.

### 1.2 Data Ownership & Database Isolation
To adhere strictly to microservices best practices and mandatory assignment constraints:
- **No Shared Databases:** No service directly connects to, queries, or modifies another service's database or collection.
- **Reference by Identifiers:** Services reference entities in external domains exclusively through stable, immutable string IDs (e.g. `passengerId`, `driverId`, `rideId`).
- **Independent Schema Evolution:** Each microservice can evolve its database schema (e.g. adding new fields to `DriverProfile` or `PaymentRecord`) without requiring schema migrations or coordination in other microservices.

### 1.3 Architectural Comparison: Microservices vs. Monolithic Architecture

| Evaluation Metric | RideLink Microservice Architecture | Monolithic Alternative |
|---|---|---|
| **Data Coupling** | Zero database coupling. Each service encapsulates its schema and data operations within its boundary. | High coupling. Shared schema where altering a table can cause cascading failures across unrelated modules. |
| **Fault Isolation** | High resilience. A failure in the payment gateway simulation or external provider does not prevent drivers from updating locations or users from registering. | Fragile. A critical crash or thread pool exhaustion in one component halts the entire application. |
| **Team Scalability** | Excellent. Each of the 4 group members can independently develop, test, debug, and commit to their assigned service with zero Git branch collisions. | High merge conflict rate and tight interdependencies slowing down concurrent feature development. |
| **Operational Trade-offs** | Requires handling interservice network latencies, timeouts, and distributed testing. | Simpler local single-command execution and unified logging. |

---

## 2. Interservice Communication & Interface Design

### 2.1 Communication Strategy & Justification
The platform employs **Synchronous RESTful JSON HTTP APIs** utilizing Spring Boot 3's modern `RestClient`:

1. **Account Validation during Booking:**
   - *Workflow:* When a ride is requested, `Ride Management Service` invokes `Account Service` (`GET /api/users/{id}`) to verify that the passenger exists and holds an `ACTIVE` status.
   - *Justification:* Synchronous request-response is necessary because booking cannot proceed if the account is deactivated or fraudulent.

2. **Proximity Driver Discovery & Assignment:**
   - *Workflow:* `Ride Management Service` calls `Driver & Vehicle Service` (`POST /api/drivers/eligible`) supplying pickup coordinates, desired vehicle tier, and search radius.
   - *Justification:* The dispatcher requires immediate, real-time driver coordinates to compute proximity and make instant dispatching decisions.

3. **Driver Availability State Synchronization:**
   - *Workflow:* Upon assigning a driver, `Ride Management Service` issues a `PATCH /api/drivers/{id}/availability` to transition the driver to `BUSY`. Upon ride completion or cancellation, it transitions the driver back to `AVAILABLE`.
   - *Justification:* Prevents race conditions where multiple rides could be concurrently dispatched to the same driver.

4. **Fare Estimation & Completion Payment:**
   - *Workflow:* `Ride Management Service` queries `Fare Service` upfront for price transparency (`POST /api/fares/estimate`), and calls `POST /api/fares/calculate` and `POST /api/payments` on trip completion.
   - *Justification:* Ensures consistency between calculated trip metrics and recorded financial transactions.

### 2.2 Synchronous vs. Asynchronous Evaluation
- **Synchronous REST (Implemented):** Optimal for client-facing queries, immediate fare calculations, and dispatch decisions requiring instantaneous feedback to the passenger.
- **Asynchronous Messaging Alternative (Kafka / RabbitMQ):** Highly beneficial for high-scale location streaming and post-ride event notifications (e.g. sending promotional emails or updating analytics dashboards). For this core operational scope, synchronous REST with resilient timeouts was chosen to prevent message broker operational overhead while guaranteeing immediate consistency across the ride lifecycle.

---

## 3. Core Business Workflows & Documented Pricing Rules

### 3.1 Documented Fare Calculation Rules
The `Fare & Payment Service` implements an open, transparent, and documented pricing algorithm:

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

### 3.2 Ride Lifecycle State Machine
Ride states follow a strictly validated state transition lifecycle:
1. `REQUESTED`: Ride created by passenger with estimated fare.
2. `ASSIGNED`: Closest available driver matched and locked (`BUSY`).
3. `ACCEPTED`: Driver formally confirms assignment.
4. `IN_PROGRESS`: Passenger picked up; trip ongoing.
5. `COMPLETED`: Passenger arrived; final fare computed, payment recorded, driver released.
6. `CANCELLED`: Ride aborted prior to completion; assigned driver released.

**Negative State Transition Handling:**
Attempting an invalid jump (e.g., transitioning from `COMPLETED` $\rightarrow$ `CANCELLED` or `REQUESTED` $\rightarrow$ `IN_PROGRESS`) triggers an `InvalidRideStatusTransitionException` returning HTTP 400 Bad Request.

---

## 4. Security, Quality Assurance & Negative Testing

### 4.1 Security Implementation
- **Password Security:** BCrypt password encoder with 10 salt rounds. Plaintext passwords are never stored.
- **Stateless Authentication:** HMAC-SHA256 signed JSON Web Tokens (JWT) carrying user ID, email, and role claims.
- **Role-Based Authorization:** Endpoints enforce permissions using `@PreAuthorize` and Spring Security matchers (`ROLE_PASSENGER`, `ROLE_DRIVER`, `ROLE_ADMIN`).
- **Secrets Management:** Environment variable externalization (`JWT_SECRET`, `MONGODB_URI`). No production credentials hardcoded.

### 4.2 Negative Scenario Test Verification
As mandated in Section 5 (item 7) and Section 6.4:
1. **No Available Driver:** When a pickup query returns zero eligible drivers, the system throws `NoAvailableDriverException` returning HTTP 503 Service Unavailable.
2. **Invalid Lifecycle State Transition:** Attempting to cancel an already completed ride throws `InvalidRideStatusTransitionException` returning HTTP 400 Bad Request.
3. **Duplicate Account Registration:** Attempting to register an already existing email throws `UserAlreadyExistsException` returning HTTP 409 Conflict.
4. **Invalid Authentication:** Submitting incorrect passwords returns HTTP 401 Unauthorized.
5. **Simulated Payment Gateway Failure:** Supplying `simulateFailure: true` causes the payment processor to decline the transaction, recording a `FAILED` audit record and returning HTTP 402 Payment Required.

---

## 5. Continuous Integration (CI) Architecture
A GitHub Actions CI workflow (`.github/workflows/ci.yml`) is established to automate testing:
- **Triggers:** Every push and pull request to `main` and `feature/**` branches.
- **Environment:** Containerized Ubuntu with automated MongoDB 7.0 service container.
- **Pipeline Stages:**
  1. Source checkout.
  2. Setup JDK 21 (Temurin).
  3. Dependency resolution and caching.
  4. Full multi-module compilation and test execution (`./mvnw clean test`).
  5. Application packaging verification (`./mvnw package -DskipTests`).

---

## 6. Individual Contribution Statements

| Student Member | Assigned Service | Specific Technical Contributions |
|---|---|---|
| **Member 1** | **Account Service** | Built registration, login, JWT token issuance, BCrypt security configuration, role-based authorization, account status management, Swagger documentation, and unit tests. |
| **Member 2** | **Driver & Vehicle Service** | Developed driver operational profile, vehicle registration, availability state toggle, simulated GPS location management, Haversine geospatial proximity algorithm, and unit tests. |
| **Member 3** | **Ride Management Service** | Orchestrated ride booking workflow, driver matching dispatch, state machine transition rules, synchronous REST clients (`DriverServiceClient`, `FareServiceClient`, `AccountServiceClient`), and unit tests. |
| **Member 4** | **Fare & Payment Service** | Implemented upfront fare estimation, documented pricing calculation engine, simulated payment processor with transaction references, receipt generation, negative failure simulation, and unit tests. |

# RideLink Platform - Interservice Sequence Diagrams

## 1. End-to-End Ride Booking, Lifecycle, and Payment Flow

```mermaid
sequenceDiagram
    autonumber
    actor Passenger
    actor Driver
    participant RideSvc as Ride Management Service (:8083)
    participant AccountSvc as Account Service (:8081)
    participant DriverSvc as Driver Service (:8082)
    participant FareSvc as Fare & Payment Service (:8084)

    %% 1. Ride Creation
    Passenger->>RideSvc: POST /api/rides (Pickup, Destination, VehicleType)
    activate RideSvc
    RideSvc->>AccountSvc: GET /api/users/{passengerId} (Validate status)
    AccountSvc-->>RideSvc: 200 OK (Status: ACTIVE)
    
    RideSvc->>FareSvc: POST /api/fares/estimate (Distance, VehicleType)
    FareSvc-->>RideSvc: 200 OK (EstimatedFare: 450.00 LKR)
    
    Note over RideSvc: Create Ride with status REQUESTED
    RideSvc-->>Passenger: 201 Created (Ride ID, Status: REQUESTED)
    deactivate RideSvc

    %% 2. Driver Matching and Assignment
    Passenger->>RideSvc: POST /api/rides/{id}/assign
    activate RideSvc
    RideSvc->>DriverSvc: POST /api/drivers/eligible (PickupCoords, VehicleType, Radius)
    DriverSvc-->>RideSvc: 200 OK (List of AVAILABLE drivers sorted by Haversine distance)
    
    alt No Drivers Available
        RideSvc-->>Passenger: 503 Service Unavailable (NoAvailableDriverException)
    else Driver Found
        RideSvc->>DriverSvc: PATCH /api/drivers/{driverId}/availability (status: BUSY, activeRideId: rideId)
        DriverSvc-->>RideSvc: 200 OK (Updated status: BUSY)
        Note over RideSvc: Transition state: REQUESTED -> ASSIGNED
        RideSvc-->>Passenger: 200 OK (Status: ASSIGNED, DriverId: driver-202)
    end
    deactivate RideSvc

    %% 3. Driver Acceptance
    Driver->>RideSvc: PATCH /api/rides/{id}/accept?driverId=driver-202
    activate RideSvc
    Note over RideSvc: Transition state: ASSIGNED -> ACCEPTED
    RideSvc-->>Driver: 200 OK (Status: ACCEPTED)
    deactivate RideSvc

    %% 4. Ride Start
    Driver->>RideSvc: PATCH /api/rides/{id}/start?driverId=driver-202
    activate RideSvc
    Note over RideSvc: Transition state: ACCEPTED -> IN_PROGRESS
    RideSvc-->>Driver: 200 OK (Status: IN_PROGRESS)
    deactivate RideSvc

    %% 5. Ride Completion and Payment
    Driver->>RideSvc: POST /api/rides/{id}/complete (ActualDistance, ActualDuration, PaymentMethod)
    activate RideSvc
    Note over RideSvc: Transition state: IN_PROGRESS -> COMPLETED
    
    RideSvc->>FareSvc: POST /api/fares/calculate (ActualDistance: 5.2km, Duration: 18m, VehicleType: SEDAN)
    FareSvc-->>RideSvc: 200 OK (FinalFare: 636.00 LKR, Breakdown)
    
    RideSvc->>FareSvc: POST /api/payments (RideId, PassengerId, DriverId, Amount: 636.00, Method: CARD)
    FareSvc-->>RideSvc: 201 Created (PaymentId, Status: PAID, TxRef: TXN-89A7B31)
    
    RideSvc->>DriverSvc: PATCH /api/drivers/{driverId}/availability (status: AVAILABLE)
    DriverSvc-->>RideSvc: 200 OK (Driver released to AVAILABLE)
    
    RideSvc-->>Driver: 200 OK (Ride COMPLETED, FinalFare: 636.00, PaymentId)
    deactivate RideSvc

    %% 6. Receipt Retrieval
    Passenger->>FareSvc: GET /api/payments/receipt/ride/{rideId}
    activate FareSvc
    FareSvc-->>Passenger: 200 OK (Receipt RCP-..., TotalAmount: 636.00 LKR, Status: PAID)
    deactivate FareSvc
```

---

## 2. Negative Scenarios Sequence

### Negative Scenario 1: No Available Driver
```mermaid
sequenceDiagram
    autonumber
    actor Passenger
    participant RideSvc as Ride Management Service (:8083)
    participant DriverSvc as Driver Service (:8082)

    Passenger->>RideSvc: POST /api/rides/{id}/assign
    activate RideSvc
    RideSvc->>DriverSvc: POST /api/drivers/eligible (pickup coords, SUV, radius 25km)
    DriverSvc-->>RideSvc: 200 OK (empty list: [])
    Note over RideSvc: Throws NoAvailableDriverException
    RideSvc-->>Passenger: 503 Service Unavailable ("No available drivers found in the pickup vicinity")
    deactivate RideSvc
```

### Negative Scenario 2: Invalid State Transition
```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant RideSvc as Ride Management Service (:8083)

    Client->>RideSvc: POST /api/rides/{id}/cancel (Ride is already COMPLETED)
    activate RideSvc
    Note over RideSvc: Validation fails: cannot cancel terminal status (COMPLETED)
    RideSvc-->>Client: 400 Bad Request ("Cannot cancel ride. Ride is already in terminal state: COMPLETED")
    deactivate RideSvc
```

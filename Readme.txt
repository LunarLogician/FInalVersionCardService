# Card Service API

A Spring Boot REST API service for managing credit/debit cards with JWT-based authentication and role-based authorization.

## Features

- **Card Management**: Create, view, update, block, and freeze cards
- **Authentication**: JWT-based token authentication
- **Authorization**: Role-based access control (ADMIN and USER roles)
- **Account Association**: Cards are linked to specific user accounts
- **Security**: PIN management with validation
- **Scheduling**: Automated card status management
- **Reporting**: Admin-only reporting functionality

## API Endpoints

### Authentication
All endpoints require a valid JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

### Card Endpoints

#### 1. Get All Cards (ADMIN only)
```
GET /api/v1/cards
```
Returns a list of all cards in the system.

#### 2. Get Cards by Account ID
```
GET /api/v1/cards/{accountId}
```
- **ADMIN**: Can view any account's cards
- **USER**: Can only view their own account's cards

#### 3. Create New Card
```
POST /api/v1/cards
```
**Admin Card Creation for Other Users:**
- **ADMIN**: Can create cards for any user by including `userId` in request body
- **USER**: Can only create cards for themselves (userId from JWT token is always used)

**Request Body for ADMIN creating card for another user:**
```json
{
  "userId": 123,
  "accountId": 456,
  "type": "DEBIT",
  "cardpin": "123"
}
```

**Request Body for USER or ADMIN creating card for themselves:**
```json
{
  "accountId": 456,
  "type": "DEBIT", 
  "cardpin": "123"
}
```

**Security Notes:**
- If USER includes `userId` in request, it will be ignored for security
- ADMIN can omit `userId` to create card for themselves
- ADMIN can specify `userId` to create card for any user

#### 4. Search Cards
```

#### 5. Block Card (ADMIN only)
```
PUT /api/v1/cards/{cardId}/block
```
Permanently blocks a card.

#### 6. Update Card
```
PUT /api/v1/cards/{cardId}
```
Update card PIN or status.

**Request Body Examples:**
```json
// Change PIN
{
  "cardpin": "456"
}

// Change Status
{
  "cardstatus": "freeze"
}
```

**Validation Rules:**
- PIN must be exactly 3 digits
- Status must be either "freeze" or "active"
- Users can only update their own cards

#### 7. Generate Report (ADMIN only)
```
GET /api/v1/cards/report
```
Generates system reports.

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/Card_Service/
│   │       ├── CardServiceApplication.java
│   │       ├── SecurityConfig.java
│   │       ├── controllers/
│   │       │   ├── CardController.java
│   │       │   └── TestController.java
│   │       ├── models/
│   │       │   └── CardModel.java
│   │       ├── repositories/
│   │       │   └── CardRepo.java
│   │       └── services/
│   │           ├── AuthService.java
│   │           ├── CardService.java
│   │           ├── CardStatusScheduler.java
│   │           ├── JwtUtil.java
│   │           └── dtos/
│   │               ├── CardDTO.java
│   │               ├── CardListDTO.java
│   │               └── CreateCardDTO.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/example/Card_Service/
            └── CardServiceApplicationTests.java
```

## Technologies Used

- **Java**: Programming language
- **Spring Boot**: Application framework
- **Spring Security**: Authentication and authorization
- **JWT**: Token-based authentication
- **Maven**: Dependency management and build tool
- **JPA/Hibernate**: Data persistence (implied by repository pattern)

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- Database (configuration in application.properties)

### Running the Application

1. **Clone the repository**
2. **Navigate to project directory**
   ```bash
   cd Card_service_V2
   ```

3. **Build the project**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on the default port (usually 8080).

## Configuration

Configure your database and other settings in `src/main/resources/application.properties`.

## Security

- All endpoints require valid JWT authentication
- Role-based authorization (ADMIN/USER)
- PIN validation for card updates
- User can only access their own resources (except ADMIN)

## Error Responses

The API returns appropriate HTTP status codes:
- `200`: Success
- `400`: Bad Request (invalid data)
- `401`: Unauthorized (invalid/expired token)
- `403`: Forbidden (insufficient permissions)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

[Add your license information here]
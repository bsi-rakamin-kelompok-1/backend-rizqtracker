# RizqTracker API

REST API for tracking personal finances built with Spring Boot.

## Features

- User authentication using JWT tokens
- Account management
- Transaction tracking

## Technologies

- Java 21
- Spring Boot
- Spring Security
- PostgreSQL
- Docker
- AWS

## API Endpoints

### Authentication

- `POST /v1/auth/register` - Register new user
- `POST /v1/auth/login` - Login and get JWT token

### Transactions

- `GET /v1/transactions` - Get all user transactions
- `POST /v1/transactions` - Create a new transaction
- `GET /v1/transactions/{id}` - Get transaction by ID
- `PUT /v1/transactions/{id}` - Update transaction
- `DELETE /v1/transactions/{id}` - Delete transaction

## Configuration

### JWT Configuration

```yaml
app:
  jwt:
    secret: your_secret_key_here
    expiration: 600000  # 10 minutes in milliseconds
```

### Database Configuration

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/rizqtracker?useSSL=false
    username: postgres
    password: your_password_here
```

## Security

- JWT-based authentication
- Password encryption with bcrypt
- Token expiration after 10 minutes
- Custom error handling for expired/invalid tokens

## Getting Started

1. Clone the repository
2. Configure application.yml with your database and JWT settings
3. Run `./gradlew bootRun` to start the application
4. Access the API at http://localhost:8080

## Error Handling

The API provides standardized error responses:
- 401 Unauthorized for authentication issues
- 400 Bad Request for validation errors
- 404 Not Found for missing resources
- 500 Internal Server Error for server issues

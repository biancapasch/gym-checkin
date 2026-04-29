# Gym Check-in API

A REST API built with Java and Spring Boot for managing gym members, check-ins, and monthly billing.

This project simulates a real-world gym management system, focusing on backend best practices, business rule implementation, automated billing workflows, and test coverage.

## Features

### Customer Management
- Create customers
- Automatically define payment day based on registration date
- Manage customer relationships with check-ins and invoices

### Check-in Management
- Register check-in (IN) and check-out (OUT)
- Prevent multiple open sessions for the same customer
- Automatically close inactive sessions after 6 hours
- Retrieve paginated check-in history
- Find open sessions by customer

### Invoice Management
- Generate monthly invoices
- Prevent duplicate invoice creation for the same billing cycle
- Mark invoices as paid
- Retrieve paginated invoices by customer

### Scheduled Jobs
- Automatic invoice generation
- Runs twice a day
- Creates invoices based on each customer's payment day
- Handles duplicate invoice prevention through exception handling

---

## Business Rules

This project implements important real-world business rules:

- A customer can only have one active check-in session at a time
- Open sessions are automatically closed after 6 hours
- Invoices cannot be duplicated for the same customer and billing date
- Paid invoices cannot be paid again

---

## Architecture

This project follows a layered architecture:

```text
Controller → Service → Repository → Database
```

### Applied concepts:
- RESTful API design
- DTO pattern for requests and responses
- Service layer business logic
- JPA / Hibernate persistence
- Pagination with Pageable
- Global exception handling
- Environment profiles (dev/test)
- Database containerization with Docker
- Automated testing

---

## Main Endpoints

### Customers
```http
POST /customers
```

### Check-ins
```http
POST /customers/{customerId}/checkins
GET /customers/{customerId}/checkins
GET /customers/{customerId}/checkins/open-session
```

### Invoices
```http
PATCH /customers/{customerId}/invoices/{invoiceId}/pay
GET /customers/{customerId}/invoices
```

---

## Error Handling

Standardized API error response:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Invoice not found",
  "path": "/customers/1/invoices/99/pay",
  "timestamp": "2026-01-28T10:15:30Z"
}
```

Handled exceptions:
- NotFoundException
- DuplicateInvoiceException
- InvoiceAlreadyPaidException

---

## Testing

Test coverage includes:

- Service layer tests with JUnit 5 and Mockito
- Repository tests with @DataJpaTest and H2
- Controller tests with @WebMvcTest and MockMvc

---

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL
- H2 Database
- Gradle
- Docker
- Docker Compose
- Lombok
- JUnit 5
- Mockito

---

## Running the Database

Start PostgreSQL locally:

```bash
docker compose up -d
```

Default configuration:

- Database: gym_checkin
- User: postgres
- Password: postgres
- Port: 5432

---

## Running the Application

Start the application:

```bash
./gradlew bootRun
```

---

## Running Tests

```bash
./gradlew test
```

---

## Author

Bianca Paschoal  
GitHub: https://github.com/biancapasch

# Cloud Product Catalog

## Overview
The **Cloud Product Catalog** is a RESTful API designed for managing product catalogs in a marketplace environment. The application was implemented using **Java Spring Boot**, following a **clean architecture** approach. It integrates with **MongoDB** as the database and **AWS services (SQS & S3)** for event-driven processing.

This project was originally part of a **technical challenge** that required implementation in **Node.js** with **Express.js**. However, this solution was developed using **Java Spring Boot**, ensuring robust scalability and maintainability.

## Technologies Used
- **Java 17**
- **Spring Boot**
- **Spring Data MongoDB**
- **Spring Web** (RESTful API)
- **Spring Security** (future authentication implementation)
- **AWS SDK for Java** (SQS, S3 integration)
- **MongoDB** (database)
- **JUnit & RestAssured** (testing, including E2E tests)
- **GitHub Actions** (CI/CD automation)
- **Docker & Docker Compose** (containerization)

## System Architecture
The application follows **clean architecture principles**, structuring the project into distinct layers:

```
cloud-product-catalog/
├── application/
│   ├── controller/      # REST API Controllers
│   └── dto/             # Data Transfer Objects (DTOs)
├── domain/
│   ├── model/           # Core Business Entities
│   ├── service/         # Business Logic Services
│   └── repository/      # MongoDB Repository Interfaces
├── infra/
│   ├── messaging/       # AWS SQS Integration (Publisher & Consumer)
├── test/
│   └── application/           
|       └── controller         # End-to-End API Tests
└── README.md
```

## Key Features
- **CRUD Operations** for `products` and `categories`.
- **Observer Design Pattern** for decoupled event handling.
- **AWS SQS Integration** for message-based event publishing.
- **AWS S3 Integration** for catalog storage.
- **Scheduled Message Consumption** (`@Scheduled` in `SqsConsumer`).
- **End-to-End API Testing** using **RestAssured**.
- **Continuous Integration (CI)** with **GitHub Actions**.

## API Endpoints

### Categories
| Method | Endpoint       | Description |
|--------|--------------|-------------|
| `POST` | `/categories` | Create a new category |
| `GET`  | `/categories` | List all categories |
| `PUT`  | `/categories/{id}` | Update a category |
| `DELETE` | `/categories/{id}` | Delete a category |

### Products
| Method | Endpoint       | Description |
|--------|--------------|-------------|
| `POST` | `/products` | Create a new product |
| `GET`  | `/products` | List all products |
| `PUT`  | `/products/{id}` | Update a product |
| `DELETE` | `/products/{id}` | Delete a product |

## Running the Application

### 1️⃣ Prerequisites
Ensure you have:
- **Docker & Docker Compose** installed
- **Java 17**
- **Maven**
- **AWS Credentials Configured** (if running with real AWS services)

### 2️⃣ Running with Docker Compose
```sh
docker-compose up -d
```
This starts **MongoDB** and other required services.

### 3️⃣ Running the API Locally
```sh
mvn spring-boot:run
```
The API will be available at `http://localhost:8080`.

### 4️⃣ Running Tests
#### **Unit Tests & Integration Tests**
```sh
mvn test
```
#### **End-to-End API Tests** (E2E)
```sh
mvn verify
```

## Event-Driven Architecture

### 🔹 **Message Publishing (AWS SQS)**
- Implemented using **`SQSPublisher`**, which **implements `CatalogEventListener`**.
- The **Observer Pattern** ensures that services are **not directly coupled** to the message publisher.
- Messages are published whenever `ProductService` or `CategoryService` modifies catalog data.

### 🔹 **Message Consumption (AWS SQS)**
- Implemented in **`SqsConsumer`**.
- Uses **`@Scheduled` annotation** to periodically fetch and process SQS messages.
- Generates a new catalog JSON and stores it in **AWS S3**.

## CI/CD with GitHub Actions
- **Continuous Integration (CI)** runs on every push.
- Executes **unit tests, integration tests, and E2E tests**.
- Uses **MongoDB service in GitHub Actions** to simulate a production-like database environment.

## Future Enhancements
- Implement **authentication & authorization** using **Spring Security + JWT**.
- Improve **test coverage** for unit tests.
- Implement **pagination & filtering** in API responses.
- Add **caching strategies** for optimized performance.

---


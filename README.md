# Spring-MVC-demo
一个android原生开发对Spring MVC的学习

---

# Spring Boot Learning Project

This project is a demonstration of a modern Spring Boot application, built incrementally to learn and showcase key backend development concepts. It's a RESTful API for managing users.

## Core Concepts Implemented

This project currently demonstrates:

- **RESTful API Design**: Using `@RestController` to expose API endpoints.
- **3-Tier Architecture**: A clear separation of concerns between:
    - `Controller`: Handles HTTP requests and responses.
    - `Service`: Contains the core business logic.
    - `Repository`: Simulates the data access layer.
- **Dependency Injection (IoC)**: Using constructor injection to manage dependencies between components.
- **Global Exception Handling**: A robust `@RestControllerAdvice` to catch all exceptions and return a unified JSON error response.
- **Unified API Response Format**: A consistent `ApiResponse<T>` wrapper for all successful and failed responses.
- **In-Memory "Database"**: The `UserRepository` uses a `HashMap` to simulate data persistence for rapid prototyping.

## API Endpoints

- `GET /user/{id}`: Retrieves a user by their ID.
- `POST /user/register`: Creates a new user.

## How to Run

1.  **Prerequisites**:
    - Java 17 or later
    - Maven

2.  **Execution**:
    You can run the application using the Maven wrapper included in the project:

    ```bash
    ./mvnw spring-boot:run
    ```

    The application will start on `http://localhost:8080`.

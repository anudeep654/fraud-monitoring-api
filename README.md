# Fraud Monitoring API

A microservices-based fraud monitoring system that provides real-time fraud detection and notification capabilities.

## Project Structure

The project consists of the following microservices:

- **api-gateway**: Entry point for all client requests, handles routing and authentication
- **fraud-service**: Core fraud detection and analysis service
- **model-service**: Machine learning model service for fraud prediction
- **notification-service**: Handles alerts and notifications for detected fraud cases
- **shared-utilities**: Common utilities and models shared across services

## Prerequisites

- Java 17 or higher
- Maven
- Spring Boot 3.x

## Setup Instructions

1. Clone the repository
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Start the services in the following order:
   - notification-service
   - model-service
   - fraud-service
   - api-gateway

## Configuration

Each service has its own `application.yml` file in the `src/main/resources` directory containing service-specific configurations.

## API Documentation

The API documentation is available at:
- Gateway Swagger UI: `http://localhost:8080/swagger-ui.html`
- Fraud Service: `http://localhost:8081/swagger-ui.html`
- Model Service: `http://localhost:8082/swagger-ui.html`
- Notification Service: `http://localhost:8083/swagger-ui.html`
# fraud-monitoring-api

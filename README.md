# Flight Booking Application

## Overview

This is a Spring Boot application for a flight booking system with integrated PayPal payment processing. The application uses Kafka for event streaming, MongoDB for storing company platform data, and MySQL for other persistent data.

## Key Features

1. Flight Search and Booking
2. PayPal Integration for Payments
3. Kafka Event Streaming
4. RESTful API
5. Reactive Programming with Spring WebFlux

## Technology Stack

- Java 
- Spring Boot
- Apache Kafka
- MongoDB
- MySQL
- PayPal SDK
- Thymeleaf (for server-side rendering)
- Docker (for containerization)

## Project Structure

The project follows a standard Spring Boot structure with the following key components:

- `src/main/java/main`: Contains the main application code
  - `config`: Configuration classes for Kafka, PayPal, etc.
  - `controller`: REST controllers
  - `models`: Data models and DTOs
  - `repositories`: Data access interfaces
  - `services`: Business logic services

- `src/main/resources`: Contains application properties, templates, and static resources

## Key Components

### Flight Search and Booking

The application allows users to search for flights and make bookings. The flight data is fetched and stored in the database.

### PayPal Integration

The application integrates with PayPal for payment processing.

### Kafka Integration

Kafka is used for event streaming.

### Frontend

The application uses Thymeleaf for server-side rendering.

## API Endpoints

- GET `/api/v1/flight/destination/{destination}`: Search flights by destination
- POST `/paypal/init`: Initialize PayPal payment
- GET `/payment/success`: Handle successful PayPal payment
- GET `/payment/cancel`: Handle cancelled PayPal payment

## Future Improvements

- Implement user authentication and authorization
- Enhance the frontend with a modern JavaScript framework (e.g., React, Vue.js)
- Add unit and integration tests

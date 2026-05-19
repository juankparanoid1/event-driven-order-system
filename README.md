# Distributed Order System

Event-driven microservices system built with Spring Boot, Apache Kafka, PostgreSQL, and Docker.

This project simulates a distributed commerce workflow where services communicate asynchronously using Kafka events. It demonstrates real-world backend architecture patterns used in scalable systems such as transactional outbox, idempotent consumers, API idempotency, retries, and Dead Letter Topics (DLT).

---

# Architecture

```text
Order Service
    ↓
orders.events
    ↓
Payment Service
    ↓
payment.events
    ↓
Order Service (updates order status)
```

---

# Features

## Event-Driven Architecture

Services communicate asynchronously through Kafka topics instead of direct HTTP calls.

Implemented topics:

* `orders.events`
* `payment.events`
* `orders.events-dlt`

---

## Transactional Outbox Pattern

Implemented the Outbox Pattern to guarantee reliable event publishing.

Flow:

1. Business data is saved
2. Outbox event is stored in the same database transaction
3. Scheduled publisher sends events to Kafka
4. Event is marked as processed

This prevents losing events during failures.

---

## Idempotent Kafka Consumers

Consumers prevent duplicate processing using a `processed_events` table.

This protects against:

* Kafka retries
* consumer restarts
* duplicate event delivery
* offset reprocessing

---

## API Idempotency

Implemented API-level idempotency using:

* `Idempotency-Key`
* SHA-256 request hashing
* stored responses

This prevents duplicate order creation caused by:

* client retries
* network issues
* accidental double submissions

Behavior:

* Same key + same payload → returns existing response
* Same key + different payload → request rejected

---

## Retry + Dead Letter Topic (DLT)

Implemented retry handling and Dead Letter Topics.

Failed messages:

1. Retry automatically
2. If retries are exhausted
3. Message is sent to DLT

This prevents message loss and allows later inspection/reprocessing.

---

## Order Workflow

### Order Service

* Creates orders
* Publishes `ORDER_CREATED` events
* Updates order status after payment confirmation

### Payment Service

* Consumes order events
* Simulates payment processing
* Publishes payment events

---

# Technologies

* Java
* Spring Boot
* Spring Kafka
* Spring Data JPA
* PostgreSQL
* Apache Kafka
* Docker
* Flyway
* Hibernate

---

# Implemented Concepts

* Microservices Architecture
* Event-Driven Systems
* Kafka Messaging
* Transactional Outbox Pattern
* Idempotent Consumers
* API Idempotency
* Retry Handling
* Dead Letter Topics (DLT)
* Eventual Consistency
* Distributed System Design

---

# Project Structure

```text
order-service/
payment-service/
docker-compose.yml
```

---

# Running the Project

## Requirements

* Docker
* Java 21+
* Maven

---

## Start Infrastructure

```bash
docker-compose up -d
```

---

## Run Services

```bash
mvn spring-boot:run
```

Run both:

* `order-service`
* `payment-service`

---

# Example Request

```http
POST /order/create
Idempotency-Key: 6c6f6df7-8d13-4d3e-93a0-1f1d9b7f6f91
```

```json
{
  "userId": "504dfc0a-fdf8-4ed6-945d-aac0593fd5c0",
  "totalAmount": 140.67,
  "orderItems": [
    {
      "productId": "e51212c0-9057-4001-99e4-290765b8de15",
      "quantity": 1,
      "price": 25.00
    }
  ]
}
```

---

# Purpose of This Project

This project was built to practice and demonstrate backend engineering concepts commonly used in real distributed systems and event-driven microservices architectures.

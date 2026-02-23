Payment Service

Overview

The Payment Service is responsible for handling payment transactions within the Movie Ticket Platform.

It processes:
	•	Payment charge requests
	•	Refund operations
	•	Transaction state management
	•	Idempotent payment handling
	•	Booking integration

This service is designed to be isolated, stateless, and resilient in a distributed microservice environment.



Responsibilities
	•	Charge payment for booking
	•	Store transaction details
	•	Ensure idempotent processing
	•	Process refunds
	•	Handle payment failure scenarios
	•	Maintain transaction lifecycle state



Technology Stack
	•	Spring Boot
	•	Spring Security
	•	JPA / Hibernate
	•	MySQL
	•	JWT Authentication
	•	Resilience4j (Circuit Breaker & Retry)
	•	Structured Logging
	•	REST API



Architecture Role

The Payment Service is a dedicated transactional microservice that:
	•	Isolates payment logic
	•	Prevents duplicate charging
	•	Maintains financial transaction records
	•	Integrates with Booking Service



Transaction Lifecycle

Payment Status Flow:

INITIATED → CHARGED → REFUNDED
           ↓
         FAILED




API Endpoints

Charge Payment

POST /api/v1/payment/charge

Request Body:

{
  "bookingId": 5,
  "userId": "user1",
  "amount": 400
}

Response:

{
  "bookingId": 5,
  "transactionId": 12,
  "status": "CHARGED",
  "success": true
}




Refund Payment

POST /api/v1/payment/refund




Idempotency Handling

The service ensures that duplicate payment requests do not create duplicate transactions.

Mechanism:
	•	Check transaction by bookingId
	•	If transaction exists:
	•	Return existing status
	•	Do not create new charge
	•	Prevent double charging

This ensures safe retries from Booking Service.



Failure Simulation

The service supports controlled failure simulation for testing:
	•	Random failure logic
	•	Configurable failure toggle
	•	Used for resilience testing



Refund Flow

Refund is allowed only if:
	•	Original status = CHARGED
	•	Transaction exists

Refund logic:
	1.	Validate transaction
	2.	Ensure not already refunded
	3.	Update status → REFUNDED
	4.	Return response

Refund is idempotent.



Booking Integration

Booking Service calls Payment Service during:
	•	Booking confirmation
	•	Booking cancellation

Charge Flow:
	1.	Booking created (PENDING)
	2.	Payment charge initiated
	3.	On CHARGED → Booking CONFIRMED
	4.	On FAILED → Booking FAILED

Refund Flow:
	1.	Cancel booking
	2.	Refund initiated
	3.	Booking updated to REFUNDED



Security Model
	•	JWT-based authentication
	•	Role-based access
	•	Only CUSTOMER role allowed to initiate payment
	•	Stateless session management
	•	Token validation via resource server



Structured Logging

Structured logging is implemented for observability and monitoring.

Logs include:
	•	Booking ID
	•	Transaction ID
	•	Payment status
	•	Error details

Example:

PAYMENT_PROCESS_RESULT bookingId=5 success=true
PAYMENT_SAVED bookingId=5 status=CHARGED
PAYMENT_REFUNDED bookingId=5

Benefits:
	•	Easier debugging
	•	Traceability across services
	•	Production monitoring support
	•	Compatible with ELK / CloudWatch / Azure Monitor



Resilience

Resilience features include:
	•	Circuit Breaker
	•	Retry mechanism
	•	Fallback method
	•	Graceful failure handling

If downstream failure occurs:
	•	Fallback returns FAILED response
	•	Prevents cascading failure



Database Schema

PaymentTransaction Table
	•	id
	•	bookingId
	•	userId
	•	amount
	•	status
	•	createdAt

Primary business key:
	•	bookingId

Ensures one transaction per booking.



CAP Theorem Consideration

For financial consistency:
	•	Prioritized Consistency and Partition Tolerance
	•	Strong transactional integrity
	•	No eventual consistency for charge/refund

Financial operations require strict correctness.



Deployment

Docker

docker build -t payment-service .
docker run -p 8085:8080 payment-service




Kubernetes
	•	Deployment with replicas
	•	ClusterIP internal communication
	•	Scalable independently
	•	HPA supported



Scalability Plan
	•	Horizontal scaling via Kubernetes
	•	Stateless design
	•	Idempotent API supports retry
	•	Future distributed caching for transaction lookup



Future Enhancements
	•	Razorpay integration
	•	Stripe integration
	•	Webhook support
	•	Async payment processing via Kafka
	•	Distributed transaction management (Saga pattern)
	•	Audit trail table
	•	Payment reconciliation system



Architectural Importance

The Payment Service isolates financial operations from business logic.

This separation:
	•	Reduces coupling
	•	Improves reliability
	•	Enables independent scaling
	•	Supports production-grade payment gateway integration

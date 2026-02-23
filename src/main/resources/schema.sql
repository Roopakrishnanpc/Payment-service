CREATE DATABASE payment_db;

Use payment_db;

CREATE TABLE payment_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    user_id VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL
);
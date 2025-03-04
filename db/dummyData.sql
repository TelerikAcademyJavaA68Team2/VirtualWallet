-- Insert dummy data into the `user` table
USE virtual_wallet;
INSERT INTO user (id, created_at, deleted_at, email, first_name, last_name, password, phone_number, photo, role, status, username)
VALUES
    ('11111111-1111-1111-1111-111111111111', '2023-10-01 10:00:00.000000', NULL, 'user1@example.com', 'John', 'Doe', 'hashed_password_1', '+12345678901', NULL, 'USER', 'ACTIVE', 'john_doe'),
    ('22222222-2222-2222-2222-222222222222', '2023-10-02 11:00:00.000000', NULL, 'user2@example.com', 'Jane', 'Smith', 'hashed_password_2', '+23456789012', NULL, 'USER', 'ACTIVE', 'jane_smith'),
    ('33333333-3333-3333-3333-333333333333', '2023-10-03 12:00:00.000000', NULL, 'user3@example.com', 'Alice', 'Johnson', 'hashed_password_3', '+34567890123', NULL, 'USER', 'ACTIVE', 'alice_johnson'),
    ('44444444-4444-4444-4444-444444444444', '2023-10-04 13:00:00.000000', NULL, 'user4@example.com', 'Bob', 'Brown', 'hashed_password_4', '+45678901234', NULL, 'USER', 'ACTIVE', 'bob_brown'),
    ('55555555-5555-5555-5555-555555555555', '2023-10-05 14:00:00.000000', NULL, 'user5@example.com', 'Charlie', 'Davis', 'hashed_password_5', '+56789012345', NULL, 'USER', 'ACTIVE', 'charlie_davis');
INSERT INTO user (id, created_at, deleted_at, email, first_name , status, last_name, password, phone_number, photo, role, username)
VALUES
    ('123e4567-e89b-12d3-a456-426614174000', NOW(), NULL, 'john.doe@example.com', 'John', 'PENDING', 'Doe', 'password123', '+1234567890', NULL, 'USER', 'johndoe'),
    ('123e4567-e89b-12d3-a456-426614174001', NOW(), NULL, 'admin@example.com', 'Admin',  'ACTIVE', 'User', 'adminpass', '+0987654322', NULL, 'ADMIN', 'adminuser'),
    ('123e4567-e89b-12d3-a456-426614174002', NOW(), NULL, 'adminGeorgi@example.com', 'Georgi',  'ACTIVE', 'User', '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+0987654323', NULL, 'ADMIN', 'georgi'),
    ('123e4567-e89b-12d3-a456-426614174003', NOW(), NULL, 'adminIvan@example.com', 'Ivan',  'ACTIVE', 'User', '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+0987654324', NULL, 'ADMIN', 'vankata');

-- Insert dummy data into the `credit_card` table
INSERT INTO card (id, card_holder, card_number, created_at, cvv, deleted_at, expiration_date, is_deleted, owner_id)
VALUES
    ('c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1', 'John Doe', '4111111111111111', '2023-10-01 10:15:00.000000', '123', NULL, '12/25', 0, '11111111-1111-1111-1111-111111111111'),
    ('c2c2c2c2-c2c2-c2c2-c2c2-c2c2c2c2c2c2', 'Jane Smith', '5105105105105100', '2023-10-02 11:15:00.000000', '456', NULL, '11/26', 0, '22222222-2222-2222-2222-222222222222');

INSERT INTO card (id, card_holder, card_number, created_at, cvv, deleted_at, expiration_date, is_deleted, owner_id)
VALUES
    ('123e4567-e89b-12d3-a452-426614174002', 'John Doe', '4111111211111111', NOW(), '123', NULL, '12/28', 0, '123e4567-e89b-12d3-a456-426614174000'),
    ('123e4567-e89b-12d3-a456-446614174003', 'Admin User', '5555555555554444', `NOW`(), '456', NULL, '12/26', 0, '123e4567-e89b-12d3-a456-426614174000');


INSERT INTO wallet (id, balance, created_at, currency, deleted_at, is_deleted, owner_id)
VALUES
-- User 1 Wallets
('1a1a1a1a-1a1a-1a1a-1a1a-1a1a1a1a1a1a', 500.00, '2023-10-01 10:05:00.000000', 'USD', NULL, 0, '11111111-1111-1111-1111-111111111111'),
('1b1b1b1b-1b1b-1b1b-1b1b-1b1b1b1b1b1b', 600.00, '2023-10-01 10:10:00.000000', 'EUR', NULL, 0, '11111111-1111-1111-1111-111111111111'),

-- User 2 Wallets
('2a2a2a2a-2a2a-2a2a-2a2a-2a2a2a2a2a2a', 700.00, '2023-10-02 11:05:00.000000', 'BGN', NULL, 0, '22222222-2222-2222-2222-222222222222'),
('2b2b2b2b-2b2b-2b2b-2b2b-2b2b2b2b2b2b', 800.00, '2023-10-02 11:10:00.000000', 'USD', NULL, 0, '22222222-2222-2222-2222-222222222222'),

-- User 3 Wallets
('3a3a3a3a-3a3a-3a3a-3a3a-3a3a3a3a3a3a', 900.00, '2023-10-03 12:05:00.000000', 'EUR', NULL, 0, '33333333-3333-3333-3333-333333333333'),
('3b3b3b3b-3b3b-3b3b-3b3b-3b3b3b3b3b3b', 1000.00, '2023-10-03 12:10:00.000000', 'BGN', NULL, 0, '33333333-3333-3333-3333-333333333333'),

-- User 4 Wallets
('4a4a4a4a-4a4a-4a4a-4a4a-4a4a4a4a4a4a', 1100.00, '2023-10-04 13:05:00.000000', 'USD', NULL, 0, '44444444-4444-4444-4444-444444444444'),
('4b4b4b4b-4b4b-4b4b-4b4b-4b4b4b4b4b4b', 1200.00, '2023-10-04 13:10:00.000000', 'EUR', NULL, 0, '44444444-4444-4444-4444-444444444444'),

-- User 5 Wallets
('5a5a5a5a-5a5a-5a5a-5a5a-5a5a5a5a5a5a', 1300.00, '2023-10-05 14:05:00.000000', 'BGN', NULL, 0, '55555555-5555-5555-5555-555555555555'),
('5b5b5b5b-5b5b-5b5b-5b5b-5b5b5b5b5b5b', 1400.00, '2023-10-05 14:10:00.000000', 'USD', NULL, 0, '55555555-5555-5555-5555-555555555555');
-- Insert dummy data into the `wallet` table
INSERT INTO wallet (id, balance, created_at, currency, deleted_at, is_deleted, owner_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174004', 1000.00, NOW(), 'USD', NULL, 0, '123e4567-e89b-12d3-a456-426614174000'),
    ('123e4567-e89b-12d3-a456-426614174005', 500.00, NOW(), 'EUR', NULL, 0, '123e4567-e89b-12d3-a456-426614174001');

-- Insert dummy data into the `exchange` table
INSERT INTO exchange (id, amount, from_currency,to_currency, date, exchange_rate, from_wallet_id, to_wallet_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174006', 100.00, 'USD', 'BGN', NOW(), 0.85, '123e4567-e89b-12d3-a456-426614174004', '123e4567-e89b-12d3-a456-426614174005');

-- Insert dummy data into the `transaction` table
INSERT INTO transaction (id, amount, currency, date, recipient_wallet_id, sender_wallet_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174007', 50.00, 'USD', NOW(), '123e4567-e89b-12d3-a456-426614174005', '123e4567-e89b-12d3-a456-426614174004');

-- Insert dummy data into the `transfer` table
INSERT INTO transfer (id, amount, currency, date, status, card_id, wallet_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174008', 200.00, 'USD', NOW(), 'APPROVED', '123e4567-e89b-12d3-a456-426614174002', '123e4567-e89b-12d3-a456-426614174004');
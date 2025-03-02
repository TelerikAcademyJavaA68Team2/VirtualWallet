-- Insert dummy data into the `user` table
USE virtual_wallet;
INSERT INTO user (id, created_at, deleted_at, email, first_name , status, last_name, password, phone_number, photo, role, username)
VALUES
    ('123e4567-e89b-12d3-a456-426614174000', NOW(), NULL, 'john.doe@example.com', 'John', 'PENDING', 'Doe', 'password123', '+1234567890', NULL, 'USER', 'johndoe'),
    ('123e4567-e89b-12d3-a456-426614174001', NOW(), NULL, 'admin@example.com', 'Admin',  'ACTIVE', 'User', 'adminpass', '+0987654321', NULL, 'ADMIN', 'adminuser');

-- Insert dummy data into the `credit_card` table
INSERT INTO credit_card (id, card_holder, card_number, created_at, cvv, deleted_at, expiration_date, is_deleted, owner_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174002', 'John Doe', '4111111111111111', NOW(), '123', NULL, DATE_ADD(NOW(), INTERVAL 2 YEAR), 0, '123e4567-e89b-12d3-a456-426614174000'),
    ('123e4567-e89b-12d3-a456-426614174003', 'Admin User', '5555555555554444', NOW(), '456', NULL, DATE_ADD(NOW(), INTERVAL 3 YEAR), 0, '123e4567-e89b-12d3-a456-426614174001');

-- Insert dummy data into the `wallet` table
INSERT INTO wallet (id, balance, created_at, currency, deleted_at, is_deleted, owner_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174004', 1000.00, NOW(), 'USD', NULL, 0, '123e4567-e89b-12d3-a456-426614174000'),
    ('123e4567-e89b-12d3-a456-426614174005', 500.00, NOW(), 'EUR', NULL, 0, '123e4567-e89b-12d3-a456-426614174001');

-- Insert dummy data into the `exchange` table
INSERT INTO exchange (id, amount, currency, date, exchange_rate, from_wallet_id, to_wallet_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174006', 100.00, 'USD', NOW(), 0.85, '123e4567-e89b-12d3-a456-426614174004', '123e4567-e89b-12d3-a456-426614174005');

-- Insert dummy data into the `transaction` table
INSERT INTO transaction (id, amount, currency, date, recipient_wallet_id, sender_wallet_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174007', 50.00, 'USD', NOW(), '123e4567-e89b-12d3-a456-426614174005', '123e4567-e89b-12d3-a456-426614174004');

-- Insert dummy data into the `transfer` table
INSERT INTO transfer (id, amount, currency, date, status, credit_card_id, wallet_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174008', 200.00, 'USD', NOW(), 'APPROVED', '123e4567-e89b-12d3-a456-426614174002', '123e4567-e89b-12d3-a456-426614174004');
-- Insert dummy data into the `user` table    $2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m
USE virtual_wallet;
-- Users -------------------------------------------------------------------
INSERT INTO user (id, created_at, email, first_name, last_name, password, phone_number, role, status, username)
VALUES
    ('123e4567-e89b-12d3-a456-426614174000', NOW(), 'john.doe@example.com', 'John', 'Doe',
     '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+1234567890', 'USER', 'ACTIVE', 'john_doe'),

    ('123e4567-e89b-12d3-a456-426614174001', NOW(), 'jane.smith@example.com', 'Jane', 'Smith',
     '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+2345678901', 'USER', 'BLOCKED', 'jane_smith'),

    ('123e4567-e89b-12d3-a456-426614174002', NOW(), 'alice@example.com', 'Alice', 'Jones',
     '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+3456789012', 'USER', 'ACTIVE', 'alice_j'),

    ('123e4567-e89b-12d3-a456-426614174003', NOW(), 'bob@example.com', 'Bob', 'Brown',
     '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+4567890123', 'USER', 'ACTIVE', 'bob_b'),

    ('123e4567-e89b-12d3-a456-426614174004', NOW(), 'admin.georgi@example.com', 'Georgi', 'Admin',
     '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+5678901234', 'ADMIN', 'ACTIVE', 'georgi'),

    ('123e4567-e89b-12d3-a456-426614174005', NOW(), 'admin.ivan@example.com', 'Ivan', 'Admin',
     '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+6789012345', 'ADMIN', 'ACTIVE', 'vankata'),

    ('123e4567-e89b-12d3-a456-426614174006', NOW(), 'pending@example.com', 'New', 'User',
     '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+7890123456', 'USER', 'PENDING', 'new_user');

-- Cards -------------------------------------------------------------------
INSERT INTO card (id, card_holder, card_number, created_at, cvv, expiration_date, owner_id)
VALUES
    ('123e4567-e89b-12d3-a452-426614184000', 'John Doe', '4111111111111111', NOW(), '123', '12/25',
     '123e4567-e89b-12d3-a456-426614174000'), -- John's card

    ('123e4567-e89b-12d3-a452-426614874001', 'Jane Smith', '5555555555554444', NOW(), '456', '11/26',
     '123e4567-e89b-12d3-a456-426614174001'), -- Jane's card

    ('123e4567-e89b-12d3-a452-426614874002', 'Georgi Admin', '4222222222222222', NOW(), '789', '10/27',
     '123e4567-e89b-12d3-a456-426614174004'); -- Georgi's card

-- Wallets -----------------------------------------------------------------
INSERT INTO wallet (id, balance, currency, created_at, owner_id)
VALUES
-- John's wallets
('123e4567-e89b-12d3-a456-826674174001', 1000.00, 'USD', NOW(), '123e4567-e89b-12d3-a456-426614174000'),
('123e4567-e89b-12d3-a456-826674174002', 500.00, 'EUR', NOW(), '123e4567-e89b-12d3-a456-426614174000'),

-- Jane's wallets
('123e4567-e89b-12d3-a456-424684174001', 700.00, 'BGN', NOW(), '123e4567-e89b-12d3-a456-426614174001'),
('123e4567-e89b-12d3-a456-434618174002', 800.00, 'USD', NOW(), '123e4567-e89b-12d3-a456-426614174001'),

-- Admin wallets
('123e4567-e89b-12d3-a456-726618174001', 5000.00, 'BGN', NOW(), '123e4567-e89b-12d3-a456-426614174004'),
('123e4567-e89b-12d3-a456-826611174002', 2000.00, 'USD', NOW(), '123e4567-e89b-12d3-a456-426614174004');

-- Exchange Rates ----------------------------------------------------------
INSERT INTO exchange_rate (id, from_currency, to_currency, rate)
VALUES
    ('123e4567-e89b-12d3-a456-426614174011', 'EUR', 'USD', 1.0812),
    ('123e4567-e89b-12d3-a456-426614174012', 'USD', 'EUR', 0.9235),
    ('123e4567-e89b-12d3-a456-426614174013', 'BGN', 'EUR', 0.5113),
    ('123e4567-e89b-12d3-a456-426614174014', 'EUR', 'BGN', 1.9558),
    ('123e4567-e89b-12d3-a456-426614174015', 'USD', 'BGN', 1.8012),
    ('123e4567-e89b-12d3-a456-426614174016', 'BGN', 'USD', 0.5543);

-- Exchanges
INSERT INTO exchange (id,
                      amount,
                      to_amount,
                      from_currency,
                      to_currency,
                      exchange_rate,
                      from_wallet_id,
                      to_wallet_id,
                      recipient_username,
                      date)
VALUES
-- 1) John exchanging from his USD wallet into his EUR wallet
('123e4567-e89b-12d3-a456-426614174019',
 100.00,
 100.00 * 1.0812,
 'USD',
 'EUR',
 1.0812,
 '123e4567-e89b-12d3-a456-826674174001',  -- John's USD wallet
 '123e4567-e89b-12d3-a456-826674174002',  -- John's EUR wallet
 'john_doe',
 NOW()),

-- 2) Georgi exchanging from his BGN wallet into John's EUR wallet (sample scenario)
('123e4567-e89b-12d3-a456-426614174020',
 500.00,
 500.00 * 0.5113,
 'BGN',
 'EUR',
 0.5113,
 '123e4567-e89b-12d3-a456-726618174001',  -- Georgi's BGN wallet
 '123e4567-e89b-12d3-a456-826674174002',  -- John's EUR wallet
 'john_doe',
 NOW());

-- Transactions
INSERT INTO transaction (id,
                         amount,
                         currency,
                         sender_wallet_id,
                         recipient_wallet_id,
                         date)
VALUES
-- 1) 50 USD from John's USD wallet to Jane's USD wallet
('123e4567-e89b-12d3-a456-426614174021',
 50.00,
 'USD',
 '123e4567-e89b-12d3-a456-826674174001',  -- John's USD
 '123e4567-e89b-12d3-a456-434618174002',  -- Jane's USD
 NOW()),

-- 2) 200 EUR from John's EUR wallet to his own USD wallet
('123e4567-e89b-12d3-a456-426614174022',
 200.00,
 'EUR',
 '123e4567-e89b-12d3-a456-826674174002',  -- John's EUR
 '123e4567-e89b-12d3-a456-826674174001',  -- John's USD
 NOW());

-- Transfers
INSERT INTO transfer (id,
                      amount,
                      currency,
                      card_id,
                      wallet_id,
                      status,
                      date)
VALUES
-- 1) 150 USD from John's card into John's USD wallet
('123e4567-e89b-12d3-a456-426614174023',
 150.00,
 'USD',
 '123e4567-e89b-12d3-a452-426614184000',  -- John's card
 '123e4567-e89b-12d3-a456-826674174001',  -- John's USD wallet
 'APPROVED',
 NOW()),

-- 2) 200 BGN from Jane's card into Jane's BGN wallet (e.g. Declined for demo)
('123e4567-e89b-12d3-a456-426614174024',
 200.00,
 'BGN',
 '123e4567-e89b-12d3-a452-426614874001',  -- Jane's card
 '123e4567-e89b-12d3-a456-424684174001',  -- Jane's BGN wallet
 'DECLINED',
 NOW());

-- Email Confirmation Tokens -----------------------------------------------
INSERT INTO email_confirmation_token (id,
                                      user_id,
                                      expires_at,
                                      created_at)
VALUES ('123e4567-e89b-12d3-a456-426614174025',
        '123e4567-e89b-12d3-a456-426614174006',
        DATE_ADD(NOW(), INTERVAL 24 HOUR),
        NOW()),
       ('123e4567-e89b-12d3-a456-426614174026',
        '123e4567-e89b-12d3-a456-426614174000',
        DATE_SUB(NOW(), INTERVAL 1 HOUR),
        NOW());


-- old dummy data --


INSERT INTO user (id, created_at, deleted_at, email, first_name, last_name, password, phone_number, photo, role, status,
                  username)
VALUES ('11111111-1111-1111-1111-111111111111', '2023-10-01 10:00:00.000000', NULL, 'user1@example.com', 'John', 'Doe',
        'hashed_password_1', '+12345678901', NULL, 'USER', 'ACTIVE', 'john_doe2'),
       ('22222222-2222-2222-2222-222222222222', '2023-10-02 11:00:00.000000', NULL, 'user2@example.com', 'Jane',
        'Smith', 'hashed_password_2', '+23456789012', NULL, 'USER', 'ACTIVE', 'jane_smith'),
       ('33333333-3333-3333-3333-333333333333', '2023-10-03 12:00:00.000000', NULL, 'user3@example.com', 'Alice',
        'Johnson', 'hashed_password_3', '+34567890123', NULL, 'USER', 'ACTIVE', 'alice_johnson'),
       ('44444444-4444-4444-4444-444444444444', '2023-10-04 13:00:00.000000', NULL, 'user4@example.com', 'Bob', 'Brown',
        'hashed_password_4', '+45678901234', NULL, 'USER', 'ACTIVE', 'bob_brown'),
       ('55555555-5555-5555-5555-555555555555', '2023-10-05 14:00:00.000000', NULL, 'user5@example.com', 'Charlie',
        'Davis', 'hashed_password_5', '+56789012345', NULL, 'USER', 'ACTIVE', 'charlie_davis');
INSERT INTO user (id, created_at, deleted_at, email, first_name, status, last_name, password, phone_number, photo, role,
                  username)
VALUES ('123e4567-e89b-12d3-a456-426614174000', NOW(), NULL, 'john.doe@example.com', 'John', 'PENDING', 'Doe',
        'password123', '+1234567890', NULL, 'USER', 'johndoe'),
       ('123e4567-e89b-12d3-a456-426614174001', NOW(), NULL, 'admin@example.com', 'Admin', 'ACTIVE', 'User',
        'adminpass', '+0987654322', NULL, 'ADMIN', 'adminuser'),
       ('123e4567-e89b-12d3-a456-426614174002', NOW(), NULL, 'adminGeorgi@example.com', 'Georgi', 'ACTIVE', 'User',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+0987654323', NULL, 'ADMIN', 'georgi'),
       ('123e4567-e89b-12d3-a456-426614174003', NOW(), NULL, 'adminIvan@example.com', 'Ivan', 'ACTIVE', 'User',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+0987654324', NULL, 'ADMIN', 'vankata');

-- Insert dummy data into the `credit_card` table
INSERT INTO card (id, card_holder, card_number, created_at, cvv, deleted_at, expiration_date, is_deleted, owner_id)
VALUES ('c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1', 'John Doe', '4111111111111111', '2023-10-01 10:15:00.000000', '123',
        NULL, '12/25', 0, '11111111-1111-1111-1111-111111111111'),
       ('c2c2c2c2-c2c2-c2c2-c2c2-c2c2c2c2c2c2', 'Jane Smith', '5105105105105100', '2023-10-02 11:15:00.000000', '456',
        NULL, '11/26', 0, '22222222-2222-2222-2222-222222222222');

INSERT INTO card (id, card_holder, card_number, created_at, cvv, deleted_at, expiration_date, is_deleted, owner_id)
VALUES ('123e4567-e89b-12d3-a452-426614174002', 'John Doe', '4111111211111111', NOW(), '123', NULL, '12/28', 0,
        '123e4567-e89b-12d3-a456-426614174000'),
       ('123e4567-e89b-12d3-a456-446614174003', 'Admin User', '5555555555554444', NOW(), '456', NULL, '12/26', 0,
        '123e4567-e89b-12d3-a456-426614174000');


INSERT INTO wallet (id, balance, created_at, currency, deleted_at, is_deleted, owner_id)
VALUES
-- User 1 Wallets
('1a1a1a1a-1a1a-1a1a-1a1a-1a1a1a1a1a1a', 500.00, '2023-10-01 10:05:00.000000', 'USD', NULL, 0,
 '11111111-1111-1111-1111-111111111111'),
('1b1b1b1b-1b1b-1b1b-1b1b-1b1b1b1b1b1b', 600.00, '2023-10-01 10:10:00.000000', 'EUR', NULL, 0,
 '11111111-1111-1111-1111-111111111111'),

-- User 2 Wallets
('2a2a2a2a-2a2a-2a2a-2a2a-2a2a2a2a2a2a', 700.00, '2023-10-02 11:05:00.000000', 'BGN', NULL, 0,
 '22222222-2222-2222-2222-222222222222'),
('2b2b2b2b-2b2b-2b2b-2b2b-2b2b2b2b2b2b', 800.00, '2023-10-02 11:10:00.000000', 'USD', NULL, 0,
 '22222222-2222-2222-2222-222222222222'),

-- User 3 Wallets
('3a3a3a3a-3a3a-3a3a-3a3a-3a3a3a3a3a3a', 900.00, '2023-10-03 12:05:00.000000', 'EUR', NULL, 0,
 '33333333-3333-3333-3333-333333333333'),
('3b3b3b3b-3b3b-3b3b-3b3b-3b3b3b3b3b3b', 1000.00, '2023-10-03 12:10:00.000000', 'BGN', NULL, 0,
 '33333333-3333-3333-3333-333333333333'),

-- User 4 Wallets
('4a4a4a4a-4a4a-4a4a-4a4a-4a4a4a4a4a4a', 1100.00, '2023-10-04 13:05:00.000000', 'USD', NULL, 0,
 '44444444-4444-4444-4444-444444444444'),
('4b4b4b4b-4b4b-4b4b-4b4b-4b4b4b4b4b4b', 1200.00, '2023-10-04 13:10:00.000000', 'EUR', NULL, 0,
 '44444444-4444-4444-4444-444444444444'),

-- User 5 Wallets
('5a5a5a5a-5a5a-5a5a-5a5a-5a5a5a5a5a5a', 1300.00, '2023-10-05 14:05:00.000000', 'BGN', NULL, 0,
 '55555555-5555-5555-5555-555555555555'),
('5b5b5b5b-5b5b-5b5b-5b5b-5b5b5b5b5b5b', 1400.00, '2023-10-05 14:10:00.000000', 'USD', NULL, 0,
 '55555555-5555-5555-5555-555555555555');
-- Insert dummy data into the `wallet` table
INSERT INTO wallet (id, balance, created_at, currency, deleted_at, is_deleted, owner_id)
VALUES ('123e4567-e89b-12d3-a456-426614174004', 1000.00, NOW(), 'USD', NULL, 0, '123e4567-e89b-12d3-a456-426614174000'),
       ('123e4567-e89b-12d3-a456-426614174005', 500.00, NOW(), 'EUR', NULL, 0, '123e4567-e89b-12d3-a456-426614174001');

-- Insert dummy data into the `exchange` table
INSERT INTO exchange (id, amount, to_amount, from_currency, to_currency, recipient_username, date, exchange_rate,
                      from_wallet_id, to_wallet_id)
VALUES ('123e4567-e89b-12d3-a456-426614174006', 100.00, 184.35, 'USD', 'BGN', 'georgi', NOW(), 0.85,
        '123e4567-e89b-12d3-a456-426614174004', '123e4567-e89b-12d3-a456-426614174005');

-- Insert dummy data into the `transaction` table
INSERT INTO transaction (id, amount, currency, date, recipient_wallet_id, sender_wallet_id)
VALUES ('123e4567-e89b-12d3-a456-426614174007', 50.00, 'USD', NOW(), '123e4567-e89b-12d3-a456-426614174005',
        '123e4567-e89b-12d3-a456-426614174004');

-- Insert dummy data into the `transfer` table
INSERT INTO transfer (id, amount, currency, date, status, card_id, wallet_id)
VALUES ('123e4567-e89b-12d3-a456-426614174008', 200.00, 'USD', NOW(), 'APPROVED',
        '123e4567-e89b-12d3-a452-426614174002', '123e4567-e89b-12d3-a456-426614174004');


INSERT INTO exchange_rate (id, rate, from_currency, to_currency)
VALUES
-- EUR to USD
('123e4567-e89b-12d3-a456-426655574000', 1.0812345678, 'EUR', 'USD'),
-- USD to EUR
('123e4567-e89b-12d3-a456-426655574001', 0.9234567890, 'USD', 'EUR'),

('123e4567-e89b-12d3-a456-426615554002', 0.5123456789, 'BGN', 'EUR'),
-- EUR to BGN
('123e4567-e89b-12d3-a456-425554174003', 1.9558000000, 'EUR', 'BGN'),
-- USD to BGN
('123e4567-e89b-12d3-a456-555614174004', 1.8012345678, 'USD', 'BGN'),
-- BGN to USD
('123e4567-e89b-12d3-a456-426614174005', 0.5543210987, 'BGN', 'USD');


-- Users (with ADMIN and USER roles)
INSERT INTO user (id, created_at, email, first_name, last_name, password, phone_number, role, status, username,
                  deleted_at)
VALUES ('11111111-1111-1111-1111-111111111111', '2023-10-01 10:00:00.000000', 'john.doe@example.com', 'John', 'Doe',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+1234567890', 'USER', 'ACTIVE', 'john_doe',
        NULL),

       ('22222222-2222-2222-2222-222222222222', '2023-10-02 11:00:00.000000', 'jane.smith@example.com', 'Jane', 'Smith',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+2345678901', 'USER', 'BLOCKED', 'jane_smith',
        NULL),

       ('33333333-3333-3333-3333-333333333333', '2023-10-03 12:00:00.000000', 'admin@example.com', 'Admin', 'User',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+3456789012', 'ADMIN', 'ACTIVE', 'admin',
        NULL),

       ('44444444-4444-4444-4444-444444444444', '2023-10-04 13:00:00.000000', 'pending.user@example.com', 'Pending',
        'User',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m', '+4567890123', 'USER', 'PENDING',
        'pending_user', NULL);

-- Cards (with different expiration dates)
INSERT INTO card (id, card_holder, card_number, created_at, cvv, expiration_date, is_deleted, owner_id)
VALUES ('c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1', 'John Doe', '4111111111111111', '2023-10-01 10:15:00.000000', '123',
        '12/25', 0,
        '11111111-1111-1111-1111-111111111111'),

       ('c2c2c2c2-c2c2-c2c2-c2c2-c2c2c2c2c2c2', 'Jane Smith', '5555555555554444', '2023-10-02 11:15:00.000000', '456',
        '11/26', 1,
        '22222222-2222-2222-2222-222222222222');

-- Wallets (multiple currencies per user)
INSERT INTO wallet (id, balance, created_at, currency, is_deleted, owner_id)
VALUES
-- John Doe's wallets
('w1w1w1w1-w1w1-w1w1-w1w1-w1w1w1w1w1w1', 1000.00, '2023-10-01 10:05:00.000000', 'USD', 0,
 '11111111-1111-1111-1111-111111111111'),

('w2w2w2w2-w2w2-w2w2-w2w2-w2w2w2w2w2w2', 500.00, '2023-10-01 10:10:00.000000', 'EUR', 0,
 '11111111-1111-1111-1111-111111111111'),

-- Admin's wallets
('w3w3w3w3-w3w3-w3w3-w3w3-w3w3w3w3w3w3', 5000.00, '2023-10-03 12:05:00.000000', 'BGN', 0,
 '33333333-3333-3333-3333-333333333333'),

('w4w4w4w4-w4w4-w4w4-w4w4-w4w4w4w4w4w4', 2000.00, '2023-10-03 12:10:00.000000', 'USD', 0,
 '33333333-3333-3333-3333-333333333333');

-- Exchange Rates (realistic values)
INSERT INTO exchange_rate (id, rate, from_currency, to_currency)
VALUES ('er1er1er1-er1e-r1er-1er1-er1er1er1er1', 1.08, 'EUR', 'USD'),
       ('er2er2er2-er2e-r2er-2er2-er2er2er2er2', 0.92, 'USD', 'EUR'),
       ('er3er3er3-er3e-r3er-3er3-er3er3er3er3', 1.9558, 'BGN', 'EUR'),
       ('er4er4er4-er4e-r4er-4er4-er4er4er4er4', 0.51, 'EUR', 'BGN'),
       ('er5er5er5-er5e-r5er-5er5-er5er5er5er5', 1.80, 'USD', 'BGN'),
       ('er6er6er6-er6e-r6er-6er6-er6er6er6er6', 0.55, 'BGN', 'USD');

-- Exchanges (with realistic amounts)
INSERT INTO exchange (id, amount, to_amount, exchange_rate, from_currency, to_currency, recipient_username,
                      from_wallet_id, to_wallet_id, date)
VALUES ('ex1ex1ex1-ex1e-x1ex-1ex1-ex1ex1ex1ex1', 100.00, 108.00, 1.08, 'USD', 'EUR', 'john_doe',
        'w1w1w1w1-w1w1-w1w1-w1w1-w1w1w1w1w1w1', 'w2w2w2w2-w2w2-w2w2-w2w2-w2w2w2w2w2w2', '2023-10-05 09:00:00.000000'),

       ('ex2ex2ex2-ex2e-x2ex-2ex2-ex2ex2ex2ex2', 500.00, 977.90, 1.9558, 'BGN', 'EUR', 'admin',
        'w3w3w3w3-w3w3-w3w3-w3w3-w3w3w3w3w3w3', 'w4w4w4w4-w4w4-w4w4-w4w4-w4w4w4w4w4w4', '2023-10-05 10:00:00.000000'),

       ('ex2ex2ex2-ex2e-x2ex-2ex2-ex2ex2ex3ex3', 5000.00, 9779.05, 1.9558, 'BGN', 'EUR', 'admin',
        'w3w3w3w3-w3w3-w3w3-w3w3-w3w3w3w3w3w3', 'w4w4w4w4-w4w4-w4w4-w4w4-w4w4w4w4w4w4', '2023-10-05 10:00:00.000000');

-- Transactions (between users)
INSERT INTO transaction (id, amount, currency, date, recipient_wallet_id, sender_wallet_id)
VALUES ('tx1tx1tx1-tx1t-x1tx-1tx1-tx1tx1tx1tx1', 50.00, 'USD', '2023-10-05 11:00:00.000000',
        'w2w2w2w2-w2w2-w2w2-w2w2-w2w2w2w2w2w2', 'w1w1w1w1-w1w1-w1w1-w1w1-w1w1w1w1w1w1'),

       ('tx2tx2tx2-tx2t-x2tx-2tx2-tx2tx2tx2tx2', 200.00, 'EUR', '2023-10-05 12:00:00.000000',
        'w4w4w4w4-w4w4-w4w4-w4w4-w4w4w4w4w4w4', 'w3w3w3w3-w3w3-w3w3-w3w3-w3w3w3w3w3w3');

-- Transfers (card to wallet)
INSERT INTO transfer (id, amount, currency, date, status, card_id, wallet_id)
VALUES ('tf1tf1tf1-tf1t-f1tf-1tf1-tf1tf1tf1tf1', 150.00, 'USD', '2023-10-05 13:00:00.000000', 'APPROVED',
        'c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1', 'w1w1w1w1-w1w1-w1w1-w1w1-w1w1w1w1w1w1'),

       ('tf2tf2tf2-tf2t-f2tf-2tf2-tf2tf2tf2tf2', 200.00, 'EUR', '2023-10-05 14:00:00.000000', 'DECLINED',
        'c2c2c2c2-c2c2-c2c2-c2c2-c2c2c2c2c2c2', 'w2w2w2w2-w2w2-w2w2-w2w2-w2w2w2w2w2w2');

-- Email Confirmation Tokens
INSERT INTO email_confirmation_token (id, confirmed_at, created_at, expires_at, user_id)
VALUES ('ect1ect1ec-t1ec-t1ec-t1ec-t1ect1ect1ec', NULL, '2023-10-04 13:00:00.000000', '2023-10-05 13:00:00.000000',
        '44444444-4444-4444-4444-444444444444'),

       ('ect2ect2ec-t2ec-t2ec-t2ec-t2ect2ect2ec', '2023-10-05 14:00:00.000000', '2023-10-04 14:00:00.000000',
        '2023-10-05 14:00:00.000000', '11111111-1111-1111-1111-111111111111');
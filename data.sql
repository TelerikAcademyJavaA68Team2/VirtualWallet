CREATE SCHEMA IF NOT EXISTS virtual_wallet;
USE virtual_wallet;

CREATE TABLE IF NOT EXISTS user
(
    id           UUID PRIMARY KEY,
    first_name   VARCHAR(255)                                     NOT NULL,
    last_name    VARCHAR(255)                                     NOT NULL,
    username     VARCHAR(255)                                     NOT NULL UNIQUE,
    email        VARCHAR(255)                                     NOT NULL UNIQUE,
    password     VARCHAR(255)                                     NOT NULL,
    phone_number VARCHAR(255)                                     NOT NULL UNIQUE,
    photo        VARCHAR(255)                                     NOT NULL,
    role         ENUM ('ADMIN', 'USER')                           NOT NULL,
    status       ENUM ('ACTIVE', 'BLOCKED', 'DELETED', 'PENDING', 'BLOCKED_AND_DELETED') NOT NULL,
    created_at   DATETIME(6),
    deleted_at   DATETIME(6)
);

CREATE TABLE IF NOT EXISTS card
(
    id              UUID PRIMARY KEY,
    card_holder     VARCHAR(255)         NOT NULL,
    card_number     VARCHAR(255)         NOT NULL UNIQUE,
    created_at      DATETIME(6)          NOT NULL,
    cvv             VARCHAR(5)           NOT NULL,
    owner_id        UUID                 NOT NULL,
    expiration_date VARCHAR(10)          NOT NULL,
    is_deleted      TINYINT(1) DEFAULT 0 NOT NULL,
    deleted_at      DATETIME(6),
    FOREIGN KEY (owner_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS email_confirmation_token
(
    id           UUID PRIMARY KEY,
    user_id      UUID        NOT NULL,
    confirmed_at DATETIME(6),
    created_at   DATETIME(6) NOT NULL,
    expires_at   DATETIME(6) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS reset_password_token
(
    id           UUID PRIMARY KEY,
    user_id      UUID        NOT NULL,
    confirmed_at DATETIME(6),
    created_at   DATETIME(6) NOT NULL,
    expires_at   DATETIME(6) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS wallet
(
    id         UUID PRIMARY KEY,
    balance    DECIMAL(38, 2)                                                     NOT NULL,
    currency   ENUM ('BGN', 'EUR', 'USD','GBP', 'JPY', 'CNY','AUD', 'CAD', 'CHF') NOT NULL,
    owner_id   UUID                                                               NOT NULL,
    created_at DATETIME(6)                                                        NOT NULL,
    deleted_at DATETIME(6),
    is_deleted TINYINT(1) DEFAULT 0                                               NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS exchange
(
    id                 UUID PRIMARY KEY,
    amount             DECIMAL(38, 2)                                                 NOT NULL,
    to_amount          DECIMAL(38, 2)                                                 NOT NULL,
    exchange_rate      DECIMAL(38, 10)                                                NOT NULL,
    from_currency      ENUM ('BGN', 'EUR', 'USD','GBP','JPY','CNY','AUD','CAD','CHF') NOT NULL,
    to_currency        ENUM ('BGN', 'EUR', 'USD','GBP','JPY','CNY','AUD','CAD','CHF') NOT NULL,
    recipient_username VARCHAR(255)                                                   NOT NULL,
    from_wallet_id     UUID                                                           NOT NULL,
    to_wallet_id       UUID                                                           NOT NULL,
    date               DATETIME(6)                                                    NOT NULL,
    FOREIGN KEY (from_wallet_id) REFERENCES wallet (id),
    FOREIGN KEY (to_wallet_id) REFERENCES wallet (id)
);

CREATE TABLE IF NOT EXISTS transaction
(
    id                  UUID PRIMARY KEY,
    amount              DECIMAL(38, 2)                                                 NOT NULL,
    currency            ENUM ('BGN', 'EUR', 'USD','GBP','JPY','CNY','AUD','CAD','CHF') NOT NULL,
    sender_username     VARCHAR(255)                                                   NOT NULL,
    recipient_username  VARCHAR(255)                                                   NOT NULL,
    description         VARCHAR(255)                                                   NOT NULL,
    date                DATETIME(6)                                                    NOT NULL,
    recipient_wallet_id UUID                                                           NOT NULL,
    sender_wallet_id    UUID                                                           NOT NULL,
    FOREIGN KEY (sender_wallet_id) REFERENCES wallet (id),
    FOREIGN KEY (recipient_wallet_id) REFERENCES wallet (id)
);

CREATE TABLE IF NOT EXISTS transfer
(
    id                 UUID PRIMARY KEY,
    amount             DECIMAL(38, 2)                                                 NOT NULL,
    currency           ENUM ('BGN', 'EUR', 'USD','GBP','JPY','CNY','AUD','CAD','CHF') NOT NULL,
    status             ENUM ('APPROVED', 'DECLINED')                                  NOT NULL,
    recipient_username VARCHAR(255)                                                   NOT NULL,
    date               DATETIME(6)                                                    NOT NULL,
    card_id            UUID                                                           NOT NULL,
    wallet_id          UUID                                                           NOT NULL,
    FOREIGN KEY (card_id) REFERENCES card (id),
    FOREIGN KEY (wallet_id) REFERENCES wallet (id)
);

CREATE TABLE IF NOT EXISTS exchange_rate
(
    id            UUID PRIMARY KEY,
    rate          DECIMAL(38, 10)                                                NOT NULL,
    from_currency ENUM ('BGN', 'EUR', 'USD','GBP','JPY','CNY','AUD','CAD','CHF') NOT NULL,
    to_currency   ENUM ('BGN', 'EUR', 'USD','GBP','JPY','CNY','AUD','CAD','CHF') NOT NULL,
    last_updated   DATETIME(6)
);

INSERT INTO virtual_wallet.user (id, first_name, last_name, username, email, password, phone_number, photo, role,
                                 status, created_at)
VALUES ('001e4567-e89b-12d3-a456-426614174000', 'Georgi', 'Benchev', 'georgi', 'georgi@gmail.com',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m',
        '0876285805', 'https://avatars.githubusercontent.com/u/184780317?v=4', 'ADMIN', 'ACTIVE', NOW()),

       ('002e4567-e89b-12d3-a456-426614174000', 'Ivan', 'Ivanov', 'vankata', 'vankata@gmail.com',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m',
        '0000000000', 'https://avatars.githubusercontent.com/u/73892591?v=4', 'ADMIN', 'ACTIVE', NOW()),

       ('003e4567-e89b-12d3-a456-426614174000', 'Yordan', 'Tsolov', 'yordan', 'yordan@gmail.com',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m',
        '0000000001', 'https://avatars.githubusercontent.com/u/117528680?s=130&v=4', 'ADMIN', 'ACTIVE', NOW()),

       ('004e4567-e89b-12d3-a456-426614174000', 'Dimitar', 'Dimitrov', 'dimitar', 'dimitar@gmail.com',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m',
        '0000000002', 'https://avatars.githubusercontent.com/u/58068879?s=130&v=4', 'ADMIN', 'ACTIVE', NOW()),

       ('005e4567-e89b-12d3-a456-426614174000', 'Ivan', 'Georgiev', 'ivan', 'ivan29654@gmail.com',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m',
        '0000000003', '/images/default-profile-pic.png', 'ADMIN', 'ACTIVE', NOW()),

       ('006e4567-e89b-12d3-a456-426614174000', 'Daniel', 'Kolev', 'daniel', 'daniel@gmail.com',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m',
        '0000000004', 'https://avatars.githubusercontent.com/u/184924322?s=130&v=4', 'ADMIN', 'ACTIVE', NOW()),

       ('007e4567-e89b-12d3-a456-426614174000', 'Chavdar', 'Tsvetkov', 'chavdar', 'chavo@gmail.com',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m',
        '0000000005', '/images/default-profile-pic.png', 'ADMIN', 'ACTIVE', NOW()),

       ('008e4567-e89b-12d3-a456-426614174000', 'Lachezar', 'Lazarov', 'lachezar', 'lachezar@gmail.com',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m',
        '0000000006', 'https://avatars.githubusercontent.com/u/91571370?s=130&v=4', 'ADMIN', 'ACTIVE', NOW()),

       ('009e4567-e89b-12d3-a456-426614174000', 'Nikolay', 'Pilashev', 'nikolay', 'nikolay@gmail.com',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m',
        '0000000007', 'https://avatars.githubusercontent.com/u/181455727?s=130&v=4', 'ADMIN', 'ACTIVE', NOW()),

       ('010e4567-e89b-12d3-a456-426614174000', 'Viktor', 'Angelov', 'viktor', 'viktor@gmail.com',
        '$2a$10$ML33hI.7hTPKXMV1s35D/udMXQtjFpOdYIGfu/IQ4GqqITPCP088m',
        '0000000008', 'https://avatars.githubusercontent.com/u/87434779?s=130&v=4', 'ADMIN', 'ACTIVE', NOW());


INSERT INTO virtual_wallet.card (id, card_holder, card_number, created_at, cvv, expiration_date, owner_id)
VALUES ('101e4567-e89b-12d3-a452-426614184001', 'Georgi Benchev', '5723191723910001', NOW(), '001', '12/25',
        '001e4567-e89b-12d3-a456-426614174000'),

       ('102e4567-e89b-12d3-a452-426614874001', 'Georgi Benchev', '4723191723910002', NOW(), '456', '11/26',
        '001e4567-e89b-12d3-a456-426614174000'),

       ('102e4567-e89b-12d3-a452-426614875001', 'Georgi Benchev', '9723191723910002', NOW(), '459', '11/26',
        '001e4567-e89b-12d3-a456-426614174000'),

       ('203e4567-e89b-12d3-a452-426614874001', 'Ivan Ivanov', '4723191723910003', NOW(), '123', '11/26',
        '002e4567-e89b-12d3-a456-426614174000'),

       ('204e4567-e89b-12d3-a452-426614874001', 'Ivan Ivanov', '5723191723910004', NOW(), '321', '11/26',
        '002e4567-e89b-12d3-a456-426614174000'),

       ('204e4567-e89b-12d3-a452-426614875001', 'Ivan Ivanov', '9723191723910004', NOW(), '323', '11/26',
        '002e4567-e89b-12d3-a456-426614174000'),

       ('305e4567-e89b-12d3-a452-426614874001', 'Yordan Tsolov', '4723191723910005', NOW(), '535', '11/26',
        '003e4567-e89b-12d3-a456-426614174000'),

       ('306e4567-e89b-12d3-a452-426614874001', 'Yordan Tsolov', '5723191723910006', NOW(), '757', '11/26',
        '003e4567-e89b-12d3-a456-426614174000'),

       ('306e4567-e89b-12d3-a452-426614879001', 'Yordan Tsolov', '9723191723910006', NOW(), '751', '11/26',
        '003e4567-e89b-12d3-a456-426614174000'),

       ('407e4567-e89b-12d3-a452-426614874001', 'Dimitar Dimitrov', '4723191723910007', NOW(), '834', '11/26',
        '004e4567-e89b-12d3-a456-426614174000'),

       ('408e4567-e89b-12d3-a452-426614874001', 'Dimitar Dimitrov', '5723191723910008', NOW(), '845', '11/26',
        '004e4567-e89b-12d3-a456-426614174000'),

       ('408e4567-e89b-12d3-a452-426614872001', 'Dimitar Dimitrov', '9723191723910008', NOW(), '445', '11/28',
        '004e4567-e89b-12d3-a456-426614174000'),

       ('509e4567-e89b-12d3-a452-426614874001', 'Ivan Georgiev', '4723191723910009', NOW(), '104', '11/26',
        '005e4567-e89b-12d3-a456-426614174000'),

       ('510e4567-e89b-12d3-a452-426614874001', 'Ivan Georgiev', '5723191723910010', NOW(), '569', '11/28',
        '005e4567-e89b-12d3-a456-426614174000'),

       ('510e4567-e89b-12d3-a452-426617874001', 'Ivan Georgiev', '9723191723910010', NOW(), '169', '11/28',
        '005e4567-e89b-12d3-a456-426614174000'),

       ('611e4567-e89b-12d3-a452-426614874001', 'Daniel Kolev', '4723191723910011', NOW(), '265', '11/26',
        '006e4567-e89b-12d3-a456-426614174000'),

       ('612e4567-e89b-12d3-a452-426614874001', 'Daniel Kolev', '5723191723910012', NOW(), '287', '11/26',
        '006e4567-e89b-12d3-a456-426614174000'),

       ('612e4567-e89b-12d3-a452-126614874001', 'Daniel Kolev', '9723191723910012', NOW(), '187', '11/26',
        '006e4567-e89b-12d3-a456-426614174000'),

       ('713e4567-e89b-12d3-a452-426614874001', 'Chavdar Tsvetkov', '4723191723910013', NOW(), '306', '11/26',
        '007e4567-e89b-12d3-a456-426614174000'),

       ('714e4567-e89b-12d3-a452-426614874001', 'Chavdar Tsvetkov', '5723191723910014', NOW(), '266', '11/26',
        '007e4567-e89b-12d3-a456-426614174000'),

       ('714e4567-e89b-12d3-a452-426611874001', 'Chavdar Tsvetkov', '9723191723910014', NOW(), '266', '11/26',
        '007e4567-e89b-12d3-a456-426614174000'),

       ('815e4567-e89b-12d3-a452-426614874001', 'Lachezar Lazarov', '5723191723910015', NOW(), '248', '11/26',
        '008e4567-e89b-12d3-a456-426614174000'),

       ('816e4567-e89b-12d3-a452-426614874001', 'Lachezar Lazarov', '4723191723910016', NOW(), '118', '11/26',
        '008e4567-e89b-12d3-a456-426614174000'),

       ('816e4567-e89b-12d3-a452-426613874001', 'Lachezar Lazarov', '9723191723910016', NOW(), '018', '11/26',
        '008e4567-e89b-12d3-a456-426614174000'),

       ('917e4567-e89b-12d3-a452-426614874001', 'Nikolay Pilashev', '5723191723910017', NOW(), '734', '11/26',
        '009e4567-e89b-12d3-a456-426614174000'),

       ('918e4567-e89b-12d3-a452-426614874001', 'Nikolay Pilashev', '4723191723910018', NOW(), '338', '11/26',
        '009e4567-e89b-12d3-a456-426614174000'),

       ('918e4567-e89b-12d3-a452-426619874001', 'Nikolay Pilashev', '9723191723910018', NOW(), '238', '11/26',
        '009e4567-e89b-12d3-a456-426614174000'),

       ('019e4567-e89b-12d3-a452-426614874001', 'Viktor Angelov', '4723191723910019', NOW(), '226', '11/26',
        '010e4567-e89b-12d3-a456-426614174000'),

       ('020e4567-e89b-12d3-a452-426614874002', 'Viktor Angelov', '5723191723910020', NOW(), '152', '10/27',
        '010e4567-e89b-12d3-a456-426614174000'),

       ('020e4567-e89b-12d3-a452-416614874002', 'Viktor Angelov', '9723191723910020', NOW(), '952', '10/27',
        '010e4567-e89b-12d3-a456-426614174000');

INSERT INTO virtual_wallet.wallet (id, balance, currency, created_at, owner_id)
VALUES ('111e4567-e89b-12d3-a452-426614874000', 5000.00, 'BGN', NOW(), '001e4567-e89b-12d3-a456-426614174000'),
       ('111e4567-e89b-12d3-a452-426614874001', 5000.00, 'EUR', NOW(), '001e4567-e89b-12d3-a456-426614174000'),
       ('111e4567-e89b-12d3-a452-426614874002', 30000.00, 'USD', NOW(), '001e4567-e89b-12d3-a456-426614174000'),

       ('222e4567-e89b-12d3-a452-426614874000', 5000.00, 'BGN', NOW(), '002e4567-e89b-12d3-a456-426614174000'),
       ('222e4567-e89b-12d3-a452-426614874001', 5000.00, 'EUR', NOW(), '002e4567-e89b-12d3-a456-426614174000'),
       ('222e4567-e89b-12d3-a452-426614874002', 30000.00, 'USD', NOW(), '002e4567-e89b-12d3-a456-426614174000'),

       ('333e4567-e89b-12d3-a452-426614874000', 5000.00, 'BGN', NOW(), '003e4567-e89b-12d3-a456-426614174000'),
       ('333e4567-e89b-12d3-a452-426614874001', 5000.00, 'EUR', NOW(), '003e4567-e89b-12d3-a456-426614174000'),
       ('333e4567-e89b-12d3-a452-426614874002', 30000.00, 'USD', NOW(), '003e4567-e89b-12d3-a456-426614174000'),

       ('444e4567-e89b-12d3-a452-426614874000', 5000.00, 'BGN', NOW(), '004e4567-e89b-12d3-a456-426614174000'),
       ('444e4567-e89b-12d3-a452-426614874001', 5000.00, 'EUR', NOW(), '004e4567-e89b-12d3-a456-426614174000'),
       ('444e4567-e89b-12d3-a452-426614874002', 30000.00, 'USD', NOW(), '004e4567-e89b-12d3-a456-426614174000'),

       ('555e4567-e89b-12d3-a452-426614874000', 5000.00, 'BGN', NOW(), '005e4567-e89b-12d3-a456-426614174000'),
       ('555e4567-e89b-12d3-a452-426614874001', 5000.00, 'EUR', NOW(), '005e4567-e89b-12d3-a456-426614174000'),
       ('555e4567-e89b-12d3-a452-426614874002', 30000.00, 'USD', NOW(), '005e4567-e89b-12d3-a456-426614174000'),

       ('666e4567-e89b-12d3-a452-426614874000', 5000.00, 'BGN', NOW(), '006e4567-e89b-12d3-a456-426614174000'),
       ('666e4567-e89b-12d3-a452-426614874001', 5000.00, 'EUR', NOW(), '006e4567-e89b-12d3-a456-426614174000'),
       ('666e4567-e89b-12d3-a452-426614874002', 30000.00, 'USD', NOW(), '006e4567-e89b-12d3-a456-426614174000'),

       ('777e4567-e89b-12d3-a452-426614874000', 5000.00, 'BGN', NOW(), '007e4567-e89b-12d3-a456-426614174000'),
       ('777e4567-e89b-12d3-a452-426614874001', 5000.00, 'EUR', NOW(), '007e4567-e89b-12d3-a456-426614174000'),
       ('777e4567-e89b-12d3-a452-426614874002', 30000.00, 'USD', NOW(), '007e4567-e89b-12d3-a456-426614174000'),

       ('888e4567-e89b-12d3-a452-426614874000', 5000.00, 'BGN', NOW(), '008e4567-e89b-12d3-a456-426614174000'),
       ('888e4567-e89b-12d3-a452-426614874001', 5000.00, 'EUR', NOW(), '008e4567-e89b-12d3-a456-426614174000'),
       ('888e4567-e89b-12d3-a452-426614874002', 30000.00, 'USD', NOW(), '008e4567-e89b-12d3-a456-426614174000'),

       ('999e4567-e89b-12d3-a452-426614874000', 5000.00, 'BGN', NOW(), '009e4567-e89b-12d3-a456-426614174000'),
       ('999e4567-e89b-12d3-a452-426614874001', 5000.00, 'EUR', NOW(), '009e4567-e89b-12d3-a456-426614174000'),
       ('999e4567-e89b-12d3-a452-426614874002', 30000.00, 'USD', NOW(), '009e4567-e89b-12d3-a456-426614174000'),

       ('000e4567-e89b-12d3-a452-426614874000', 5000.00, 'BGN', NOW(), '010e4567-e89b-12d3-a456-426614174000'),
       ('000e4567-e89b-12d3-a452-426614874001', 5000.00, 'EUR', NOW(), '010e4567-e89b-12d3-a456-426614174000'),
       ('000e4567-e89b-12d3-a452-426614874002', 30000.00, 'USD', NOW(), '010e4567-e89b-12d3-a456-426614174000');

INSERT INTO virtual_wallet.exchange
(id, amount, to_amount, from_currency, to_currency, exchange_rate, from_wallet_id, to_wallet_id, recipient_username,
 date)
VALUES ('001e0001-e89b-12d3-a452-426614874000', 100.00, 100.00 * 1.9558, 'EUR', 'BGN', 1.9558,
        '111e4567-e89b-12d3-a452-426614874001', '111e4567-e89b-12d3-a452-426614874000', 'georgi', NOW()),
       ('001e0002-e89b-12d3-a452-426614874000', 260.00, 260.00 * 1.0812, 'EUR', 'USD', 1.0812,
        '111e4567-e89b-12d3-a452-426614874001', '111e4567-e89b-12d3-a452-426614874002', 'georgi', NOW()),
       ('001e0003-e89b-12d3-a452-426614874000', 720.00, 720.00 * 0.9235, 'USD', 'EUR', 0.9235,
        '111e4567-e89b-12d3-a452-426614874002', '111e4567-e89b-12d3-a452-426614874001', 'georgi', NOW()),
       ('001e0004-e89b-12d3-a452-426614874000', 90.50, 90.50 * 0.5543, 'BGN', 'USD', 0.5543,
        '111e4567-e89b-12d3-a452-426614874000', '111e4567-e89b-12d3-a452-426614874002', 'georgi', NOW()),
       ('001e0005-e89b-12d3-a452-426614874000', 450.00, 450.00 * 0.5113, 'BGN', 'EUR', 0.5113,
        '111e4567-e89b-12d3-a452-426614874000', '111e4567-e89b-12d3-a452-426614874001', 'georgi', NOW()),

       ('006e0001-e89b-12d3-a452-426614874000', 100.00, 100.00 * 1.9558, 'EUR', 'BGN', 1.9558,
        '222e4567-e89b-12d3-a452-426614874001', '222e4567-e89b-12d3-a452-426614874000', 'vankata', NOW()),
       ('007e0002-e89b-12d3-a452-426614874000', 260.00, 260.00 * 1.0812, 'EUR', 'USD', 1.0812,
        '222e4567-e89b-12d3-a452-426614874001', '222e4567-e89b-12d3-a452-426614874002', 'vankata', NOW()),
       ('008e0003-e89b-12d3-a452-426614874000', 720.00, 720.00 * 0.9235, 'USD', 'EUR', 0.9235,
        '222e4567-e89b-12d3-a452-426614874002', '222e4567-e89b-12d3-a452-426614874001', 'vankata', NOW()),
       ('009e0004-e89b-12d3-a452-426614874000', 90.50, 90.50 * 0.5543, 'BGN', 'USD', 0.5543,
        '222e4567-e89b-12d3-a452-426614874000', '222e4567-e89b-12d3-a452-426614874002', 'vankata', NOW()),
       ('010e0005-e89b-12d3-a452-426614874000', 450.00, 450.00 * 0.5113, 'BGN', 'EUR', 0.5113,
        '222e4567-e89b-12d3-a452-426614874000', '222e4567-e89b-12d3-a452-426614874001', 'vankata', NOW()),

       ('011e0001-e89b-12d3-a452-426614874000', 100.00, 100.00 * 1.9558, 'EUR', 'BGN', 1.9558,
        '333e4567-e89b-12d3-a452-426614874001', '333e4567-e89b-12d3-a452-426614874000', 'yordan', NOW()),
       ('012e0002-e89b-12d3-a452-426614874000', 260.00, 260.00 * 1.0812, 'EUR', 'USD', 1.0812,
        '333e4567-e89b-12d3-a452-426614874001', '333e4567-e89b-12d3-a452-426614874002', 'yordan', NOW()),
       ('013e0003-e89b-12d3-a452-426614874000', 720.00, 720.00 * 0.9235, 'USD', 'EUR', 0.9235,
        '333e4567-e89b-12d3-a452-426614874002', '333e4567-e89b-12d3-a452-426614874001', 'yordan', NOW()),
       ('014e0004-e89b-12d3-a452-426614874000', 90.50, 90.50 * 0.5543, 'BGN', 'USD', 0.5543,
        '333e4567-e89b-12d3-a452-426614874000', '333e4567-e89b-12d3-a452-426614874002', 'yordan', NOW()),
       ('015e0005-e89b-12d3-a452-426614874000', 450.00, 450.00 * 0.5113, 'BGN', 'EUR', 0.5113,
        '333e4567-e89b-12d3-a452-426614874000', '333e4567-e89b-12d3-a452-426614874001', 'yordan', NOW()),

       ('016e0001-e89b-12d3-a452-426614874000', 100.00, 100.00 * 1.9558, 'EUR', 'BGN', 1.9558,
        '444e4567-e89b-12d3-a452-426614874001', '444e4567-e89b-12d3-a452-426614874000', 'dimitar', NOW()),
       ('017e0002-e89b-12d3-a452-426614874000', 260.00, 260.00 * 1.0812, 'EUR', 'USD', 1.0812,
        '444e4567-e89b-12d3-a452-426614874001', '444e4567-e89b-12d3-a452-426614874002', 'dimitar', NOW()),
       ('018e0003-e89b-12d3-a452-426614874000', 720.00, 720.00 * 0.9235, 'USD', 'EUR', 0.9235,
        '444e4567-e89b-12d3-a452-426614874002', '444e4567-e89b-12d3-a452-426614874001', 'dimitar', NOW()),
       ('019e0004-e89b-12d3-a452-426614874000', 90.50, 90.50 * 0.5543, 'BGN', 'USD', 0.5543,
        '444e4567-e89b-12d3-a452-426614874000', '444e4567-e89b-12d3-a452-426614874002', 'dimitar', NOW()),
       ('020e0005-e89b-12d3-a452-426614874000', 450.00, 450.00 * 0.5113, 'BGN', 'EUR', 0.5113,
        '444e4567-e89b-12d3-a452-426614874000', '444e4567-e89b-12d3-a452-426614874001', 'dimitar', NOW()),

       ('021e0001-e89b-12d3-a452-426614874000', 100.00, 100.00 * 1.9558, 'EUR', 'BGN', 1.9558,
        '555e4567-e89b-12d3-a452-426614874001', '555e4567-e89b-12d3-a452-426614874000', 'ivan', NOW()),
       ('022e0002-e89b-12d3-a452-426614874000', 260.00, 260.00 * 1.0812, 'EUR', 'USD', 1.0812,
        '555e4567-e89b-12d3-a452-426614874001', '555e4567-e89b-12d3-a452-426614874002', 'ivan', NOW()),
       ('023e0003-e89b-12d3-a452-426614874000', 720.00, 720.00 * 0.9235, 'USD', 'EUR', 0.9235,
        '555e4567-e89b-12d3-a452-426614874002', '555e4567-e89b-12d3-a452-426614874001', 'ivan', NOW()),
       ('024e0004-e89b-12d3-a452-426614874000', 90.50, 90.50 * 0.5543, 'BGN', 'USD', 0.5543,
        '555e4567-e89b-12d3-a452-426614874000', '555e4567-e89b-12d3-a452-426614874002', 'ivan', NOW()),
       ('025e0005-e89b-12d3-a452-426614874000', 450.00, 450.00 * 0.5113, 'BGN', 'EUR', 0.5113,
        '555e4567-e89b-12d3-a452-426614874000', '555e4567-e89b-12d3-a452-426614874001', 'ivan', NOW()),

       ('026e0001-e89b-12d3-a452-426614874000', 100.00, 100.00 * 1.9558, 'EUR', 'BGN', 1.9558,
        '666e4567-e89b-12d3-a452-426614874001', '666e4567-e89b-12d3-a452-426614874000', 'daniel', NOW()),
       ('027e0002-e89b-12d3-a452-426614874000', 260.00, 260.00 * 1.0812, 'EUR', 'USD', 1.0812,
        '666e4567-e89b-12d3-a452-426614874001', '666e4567-e89b-12d3-a452-426614874002', 'daniel', NOW()),
       ('028e0003-e89b-12d3-a452-426614874000', 720.00, 720.00 * 0.9235, 'USD', 'EUR', 0.9235,
        '666e4567-e89b-12d3-a452-426614874002', '666e4567-e89b-12d3-a452-426614874001', 'daniel', NOW()),
       ('029e0004-e89b-12d3-a452-426614874000', 90.50, 90.50 * 0.5543, 'BGN', 'USD', 0.5543,
        '666e4567-e89b-12d3-a452-426614874000', '666e4567-e89b-12d3-a452-426614874002', 'daniel', NOW()),
       ('030e0005-e89b-12d3-a452-426614874000', 450.00, 450.00 * 0.5113, 'BGN', 'EUR', 0.5113,
        '666e4567-e89b-12d3-a452-426614874000', '666e4567-e89b-12d3-a452-426614874001', 'daniel', NOW()),

       ('031e0001-e89b-12d3-a452-426614874000', 100.00, 100.00 * 1.9558, 'EUR', 'BGN', 1.9558,
        '777e4567-e89b-12d3-a452-426614874001', '777e4567-e89b-12d3-a452-426614874000', 'chavdar', NOW()),
       ('032e0002-e89b-12d3-a452-426614874000', 260.00, 260.00 * 1.0812, 'EUR', 'USD', 1.0812,
        '777e4567-e89b-12d3-a452-426614874001', '777e4567-e89b-12d3-a452-426614874002', 'chavdar', NOW()),
       ('033e0003-e89b-12d3-a452-426614874000', 720.00, 720.00 * 0.9235, 'USD', 'EUR', 0.9235,
        '777e4567-e89b-12d3-a452-426614874002', '777e4567-e89b-12d3-a452-426614874001', 'chavdar', NOW()),
       ('034e0004-e89b-12d3-a452-426614874000', 90.50, 90.50 * 0.5543, 'BGN', 'USD', 0.5543,
        '777e4567-e89b-12d3-a452-426614874000', '777e4567-e89b-12d3-a452-426614874002', 'chavdar', NOW()),
       ('035e0005-e89b-12d3-a452-426614874000', 450.00, 450.00 * 0.5113, 'BGN', 'EUR', 0.5113,
        '777e4567-e89b-12d3-a452-426614874000', '777e4567-e89b-12d3-a452-426614874001', 'chavdar', NOW()),

       ('036e0001-e89b-12d3-a452-426614874000', 100.00, 100.00 * 1.9558, 'EUR', 'BGN', 1.9558,
        '888e4567-e89b-12d3-a452-426614874001', '888e4567-e89b-12d3-a452-426614874000', 'lachezar', NOW()),
       ('037e0002-e89b-12d3-a452-426614874000', 260.00, 260.00 * 1.0812, 'EUR', 'USD', 1.0812,
        '888e4567-e89b-12d3-a452-426614874001', '888e4567-e89b-12d3-a452-426614874002', 'lachezar', NOW()),
       ('038e0003-e89b-12d3-a452-426614874000', 720.00, 720.00 * 0.9235, 'USD', 'EUR', 0.9235,
        '888e4567-e89b-12d3-a452-426614874002', '888e4567-e89b-12d3-a452-426614874001', 'lachezar', NOW()),
       ('039e0004-e89b-12d3-a452-426614874000', 90.50, 90.50 * 0.5543, 'BGN', 'USD', 0.5543,
        '888e4567-e89b-12d3-a452-426614874000', '888e4567-e89b-12d3-a452-426614874002', 'lachezar', NOW()),
       ('040e0005-e89b-12d3-a452-426614874000', 450.00, 450.00 * 0.5113, 'BGN', 'EUR', 0.5113,
        '888e4567-e89b-12d3-a452-426614874000', '888e4567-e89b-12d3-a452-426614874001', 'lachezar', NOW()),

       ('041e0001-e89b-12d3-a452-426614874000', 100.00, 100.00 * 1.9558, 'EUR', 'BGN', 1.9558,
        '999e4567-e89b-12d3-a452-426614874001', '999e4567-e89b-12d3-a452-426614874000', 'nikolay', NOW()),
       ('042e0002-e89b-12d3-a452-426614874000', 260.00, 260.00 * 1.0812, 'EUR', 'USD', 1.0812,
        '999e4567-e89b-12d3-a452-426614874001', '999e4567-e89b-12d3-a452-426614874002', 'nikolay', NOW()),
       ('043e0003-e89b-12d3-a452-426614874000', 720.00, 720.00 * 0.9235, 'USD', 'EUR', 0.9235,
        '999e4567-e89b-12d3-a452-426614874002', '999e4567-e89b-12d3-a452-426614874001', 'nikolay', NOW()),
       ('044e0004-e89b-12d3-a452-426614874000', 90.50, 90.50 * 0.5543, 'BGN', 'USD', 0.5543,
        '999e4567-e89b-12d3-a452-426614874000', '999e4567-e89b-12d3-a452-426614874002', 'nikolay', NOW()),
       ('045e0005-e89b-12d3-a452-426614874000', 450.00, 450.00 * 0.5113, 'BGN', 'EUR', 0.5113,
        '999e4567-e89b-12d3-a452-426614874000', '999e4567-e89b-12d3-a452-426614874001', 'nikolay', NOW()),

       ('046e0001-e89b-12d3-a452-426614874000', 100.00, 100.00 * 1.9558, 'EUR', 'BGN', 1.9558,
        '000e4567-e89b-12d3-a452-426614874001', '000e4567-e89b-12d3-a452-426614874000', 'viktor', NOW()),
       ('047e0002-e89b-12d3-a452-426614874000', 260.00, 260.00 * 1.0812, 'EUR', 'USD', 1.0812,
        '000e4567-e89b-12d3-a452-426614874001', '000e4567-e89b-12d3-a452-426614874002', 'viktor', NOW()),
       ('048e0003-e89b-12d3-a452-426614874000', 720.00, 720.00 * 0.9235, 'USD', 'EUR', 0.9235,
        '000e4567-e89b-12d3-a452-426614874002', '000e4567-e89b-12d3-a452-426614874001', 'viktor', NOW()),
       ('049e0004-e89b-12d3-a452-426614874000', 90.50, 90.50 * 0.5543, 'BGN', 'USD', 0.5543,
        '000e4567-e89b-12d3-a452-426614874000', '000e4567-e89b-12d3-a452-426614874002', 'viktor', NOW()),
       ('050e0005-e89b-12d3-a452-426614874000', 450.00, 450.00 * 0.5113, 'BGN', 'EUR', 0.5113,
        '000e4567-e89b-12d3-a452-426614874000', '000e4567-e89b-12d3-a452-426614874001', 'viktor', NOW());

INSERT INTO virtual_wallet.transaction (id, amount, currency, sender_username, recipient_username, sender_wallet_id,
                         recipient_wallet_id, date, description)
VALUES ('050e1000-e89b-12d3-a452-426614874000', 100.00, 'BGN', 'georgi', 'vankata',
        '111e4567-e89b-12d3-a452-426614874000', '222e4567-e89b-12d3-a452-426614874000', NOW(), 'happy birthday! <3'),
       ('050e1001-e89b-12d3-a452-426614874000', 70.00, 'BGN', 'georgi', 'yordan',
        '111e4567-e89b-12d3-a452-426614874000', '333e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1002-e89b-12d3-a452-426614874000', 99.99, 'BGN', 'georgi', 'dimitar',
        '111e4567-e89b-12d3-a452-426614874000', '444e4567-e89b-12d3-a452-426614874000', NOW(), 'the money i owe you'),
       ('050e1003-e89b-12d3-a452-426614874000', 100.00, 'EUR', 'georgi', 'ivan',
        '111e4567-e89b-12d3-a452-426614874001', '555e4567-e89b-12d3-a452-426614874001', NOW(), 'for plane tickets'),
       ('050e1004-e89b-12d3-a452-426614874000', 50.00, 'EUR', 'georgi', 'daniel',
        '111e4567-e89b-12d3-a452-426614874001', '666e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1005-e89b-12d3-a452-426614874000', 70.00, 'EUR', 'georgi', 'chavdar',
        '111e4567-e89b-12d3-a452-426614874001', '777e4567-e89b-12d3-a452-426614874001', NOW(), 'happy birthday! <3'),
       ('050e1006-e89b-12d3-a452-426614874000', 100.00, 'USD', 'georgi', 'lachezar',
        '111e4567-e89b-12d3-a452-426614874002', '888e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),
       ('050e1007-e89b-12d3-a452-426614874000', 99.99, 'USD', 'georgi', 'nikolay',
        '111e4567-e89b-12d3-a452-426614874002', '999e4567-e89b-12d3-a452-426614874002', NOW(), 'for plane tickets'),
       ('050e1008-e89b-12d3-a452-426614874000', 20.00, 'USD', 'georgi', 'viktor',
        '111e4567-e89b-12d3-a452-426614874002', '000e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),

       ('050e1010-e89b-12d3-a452-426614874000', 100.00, 'BGN', 'vankata', 'georgi',
        '222e4567-e89b-12d3-a452-426614874000', '111e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1011-e89b-12d3-a452-426614874000', 50.00, 'BGN', 'vankata', 'yordan',
        '222e4567-e89b-12d3-a452-426614874000', '333e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1012-e89b-12d3-a452-426614874000', 70.00, 'BGN', 'vankata', 'dimitar',
        '222e4567-e89b-12d3-a452-426614874000', '444e4567-e89b-12d3-a452-426614874000', NOW(), 'happy birthday! <3'),
       ('050e1013-e89b-12d3-a452-426614874000', 100.00, 'EUR', 'vankata', 'ivan',
        '222e4567-e89b-12d3-a452-426614874001', '555e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1014-e89b-12d3-a452-426614874000', 99.99, 'EUR', 'vankata', 'daniel',
        '222e4567-e89b-12d3-a452-426614874001', '666e4567-e89b-12d3-a452-426614874001', NOW(), 'for plane tickets'),
       ('050e1015-e89b-12d3-a452-426614874000', 50.00, 'EUR', 'vankata', 'chavdar',
        '222e4567-e89b-12d3-a452-426614874001', '777e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1016-e89b-12d3-a452-426614874000', 100.00, 'USD', 'vankata', 'lachezar',
        '222e4567-e89b-12d3-a452-426614874002', '888e4567-e89b-12d3-a452-426614874002', NOW(), 'the money i owe you'),
       ('050e1017-e89b-12d3-a452-426614874000', 99.99, 'USD', 'vankata', 'nikolay',
        '222e4567-e89b-12d3-a452-426614874002', '999e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),
       ('050e1018-e89b-12d3-a452-426614874000', 20.00, 'USD', 'vankata', 'viktor',
        '222e4567-e89b-12d3-a452-426614874002', '000e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),

       ('050e1020-e89b-12d3-a452-426614874000', 100.00, 'BGN', 'yordan', 'vankata',
        '333e4567-e89b-12d3-a452-426614874000', '222e4567-e89b-12d3-a452-426614874000', NOW(), 'for plane tickets'),
       ('050e1021-e89b-12d3-a452-426614874000', 70.00, 'BGN', 'yordan', 'georgi',
        '333e4567-e89b-12d3-a452-426614874000', '111e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1022-e89b-12d3-a452-426614874000', 50.00, 'BGN', 'yordan', 'dimitar',
        '333e4567-e89b-12d3-a452-426614874000', '444e4567-e89b-12d3-a452-426614874000', NOW(), 'happy birthday! <3'),
       ('050e1023-e89b-12d3-a452-426614874000', 100.00, 'EUR', 'yordan', 'ivan',
        '333e4567-e89b-12d3-a452-426614874001', '555e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1024-e89b-12d3-a452-426614874000', 99.99, 'EUR', 'yordan', 'daniel',
        '333e4567-e89b-12d3-a452-426614874001', '666e4567-e89b-12d3-a452-426614874001', NOW(), 'the money i owe you'),
       ('050e1025-e89b-12d3-a452-426614874000', 50.00, 'EUR', 'yordan', 'chavdar',
        '333e4567-e89b-12d3-a452-426614874001', '777e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1026-e89b-12d3-a452-426614874000', 100.00, 'USD', 'yordan', 'lachezar',
        '333e4567-e89b-12d3-a452-426614874002', '888e4567-e89b-12d3-a452-426614874002', NOW(), 'for plane tickets'),
       ('050e1027-e89b-12d3-a452-426614874000', 99.99, 'USD', 'yordan', 'nikolay',
        '333e4567-e89b-12d3-a452-426614874002', '999e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),
       ('050e1028-e89b-12d3-a452-426614874000', 70.00, 'USD', 'yordan', 'viktor',
        '333e4567-e89b-12d3-a452-426614874002', '000e4567-e89b-12d3-a452-426614874002', NOW(), 'happy birthday! <3'),

       ('050e1030-e89b-12d3-a452-426614874000', 100.00, 'BGN', 'dimitar', 'vankata',
        '444e4567-e89b-12d3-a452-426614874000', '222e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1031-e89b-12d3-a452-426614874000', 200.00, 'BGN', 'dimitar', 'yordan',
        '444e4567-e89b-12d3-a452-426614874000', '333e4567-e89b-12d3-a452-426614874000', NOW(), 'the money i owe you'),
       ('050e1032-e89b-12d3-a452-426614874000', 99.99, 'BGN', 'dimitar', 'georgi',
        '444e4567-e89b-12d3-a452-426614874000', '111e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1033-e89b-12d3-a452-426614874000', 200.00, 'EUR', 'dimitar', 'ivan',
        '444e4567-e89b-12d3-a452-426614874001', '555e4567-e89b-12d3-a452-426614874001', NOW(), 'for plane tickets'),
       ('050e1034-e89b-12d3-a452-426614874000', 20.00, 'EUR', 'dimitar', 'daniel',
        '444e4567-e89b-12d3-a452-426614874001', '666e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1035-e89b-12d3-a452-426614874000', 50.00, 'EUR', 'dimitar', 'chavdar',
        '444e4567-e89b-12d3-a452-426614874001', '777e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1036-e89b-12d3-a452-426614874000', 70.00, 'USD', 'dimitar', 'lachezar',
        '444e4567-e89b-12d3-a452-426614874002', '888e4567-e89b-12d3-a452-426614874002', NOW(), 'happy birthday! <3'),
       ('050e1037-e89b-12d3-a452-426614874000', 200.00, 'USD', 'dimitar', 'nikolay',
        '444e4567-e89b-12d3-a452-426614874002', '999e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),
       ('050e1038-e89b-12d3-a452-426614874000', 99.99, 'USD', 'dimitar', 'viktor',
        '444e4567-e89b-12d3-a452-426614874002', '000e4567-e89b-12d3-a452-426614874002', NOW(), 'for plane tickets'),

       ('050e1040-e89b-12d3-a452-426614874000', 200.00, 'BGN', 'ivan', 'vankata',
        '555e4567-e89b-12d3-a452-426614874000', '222e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1041-e89b-12d3-a452-426614874000', 50.00, 'BGN', 'ivan', 'yordan',
        '555e4567-e89b-12d3-a452-426614874000', '333e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1042-e89b-12d3-a452-426614874000', 20.00, 'BGN', 'ivan', 'dimitar',
        '555e4567-e89b-12d3-a452-426614874000', '444e4567-e89b-12d3-a452-426614874000', NOW(), 'for plane tickets'),
       ('050e1043-e89b-12d3-a452-426614874000', 200.00, 'EUR', 'ivan', 'georgi',
        '555e4567-e89b-12d3-a452-426614874001', '111e4567-e89b-12d3-a452-426614874001', NOW(), 'happy birthday! <3'),
       ('050e1044-e89b-12d3-a452-426614874000', 70.00, 'EUR', 'ivan', 'daniel',
        '555e4567-e89b-12d3-a452-426614874001', '666e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1045-e89b-12d3-a452-426614874000', 99.99, 'EUR', 'ivan', 'chavdar',
        '555e4567-e89b-12d3-a452-426614874001', '777e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1046-e89b-12d3-a452-426614874000', 200.00, 'USD', 'ivan', 'lachezar',
        '555e4567-e89b-12d3-a452-426614874002', '888e4567-e89b-12d3-a452-426614874002', NOW(), 'the money i owe you'),
       ('050e1047-e89b-12d3-a452-426614874000', 50.00, 'USD', 'ivan', 'nikolay',
        '555e4567-e89b-12d3-a452-426614874002', '999e4567-e89b-12d3-a452-426614874002', NOW(), 'for plane tickets'),
       ('050e1048-e89b-12d3-a452-426614874000', 99.99, 'USD', 'ivan', 'viktor',
        '555e4567-e89b-12d3-a452-426614874002', '000e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),

       ('050e1050-e89b-12d3-a452-426614874000', 50.00, 'BGN', 'daniel', 'vankata',
        '666e4567-e89b-12d3-a452-426614874000', '222e4567-e89b-12d3-a452-426614874000', NOW(), 'for plane tickets'),
       ('050e1051-e89b-12d3-a452-426614874000', 70.00, 'BGN', 'daniel', 'yordan',
        '666e4567-e89b-12d3-a452-426614874000', '333e4567-e89b-12d3-a452-426614874000', NOW(), 'happy birthday! <3'),
       ('050e1052-e89b-12d3-a452-426614874000', 200.00, 'BGN', 'daniel', 'dimitar',
        '666e4567-e89b-12d3-a452-426614874000', '444e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1053-e89b-12d3-a452-426614874000', 99.99, 'EUR', 'daniel', 'ivan',
        '666e4567-e89b-12d3-a452-426614874001', '555e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1054-e89b-12d3-a452-426614874000', 20.00, 'EUR', 'daniel', 'georgi',
        '666e4567-e89b-12d3-a452-426614874001', '111e4567-e89b-12d3-a452-426614874001', NOW(), 'the money i owe you'),
       ('050e1055-e89b-12d3-a452-426614874000', 200.00, 'EUR', 'daniel', 'chavdar',
        '666e4567-e89b-12d3-a452-426614874001', '777e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1056-e89b-12d3-a452-426614874000', 50.00, 'USD', 'daniel', 'lachezar',
        '666e4567-e89b-12d3-a452-426614874002', '888e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),
       ('050e1057-e89b-12d3-a452-426614874000', 99.99, 'USD', 'daniel', 'nikolay',
        '666e4567-e89b-12d3-a452-426614874002', '999e4567-e89b-12d3-a452-426614874002', NOW(), 'happy birthday! <3'),
       ('050e1058-e89b-12d3-a452-426614874000', 50.00, 'USD', 'daniel', 'viktor',
        '666e4567-e89b-12d3-a452-426614874002', '000e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),

       ('050e1060-e89b-12d3-a452-426614874000', 20.00, 'BGN', 'chavdar', 'vankata',
        '777e4567-e89b-12d3-a452-426614874000', '222e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1061-e89b-12d3-a452-426614874000', 200.00, 'BGN', 'chavdar', 'yordan',
        '777e4567-e89b-12d3-a452-426614874000', '333e4567-e89b-12d3-a452-426614874000', NOW(), 'the money i owe you'),
       ('050e1062-e89b-12d3-a452-426614874000', 99.99, 'BGN', 'chavdar', 'dimitar',
        '777e4567-e89b-12d3-a452-426614874000', '444e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1063-e89b-12d3-a452-426614874000', 50.00, 'EUR', 'chavdar', 'ivan',
        '777e4567-e89b-12d3-a452-426614874001', '555e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1064-e89b-12d3-a452-426614874000', 99.99, 'EUR', 'chavdar', 'daniel',
        '777e4567-e89b-12d3-a452-426614874001', '666e4567-e89b-12d3-a452-426614874001', NOW(), 'for plane tickets'),
       ('050e1065-e89b-12d3-a452-426614874000', 200.00, 'EUR', 'chavdar', 'georgi',
        '777e4567-e89b-12d3-a452-426614874001', '111e4567-e89b-12d3-a452-426614874001', NOW(), 'happy birthday! <3'),
       ('050e1066-e89b-12d3-a452-426614874000', 50.00, 'USD', 'chavdar', 'lachezar',
        '777e4567-e89b-12d3-a452-426614874002', '888e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),
       ('050e1067-e89b-12d3-a452-426614874000', 20.00, 'USD', 'chavdar', 'nikolay',
        '777e4567-e89b-12d3-a452-426614874002', '999e4567-e89b-12d3-a452-426614874002', NOW(), 'the money i owe you'),
       ('050e1068-e89b-12d3-a452-426614874000', 200.00, 'USD', 'chavdar', 'viktor',
        '777e4567-e89b-12d3-a452-426614874002', '000e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),

       ('050e1070-e89b-12d3-a452-426614874000', 99.99, 'BGN', 'lachezar', 'vankata',
        '888e4567-e89b-12d3-a452-426614874000', '222e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1071-e89b-12d3-a452-426614874000', 50.00, 'BGN', 'lachezar', 'yordan',
        '888e4567-e89b-12d3-a452-426614874000', '333e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1072-e89b-12d3-a452-426614874000', 200.00, 'BGN', 'lachezar', 'dimitar',
        '888e4567-e89b-12d3-a452-426614874000', '444e4567-e89b-12d3-a452-426614874000', NOW(), 'happy birthday! <3'),
       ('050e1073-e89b-12d3-a452-426614874000', 50.00, 'EUR', 'lachezar', 'ivan',
        '888e4567-e89b-12d3-a452-426614874001', '555e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1074-e89b-12d3-a452-426614874000', 99.99, 'EUR', 'lachezar', 'daniel',
        '888e4567-e89b-12d3-a452-426614874001', '666e4567-e89b-12d3-a452-426614874001', NOW(), 'the money i owe you'),
       ('050e1075-e89b-12d3-a452-426614874000', 200.00, 'EUR', 'lachezar', 'chavdar',
        '888e4567-e89b-12d3-a452-426614874001', '777e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1076-e89b-12d3-a452-426614874000', 20.00, 'USD', 'lachezar', 'georgi',
        '888e4567-e89b-12d3-a452-426614874002', '111e4567-e89b-12d3-a452-426614874002', NOW(), 'for plane tickets'),
       ('050e1077-e89b-12d3-a452-426614874000', 50.00, 'USD', 'lachezar', 'nikolay',
        '888e4567-e89b-12d3-a452-426614874002', '999e4567-e89b-12d3-a452-426614874002', NOW(), 'the money i owe you'),
       ('050e1078-e89b-12d3-a452-426614874000', 200.00, 'USD', 'lachezar', 'viktor',
        '888e4567-e89b-12d3-a452-426614874002', '000e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),

       ('050e1080-e89b-12d3-a452-426614874000', 99.99, 'BGN', 'nikolay', 'vankata',
        '999e4567-e89b-12d3-a452-426614874000', '222e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1081-e89b-12d3-a452-426614874000', 50.00, 'BGN', 'nikolay', 'yordan',
        '999e4567-e89b-12d3-a452-426614874000', '333e4567-e89b-12d3-a452-426614874000', NOW(), 'happy birthday! <3'),
       ('050e1082-e89b-12d3-a452-426614874000', 20.00, 'BGN', 'nikolay', 'dimitar',
        '999e4567-e89b-12d3-a452-426614874000', '444e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1083-e89b-12d3-a452-426614874000', 200.00, 'EUR', 'nikolay', 'ivan',
        '999e4567-e89b-12d3-a452-426614874001', '555e4567-e89b-12d3-a452-426614874001', NOW(), 'the money i owe you'),
       ('050e1084-e89b-12d3-a452-426614874000', 99.99, 'EUR', 'nikolay', 'daniel',
        '999e4567-e89b-12d3-a452-426614874001', '666e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1085-e89b-12d3-a452-426614874000', 50.00, 'EUR', 'nikolay', 'chavdar',
        '999e4567-e89b-12d3-a452-426614874001', '777e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1086-e89b-12d3-a452-426614874000', 200.00, 'USD', 'nikolay', 'lachezar',
        '999e4567-e89b-12d3-a452-426614874002', '888e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),
       ('050e1087-e89b-12d3-a452-426614874000', 50.00, 'USD', 'nikolay', 'georgi',
        '999e4567-e89b-12d3-a452-426614874002', '111e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),
       ('050e1088-e89b-12d3-a452-426614874000', 99.99, 'USD', 'nikolay', 'viktor',
        '999e4567-e89b-12d3-a452-426614874002', '000e4567-e89b-12d3-a452-426614874002', NOW(), 'for plane tickets'),

       ('050e1090-e89b-12d3-a452-426614874000', 20.00, 'BGN', 'viktor', 'vankata',
        '000e4567-e89b-12d3-a452-426614874000', '222e4567-e89b-12d3-a452-426614874000', NOW(), 'happy birthday! <3'),
       ('050e1091-e89b-12d3-a452-426614874000', 200.00, 'BGN', 'viktor', 'yordan',
        '000e4567-e89b-12d3-a452-426614874000', '333e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1092-e89b-12d3-a452-426614874000', 50.00, 'BGN', 'viktor', 'dimitar',
        '000e4567-e89b-12d3-a452-426614874000', '444e4567-e89b-12d3-a452-426614874000', NOW(), 'transaction'),
       ('050e1093-e89b-12d3-a452-426614874000', 99.99, 'EUR', 'viktor', 'ivan',
        '000e4567-e89b-12d3-a452-426614874001', '555e4567-e89b-12d3-a452-426614874001', NOW(), 'transaction'),
       ('050e1094-e89b-12d3-a452-426614874000', 200.00, 'EUR', 'viktor', 'daniel',
        '000e4567-e89b-12d3-a452-426614874001', '666e4567-e89b-12d3-a452-426614874001', NOW(), 'the money i owe you'),
       ('050e1095-e89b-12d3-a452-426614874000', 20.00, 'EUR', 'viktor', 'chavdar',
        '000e4567-e89b-12d3-a452-426614874001', '777e4567-e89b-12d3-a452-426614874001', NOW(), 'the money i owe you'),
       ('050e1096-e89b-12d3-a452-426614874000', 50.00, 'USD', 'viktor', 'lachezar',
        '000e4567-e89b-12d3-a452-426614874002', '888e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),
       ('050e1097-e89b-12d3-a452-426614874000', 70.00, 'USD', 'viktor', 'nikolay',
        '000e4567-e89b-12d3-a452-426614874002', '999e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction'),
       ('050e1098-e89b-12d3-a452-426614874000', 50.00, 'USD', 'viktor', 'georgi',
        '000e4567-e89b-12d3-a452-426614874002', '111e4567-e89b-12d3-a452-426614874002', NOW(), 'transaction')
;

INSERT INTO virtual_wallet.transfer (id, amount, currency, card_id, wallet_id, status, recipient_username, date)
VALUES ('090e0001-e89b-12d3-a456-426682274099', 5347.43, 'BGN', '101e4567-e89b-12d3-a452-426614184001',
        '111e4567-e89b-12d3-a452-426614874000', 'APPROVED', 'georgi', NOW()),
       ('090e0002-e89b-12d3-a456-426682274099', 7087.80, 'EUR', '102e4567-e89b-12d3-a452-426614874001',
        '111e4567-e89b-12d3-a452-426614874001', 'APPROVED', 'georgi', NOW()),
       ('090e0003-e89b-12d3-a456-426682274099', 3990.50, 'USD', '102e4567-e89b-12d3-a452-426614874001',
        '111e4567-e89b-12d3-a452-426614874002', 'APPROVED', 'georgi', NOW()),

       ('090e0004-e89b-12d3-a456-426682274099', 5347.43, 'BGN', '203e4567-e89b-12d3-a452-426614874001',
        '222e4567-e89b-12d3-a452-426614874000', 'APPROVED', 'vankata', NOW()),
       ('090e0005-e89b-12d3-a456-426682274099', 7087.80, 'EUR', '203e4567-e89b-12d3-a452-426614874001',
        '222e4567-e89b-12d3-a452-426614874001', 'APPROVED', 'vankata', NOW()),
       ('090e0006-e89b-12d3-a456-426682274099', 3990.50, 'USD', '204e4567-e89b-12d3-a452-426614874001',
        '222e4567-e89b-12d3-a452-426614874002', 'APPROVED', 'vankata', NOW()),

       ('090e0007-e89b-12d3-a456-426682274099', 5347.43, 'BGN', '305e4567-e89b-12d3-a452-426614874001',
        '333e4567-e89b-12d3-a452-426614874000', 'APPROVED', 'yordan', NOW()),
       ('090e0008-e89b-12d3-a456-426682274099', 7087.80, 'EUR', '305e4567-e89b-12d3-a452-426614874001',
        '333e4567-e89b-12d3-a452-426614874001', 'APPROVED', 'yordan', NOW()),
       ('090e0009-e89b-12d3-a456-426682274099', 3990.50, 'USD', '306e4567-e89b-12d3-a452-426614874001',
        '333e4567-e89b-12d3-a452-426614874002', 'APPROVED', 'yordan', NOW()),

       ('090e0010-e89b-12d3-a456-426682274099', 5347.43, 'BGN', '407e4567-e89b-12d3-a452-426614874001',
        '444e4567-e89b-12d3-a452-426614874000', 'APPROVED', 'dimitar', NOW()),
       ('090e0011-e89b-12d3-a456-426682274099', 7087.80, 'EUR', '407e4567-e89b-12d3-a452-426614874001',
        '444e4567-e89b-12d3-a452-426614874001', 'APPROVED', 'dimitar', NOW()),
       ('090e0012-e89b-12d3-a456-426682274099', 3990.50, 'USD', '408e4567-e89b-12d3-a452-426614874001',
        '444e4567-e89b-12d3-a452-426614874002', 'APPROVED', 'dimitar', NOW()),

       ('090e0013-e89b-12d3-a456-426682274099', 5347.43, 'BGN', '509e4567-e89b-12d3-a452-426614874001',
        '555e4567-e89b-12d3-a452-426614874000', 'APPROVED', 'ivan', NOW()),
       ('090e0014-e89b-12d3-a456-426682274099', 7087.80, 'EUR', '509e4567-e89b-12d3-a452-426614874001',
        '555e4567-e89b-12d3-a452-426614874001', 'APPROVED', 'ivan', NOW()),
       ('090e0015-e89b-12d3-a456-426682274099', 3990.50, 'USD', '510e4567-e89b-12d3-a452-426614874001',
        '555e4567-e89b-12d3-a452-426614874002', 'APPROVED', 'ivan', NOW()),

       ('090e0016-e89b-12d3-a456-426682274099', 5347.43, 'BGN', '611e4567-e89b-12d3-a452-426614874001',
        '666e4567-e89b-12d3-a452-426614874000', 'APPROVED', 'daniel', NOW()),
       ('090e0017-e89b-12d3-a456-426682274099', 7087.80, 'EUR', '611e4567-e89b-12d3-a452-426614874001',
        '666e4567-e89b-12d3-a452-426614874001', 'APPROVED', 'daniel', NOW()),
       ('090e0018-e89b-12d3-a456-426682274099', 3990.50, 'USD', '612e4567-e89b-12d3-a452-426614874001',
        '666e4567-e89b-12d3-a452-426614874002', 'APPROVED', 'daniel', NOW()),

       ('090e0019-e89b-12d3-a456-426682274099', 5347.43, 'BGN', '713e4567-e89b-12d3-a452-426614874001',
        '777e4567-e89b-12d3-a452-426614874000', 'APPROVED', 'chavdar', NOW()),
       ('090e0020-e89b-12d3-a456-426682274099', 7087.80, 'EUR', '713e4567-e89b-12d3-a452-426614874001',
        '777e4567-e89b-12d3-a452-426614874001', 'APPROVED', 'chavdar', NOW()),
       ('090e0021-e89b-12d3-a456-426682274099', 3990.50, 'USD', '714e4567-e89b-12d3-a452-426614874001',
        '777e4567-e89b-12d3-a452-426614874002', 'APPROVED', 'chavdar', NOW()),

       ('090e0022-e89b-12d3-a456-426682274099', 5347.43, 'BGN', '815e4567-e89b-12d3-a452-426614874001',
        '888e4567-e89b-12d3-a452-426614874000', 'APPROVED', 'lachezar', NOW()),
       ('090e0023-e89b-12d3-a456-426682274099', 7087.80, 'EUR', '815e4567-e89b-12d3-a452-426614874001',
        '888e4567-e89b-12d3-a452-426614874001', 'APPROVED', 'lachezar', NOW()),
       ('090e0024-e89b-12d3-a456-426682274099', 3990.50, 'USD', '816e4567-e89b-12d3-a452-426614874001',
        '888e4567-e89b-12d3-a452-426614874002', 'APPROVED', 'lachezar', NOW()),

       ('090e0025-e89b-12d3-a456-426682274099', 5347.43, 'BGN', '917e4567-e89b-12d3-a452-426614874001',
        '999e4567-e89b-12d3-a452-426614874000', 'APPROVED', 'nikolay', NOW()),
       ('090e0026-e89b-12d3-a456-426682274099', 7087.80, 'EUR', '917e4567-e89b-12d3-a452-426614874001',
        '999e4567-e89b-12d3-a452-426614874001', 'APPROVED', 'nikolay', NOW()),
       ('090e0027-e89b-12d3-a456-426682274099', 3990.50, 'USD', '918e4567-e89b-12d3-a452-426614874001',
        '999e4567-e89b-12d3-a452-426614874002', 'APPROVED', 'nikolay', NOW()),

       ('090e0028-e89b-12d3-a456-426682274099', 5347.43, 'BGN', '019e4567-e89b-12d3-a452-426614874001',
        '000e4567-e89b-12d3-a452-426614874000', 'APPROVED', 'viktor', NOW()),
       ('090e0029-e89b-12d3-a456-426682274099', 7087.80, 'EUR', '019e4567-e89b-12d3-a452-426614874001',
        '000e4567-e89b-12d3-a452-426614874001', 'APPROVED', 'viktor', NOW()),
       ('090e0030-e89b-12d3-a456-426682274099', 3990.50, 'USD', '020e4567-e89b-12d3-a452-426614874002',
        '000e4567-e89b-12d3-a452-426614874002', 'APPROVED', 'viktor', NOW())
;

INSERT INTO virtual_wallet.exchange_rate (id, from_currency, to_currency, rate)
VALUES ('123e1234-e01b-12d3-a456-426614174013', 'BGN', 'EUR', 0.5113),
       ('123e1234-e02b-12d3-a456-426614174016', 'BGN', 'USD', 0.5543),
       ('123e1234-e04b-12d3-a456-426614174021', 'BGN', 'GBP', 0.4346),
       ('123e1234-e05b-12d3-a456-426614174027', 'BGN', 'JPY', 72.35),
       ('123e1234-e06b-12d3-a456-426614174033', 'BGN', 'CNY', 3.988),
       ('123e1234-e07b-12d3-a456-426614174039', 'BGN', 'AUD', 0.8283),
       ('123e1234-e08b-12d3-a456-426614174045', 'BGN', 'CAD', 0.7465),
       ('123e1234-e09b-12d3-a456-426614174051', 'BGN', 'CHF', 0.510),

       ('123e1234-e10b-12d3-a456-426614174014', 'EUR', 'BGN', 1.9558),
       ('123e1234-e11b-12d3-a456-426614174011', 'EUR', 'USD', 1.0812),
       ('123e1234-e12b-12d3-a456-426614174053', 'EUR', 'GBP', 0.85),
       ('123e1234-e13b-12d3-a456-426614174054', 'EUR', 'JPY', 141.5),
       ('123e1234-e14b-12d3-a456-426614174055', 'EUR', 'CNY', 7.8),
       ('123e1234-e15b-12d3-a456-426614174056', 'EUR', 'AUD', 1.62),
       ('123e1234-e16b-12d3-a456-426614174057', 'EUR', 'CAD', 1.46),
       ('123e1234-e17b-12d3-a456-426614174058', 'EUR', 'CHF', 0.997),

       ('123e1234-e18b-12d3-a456-426614174015', 'USD', 'BGN', 1.8012),
       ('123e1234-e19b-12d3-a456-426614174012', 'USD', 'EUR', 0.9235),
       ('123e1234-e20b-12d3-a456-426614174017', 'USD', 'GBP', 0.79),
       ('123e1234-e21b-12d3-a456-426614174018', 'USD', 'JPY', 151.5),
       ('123e1234-e22b-12d3-a456-426614174019', 'USD', 'CNY', 7.2),
       ('123e1234-e23b-12d3-a456-426614174020', 'USD', 'AUD', 1.52),
       ('123e1234-e24b-12d3-a456-426614174021', 'USD', 'CAD', 1.36),
       ('123e1234-e25b-12d3-a456-426614174022', 'USD', 'CHF', 0.905),

       ('123e1234-e26b-12d3-a456-426614174061', 'GBP', 'BGN', 2.301),
       ('123e1234-e27b-12d3-a456-426614174059', 'GBP', 'EUR', 1.1765),
       ('123e1234-e28b-12d3-a456-426614174060', 'GBP', 'USD', 1.2658),
       ('123e1234-e29b-12d3-a456-426614174062', 'GBP', 'JPY', 166.35),
       ('123e1234-e30b-12d3-a456-426614174063', 'GBP', 'CNY', 9.1767),
       ('123e1234-e31b-12d3-a456-426614174064', 'GBP', 'AUD', 1.906),
       ('123e1234-e32b-12d3-a456-426614174065', 'GBP', 'CAD', 1.718),
       ('123e1234-e33b-12d3-a456-426614174066', 'GBP', 'CHF', 1.173),

       ('123e1234-e34b-12d3-a456-426614174028', 'JPY', 'BGN', 0.01382),
       ('123e1234-e35b-12d3-a456-426614174024', 'JPY', 'EUR', 0.00707),
       ('123e1234-e36b-12d3-a456-426614174026', 'JPY', 'USD', 0.0066),
       ('123e1234-e37b-12d3-a456-426614174067', 'JPY', 'GBP', 0.00601),
       ('123e1234-e38b-12d3-a456-426614174068', 'JPY', 'CNY', 0.0551),
       ('123e1234-e39b-12d3-a456-426614174069', 'JPY', 'AUD', 0.01145),
       ('123e1234-e40b-12d3-a456-426614174070', 'JPY', 'CAD', 0.01032),
       ('123e1234-e41b-12d3-a456-426614174071', 'JPY', 'CHF', 0.00705),

       ('123e1234-e42b-12d3-a456-426614174073', 'CNY', 'BGN', 0.2508),
       ('123e1234-e43b-12d3-a456-426614174074', 'CNY', 'EUR', 0.1282),
       ('123e1234-e44b-12d3-a456-426614174072', 'CNY', 'USD', 0.1389),
       ('123e1234-e45b-12d3-a456-426614174075', 'CNY', 'GBP', 0.1090),
       ('123e1234-e46b-12d3-a456-426614174076', 'CNY', 'JPY', 18.15),
       ('123e1234-e47b-12d3-a456-426614174077', 'CNY', 'AUD', 0.2077),
       ('123e1234-e48b-12d3-a456-426614174078', 'CNY', 'CAD', 0.1853),
       ('123e1234-e49b-12d3-a456-426614174079', 'CNY', 'CHF', 0.1278),

       ('123e1234-e50b-12d3-a456-426614174040', 'AUD', 'BGN', 1.207),
       ('123e1234-e51b-12d3-a456-426614174036', 'AUD', 'EUR', 0.6173),
       ('123e1234-e52b-12d3-a456-426614174038', 'AUD', 'USD', 0.6579),
       ('123e1234-e53b-12d3-a456-426614174080', 'AUD', 'GBP', 0.5247),
       ('123e1234-e54b-12d3-a456-426614174081', 'AUD', 'JPY', 87.35),
       ('123e1234-e55b-12d3-a456-426614174082', 'AUD', 'CNY', 4.816),
       ('123e1234-e56b-12d3-a456-426614174083', 'AUD', 'CAD', 0.901),
       ('123e1234-e57b-12d3-a456-426614174084', 'AUD', 'CHF', 0.6156),

       ('123e1234-e58b-12d3-a456-426614174086', 'CAD', 'BGN', 1.340),
       ('123e1234-e59b-12d3-a456-426614174087', 'CAD', 'EUR', 0.6849),
       ('123e1234-e60b-12d3-a456-426614174085', 'CAD', 'USD', 0.7353),
       ('123e1234-e61b-12d3-a456-426614174088', 'CAD', 'GBP', 0.5822),
       ('123e1234-e62b-12d3-a456-426614174089', 'CAD', 'JPY', 96.93),
       ('123e1234-e63b-12d3-a456-426614174090', 'CAD', 'CNY', 5.342),
       ('123e1234-e64b-12d3-a456-426614174091', 'CAD', 'AUD', 1.109),
       ('123e1234-e65b-12d3-a456-426614174092', 'CAD', 'CHF', 0.6828),

       ('123e1234-e66b-12d3-a456-426614174093', 'CHF', 'USD', 1.105),
       ('123e1234-e67b-12d3-a456-426614174094', 'CHF', 'BGN', 1.9608),
       ('123e1234-e68b-12d3-a456-426614174095', 'CHF', 'EUR', 1.003),
       ('123e1234-e69b-12d3-a456-426614174096', 'CHF', 'GBP', 0.8525),
       ('123e1234-e70b-12d3-a456-426614174097', 'CHF', 'JPY', 141.84),
       ('123e1234-e71b-12d3-a456-426614174098', 'CHF', 'CNY', 7.825),
       ('123e1234-e72b-12d3-a456-426614174099', 'CHF', 'AUD', 1.624),
       ('123e1234-e73b-12d3-a456-426614174100', 'CHF', 'CAD', 1.464)
;

INSERT INTO virtual_wallet.email_confirmation_token
    (id, user_id, expires_at, created_at)
VALUES ('001e4567-e77b-77d3-a456-776614174000', '001e4567-e89b-12d3-a456-426614174000',
        DATE_ADD(NOW(), INTERVAL 15 MINUTE), NOW()),
       ('002e4567-e77b-77d3-a456-776614174000', '002e4567-e89b-12d3-a456-426614174000',
        DATE_ADD(NOW(), INTERVAL 15 MINUTE), NOW()),
       ('003e4567-e77b-77d3-a456-776614174000', '003e4567-e89b-12d3-a456-426614174000',
        DATE_ADD(NOW(), INTERVAL 15 MINUTE), NOW()),
       ('004e4567-e77b-77d3-a456-776614174000', '004e4567-e89b-12d3-a456-426614174000',
        DATE_ADD(NOW(), INTERVAL 15 MINUTE), NOW()),
       ('005e4567-e77b-77d3-a456-776614174000', '005e4567-e89b-12d3-a456-426614174000',
        DATE_ADD(NOW(), INTERVAL 15 MINUTE), NOW()),
       ('006e4567-e77b-77d3-a456-776614174000', '006e4567-e89b-12d3-a456-426614174000',
        DATE_ADD(NOW(), INTERVAL 15 MINUTE), NOW()),
       ('007e4567-e77b-77d3-a456-776614174000', '007e4567-e89b-12d3-a456-426614174000',
        DATE_ADD(NOW(), INTERVAL 15 MINUTE), NOW()),
       ('008e4567-e77b-77d3-a456-776614174000', '008e4567-e89b-12d3-a456-426614174000',
        DATE_ADD(NOW(), INTERVAL 15 MINUTE), NOW()),
       ('009e4567-e77b-77d3-a456-776614174000', '009e4567-e89b-12d3-a456-426614174000',
        DATE_ADD(NOW(), INTERVAL 15 MINUTE), NOW()),
       ('010e4567-e77b-77d3-a456-776614174000', '010e4567-e89b-12d3-a456-426614174000',
        DATE_ADD(NOW(), INTERVAL 15 MINUTE), NOW())
;


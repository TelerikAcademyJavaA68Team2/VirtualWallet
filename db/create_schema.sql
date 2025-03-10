CREATE SCHEMA IF NOT EXISTS virtual_wallet;
USE virtual_wallet;

-- User Table
CREATE TABLE user
(
    id           UUID PRIMARY KEY,
    created_at   DATETIME(6),
    deleted_at   DATETIME(6),
    email        VARCHAR(255)                                     NOT NULL UNIQUE,
    first_name   VARCHAR(255)                                     NOT NULL,
    last_name    VARCHAR(255)                                     NOT NULL,
    password     VARCHAR(255)                                     NOT NULL,
    phone_number VARCHAR(255)                                     NOT NULL UNIQUE,
    photo        VARCHAR(255),
    role         ENUM ('ADMIN', 'USER')                           NOT NULL,
    status       ENUM ('ACTIVE', 'BLOCKED', 'DELETED', 'PENDING') NOT NULL,
    username     VARCHAR(255)                                     NOT NULL UNIQUE
);

-- Card Table
CREATE TABLE card
(
    id              UUID PRIMARY KEY,
    card_holder     VARCHAR(255)         NOT NULL,
    card_number     VARCHAR(255)         NOT NULL UNIQUE,
    created_at      DATETIME(6)          NOT NULL,
    cvv             VARCHAR(5)           NOT NULL,
    deleted_at      DATETIME(6),
    expiration_date VARCHAR(10)          NOT NULL,
    is_deleted      TINYINT(1) DEFAULT 0 NOT NULL,
    owner_id        UUID                 NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES user (id)
);

-- Email Confirmation Token
CREATE TABLE email_confirmation_token
(
    id           UUID PRIMARY KEY,
    confirmed_at DATETIME(6),
    created_at   DATETIME(6) NOT NULL,
    expires_at   DATETIME(6) NOT NULL,
    user_id      UUID        NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

-- Wallet Table
CREATE TABLE wallet
(
    id         UUID PRIMARY KEY,
    balance    DECIMAL(38, 2)             NOT NULL,
    created_at DATETIME(6)                NOT NULL,
    currency   ENUM ('BGN', 'EUR', 'USD') NOT NULL,
    deleted_at DATETIME(6),
    is_deleted TINYINT(1) DEFAULT 0       NOT NULL,
    owner_id   UUID                       NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES user (id)
);

-- Exchange Table
CREATE TABLE exchange
(
    id                 UUID PRIMARY KEY,
    amount             DECIMAL(38, 2)             NOT NULL,
    to_amount          DECIMAL(38, 2)             NOT NULL,
    exchange_rate      DECIMAL(38, 10)            NOT NULL,
    from_currency      ENUM ('BGN', 'EUR', 'USD') NOT NULL,
    to_currency        ENUM ('BGN', 'EUR', 'USD') NOT NULL,
    recipient_username VARCHAR(255)               NOT NULL,
    from_wallet_id     UUID                       NOT NULL,
    to_wallet_id       UUID                       NOT NULL,
    date               DATETIME(6)                NOT NULL,
    FOREIGN KEY (from_wallet_id) REFERENCES wallet (id),
    FOREIGN KEY (to_wallet_id) REFERENCES wallet (id)
);

-- Transaction Table
CREATE TABLE transaction
(
    id                  UUID PRIMARY KEY,
    amount              DECIMAL(38, 2)             NOT NULL,
    currency            ENUM ('BGN', 'EUR', 'USD') NOT NULL,
    sender_username     VARCHAR(255)               NOT NULL,
    recipient_username  VARCHAR(255)               NOT NULL,
    date                DATETIME(6)                NOT NULL,
    recipient_wallet_id UUID                       NOT NULL,
    sender_wallet_id    UUID                       NOT NULL,
    FOREIGN KEY (sender_wallet_id) REFERENCES wallet (id),
    FOREIGN KEY (recipient_wallet_id) REFERENCES wallet (id)
);

-- Transfer Table
CREATE TABLE transfer
(
    id        UUID PRIMARY KEY,
    amount    DECIMAL(38, 2)                NOT NULL,
    currency  ENUM ('BGN', 'EUR', 'USD')    NOT NULL,
    date      DATETIME(6)                   NOT NULL,
    status    ENUM ('APPROVED', 'DECLINED') NOT NULL,
    card_id   UUID                          NOT NULL,
    wallet_id UUID                          NOT NULL,
    FOREIGN KEY (card_id) REFERENCES card (id),
    FOREIGN KEY (wallet_id) REFERENCES wallet (id)
);

-- Exchange Rate Table
CREATE TABLE exchange_rate
(
    id            UUID PRIMARY KEY,
    rate          DECIMAL(38, 10)            NOT NULL,
    from_currency ENUM ('BGN', 'EUR', 'USD') NOT NULL,
    to_currency   ENUM ('BGN', 'EUR', 'USD') NOT NULL
);


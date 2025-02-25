/*CREATE TABLE users
(
    id           BINARY(16)   NOT NULL,
    username     VARCHAR(255) NULL,
    password     VARCHAR(255) NULL,
    email        VARCHAR(255) NULL,
    phone_number VARCHAR(255) NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_phonenumber UNIQUE (phone_number);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);*/
CREATE TABLE IF NOT EXISTS users (
                                     id BINARY(16) PRIMARY KEY,
                                     username VARCHAR(255) UNIQUE,
                                     password VARCHAR(255),
                                     email VARCHAR(255) UNIQUE,
                                     phone_number VARCHAR(255) UNIQUE
);
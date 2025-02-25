CREATE TABLE IF NOT EXISTS user (
                                    id UUID PRIMARY KEY,
                                    username VARCHAR(255) NOT NULL UNIQUE,
                                    password VARCHAR(255) NOT NULL,
                                    email VARCHAR(255) NOT NULL UNIQUE,
                                    phone_number VARCHAR(255) NOT NULL UNIQUE
);
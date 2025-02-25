/*-- Ensure no NULL values exist before applying the NOT NULL constraint
UPDATE user SET username = 'default_user' WHERE username IS NULL;
UPDATE user SET password = 'default_password' WHERE password IS NULL;
UPDATE user SET email = 'default@example.com' WHERE email IS NULL;
UPDATE user SET phone_number = '0000000000' WHERE phone_number IS NULL;

-- Alter table to add NOT NULL constraints
ALTER TABLE user
    MODIFY COLUMN id UUID NOT NULL PRIMARY KEY,
    MODIFY COLUMN username VARCHAR(255) NOT NULL UNIQUE,
    MODIFY COLUMN password VARCHAR(255) NOT NULL,
    MODIFY COLUMN email VARCHAR(255) NOT NULL UNIQUE,
    MODIFY COLUMN phone_number VARCHAR(255) NOT NULL UNIQUE;
*/
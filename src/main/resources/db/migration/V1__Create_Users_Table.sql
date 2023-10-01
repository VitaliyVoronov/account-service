CREATE TABLE users
(
    id                 SERIAL PRIMARY KEY,
    first_name         VARCHAR(50) NOT NULL,
    last_name          VARCHAR(50) NOT NULL,
    email              VARCHAR(100) NOT NULL UNIQUE,
    user_public_id     VARCHAR(255) NOT NULL UNIQUE,
    encrypted_password VARCHAR(255) NOT NULL UNIQUE
);
-- Create roles table
CREATE TABLE IF NOT EXISTS roles
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

-- Insert default roles
INSERT INTO roles (name)
VALUES ('ADMIN'),
       ('USER'),
       ('CUSTOMER');

-- Create users table
CREATE TABLE IF NOT EXISTS users
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(25)  NOT NULL,
    last_name     VARCHAR(25)  NOT NULL,
    user_name     VARCHAR(25)  NOT NULL UNIQUE,
    email         VARCHAR(35)  NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    address       VARCHAR(35),
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login    TIMESTAMP,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    is_not_locked BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Add indexes
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_username ON users (user_name);


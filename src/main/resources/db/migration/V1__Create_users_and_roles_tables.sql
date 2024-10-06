-- Create roles table
CREATE TABLE roles (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(30) NOT NULL UNIQUE
);

-- Insert default roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('CLIENT'),('VISITOR') ;

-- Create users table
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       first_name VARCHAR(45) NOT NULL,
                       last_name VARCHAR(45) NOT NULL,
                       user_name VARCHAR(45) NOT NULL UNIQUE,
                       email VARCHAR(45) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       address VARCHAR(45),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
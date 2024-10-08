-- V3__Create_products_table.sql

CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(50) NOT NULL,
                          unit VARCHAR(100),
                          price DECIMAL(10, 2) NOT NULL,
                          description TEXT,
                          image_data LONGBLOB,
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- V5__Add_categories_and_update_products.sql

-- Create categories table
CREATE TABLE IF NOT EXISTS categories
(
    id          BIGINT                                                          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255)                                                    NOT NULL,
    description TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Add category_id column to products table
ALTER TABLE products
    ADD COLUMN category_id BIGINT,
    ADD CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories (id)
            ON DELETE SET NULL;

-- Remove the existing index on product name
# DROP INDEX idx_product_name ON products;

-- Add index for categories
CREATE INDEX idx_category_name ON categories (name);
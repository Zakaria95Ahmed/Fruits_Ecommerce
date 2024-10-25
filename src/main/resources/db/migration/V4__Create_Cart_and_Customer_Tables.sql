-- V4__Create_Cart_and_Customer_Tables.sql
-- Create the customers table if it does not exist
CREATE TABLE IF NOT EXISTS customers
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT,
    billing_street    VARCHAR(50),
    billing_city      VARCHAR(25),
    billing_state     VARCHAR(12),
    billing_zip_code  VARCHAR(10),
    shipping_street   VARCHAR(50),
    shipping_city     VARCHAR(25),
    shipping_state    VARCHAR(12),
    shipping_zip_code VARCHAR(10),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create the carts table if it does not exist
CREATE TABLE IF NOT EXISTS carts
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id   BIGINT NOT NULL,
    shipping_cost DECIMAL(10, 2) DEFAULT 0.00,
    discount      DECIMAL(5, 2) DEFAULT 0.00,
    FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE
);

-- Create the cart_items table if it does not exist
CREATE TABLE IF NOT EXISTS cart_items
(
    cart_id    BIGINT,
    product_id BIGINT,
    quantity   INT NOT NULL,
    PRIMARY KEY (cart_id, product_id),
    FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

CREATE INDEX idx_customers_user_id ON customers (user_id);
CREATE INDEX idx_carts_customer_id ON carts (customer_id);
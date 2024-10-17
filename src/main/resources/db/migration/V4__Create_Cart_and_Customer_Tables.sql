-- إنشاء جدول الزبائن (customers) إذا لم يكن موجودًا
CREATE TABLE IF NOT EXISTS customers
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT,
    billing_street    VARCHAR(255),
    billing_city      VARCHAR(100),
    billing_state     VARCHAR(100),
    billing_zip_code  VARCHAR(20),
    shipping_street   VARCHAR(255),
    shipping_city     VARCHAR(100),
    shipping_state    VARCHAR(100),
    shipping_zip_code VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- إنشاء جدول السلة (carts) إذا لم يكن موجودًا

CREATE TABLE IF NOT EXISTS carts
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id   BIGINT NOT NULL,
    shipping_cost DECIMAL(10, 2) DEFAULT 0.00,
    discount      DECIMAL(5, 2) DEFAULT 0.00,
    FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE
);
-- إنشاء جدول عناصر السلة (cart_items) إذا لم يكن موجودًا
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
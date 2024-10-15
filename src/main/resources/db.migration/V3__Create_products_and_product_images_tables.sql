-- V3__Create_products_and_product_images_tables.sql

-- Create products table
CREATE TABLE IF NOT EXISTS products (
                                        id BIGINT NOT NULL AUTO_INCREMENT,
                                        name VARCHAR(255) NOT NULL,
                                        unit VARCHAR(50),
                                        price DECIMAL(19, 2) NOT NULL,
                                        description TEXT,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                                        PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create product_images table
CREATE TABLE IF NOT EXISTS product_images (
                                              id BIGINT NOT NULL AUTO_INCREMENT,
                                              image_url VARCHAR(255) NOT NULL,
                                              file_path VARCHAR(255) NOT NULL,
                                              product_id BIGINT  NULL,
                                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                              PRIMARY KEY (id),
                                              CONSTRAINT fk_product_images_product
                                                  FOREIGN KEY (product_id) REFERENCES products(id)
                                                      ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add indexes for performance
CREATE INDEX idx_product_name ON products(name);
CREATE INDEX idx_product_images_product_id ON product_images(product_id);


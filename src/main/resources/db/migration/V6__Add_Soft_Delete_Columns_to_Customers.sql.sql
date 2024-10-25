-- V5__Add_Soft_Delete_Columns_to_Customers.sql

-- Add is_active column with default value true
ALTER TABLE customers
    ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- Add deleted_at column that can be null
ALTER TABLE customers
    ADD COLUMN deleted_at TIMESTAMP NULL;

-- Add index for is_active to improve query performance when filtering
CREATE INDEX idx_customers_is_active ON customers (is_active);

-- Add index for deleted_at to improve query performance when filtering
CREATE INDEX idx_customers_deleted_at ON customers (deleted_at);
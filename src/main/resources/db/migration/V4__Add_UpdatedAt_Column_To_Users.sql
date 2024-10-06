-- Add updated_at column to users table
-- V4__Add_UpdatedAt_Column_To_Users.sql
ALTER TABLE users
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Update existing records to set updated_at to the same value as created_at
UPDATE users SET updated_at = created_at;
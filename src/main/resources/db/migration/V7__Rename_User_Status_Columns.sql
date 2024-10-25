-- V7__Rename_User_Status_Columns.sql

-- Rename is_active to active
ALTER TABLE users
    CHANGE COLUMN is_active active BOOLEAN NOT NULL DEFAULT TRUE;

-- Rename is_not_locked to locked and invert its default value
ALTER TABLE users
    CHANGE COLUMN is_not_locked locked BOOLEAN NOT NULL DEFAULT FALSE;

-- Update existing records to invert the locked status
UPDATE users
SET locked = NOT locked;

-- Create new index
CREATE INDEX idx_users_status ON users (active, locked);
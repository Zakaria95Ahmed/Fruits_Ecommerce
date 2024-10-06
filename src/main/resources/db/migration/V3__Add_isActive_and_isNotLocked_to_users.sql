-- Add isActive and isNotLocked columns to users table
ALTER TABLE users
    ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN is_not_locked BOOLEAN NOT NULL DEFAULT TRUE;

-- Update existing records
UPDATE users SET is_active = TRUE, is_not_locked = TRUE;



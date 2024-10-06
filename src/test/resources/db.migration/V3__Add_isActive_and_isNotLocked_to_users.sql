-- Add isActive and isNotLocked columns to users table
ALTER TABLE users
    ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE users
    ADD COLUMN is_not_locked BOOLEAN NOT NULL DEFAULT TRUE;
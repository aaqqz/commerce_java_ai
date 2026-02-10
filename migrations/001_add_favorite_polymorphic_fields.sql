-- Add polymorphic fields to favorite table
-- Migration: Extend favorite feature to support Brand and Merchant

-- Add new columns
ALTER TABLE favorite
    ADD COLUMN target_type VARCHAR(50),
    ADD COLUMN target_id BIGINT;

-- Migrate existing data: copy product_id to target_id and set target_type to 'PRODUCT'
UPDATE favorite
SET target_type = 'PRODUCT',
    target_id = product_id
WHERE product_id IS NOT NULL;

-- Set default value for target_type
ALTER TABLE favorite
    ALTER COLUMN target_type SET DEFAULT 'PRODUCT';

-- Add indexes for efficient querying
CREATE INDEX idx_favorite_target
    ON favorite(target_type, target_id, status);

CREATE INDEX idx_favorite_user_target
    ON favorite(user_id, target_type, status, updated_at);

-- Note: product_id column is kept for backward compatibility
-- It can be removed in a future migration after confirming all data is migrated

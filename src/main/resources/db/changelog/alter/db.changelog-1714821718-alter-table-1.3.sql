ALTER TABLE PUBLIC.vendor_whitelisting
DROP COLUMN logo;

ALTER TABLE PUBLIC.vendor_preference
ALTER COLUMN product_id DROP NOT NULL;
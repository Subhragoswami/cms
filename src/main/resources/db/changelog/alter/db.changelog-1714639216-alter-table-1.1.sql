ALTER TABLE PUBLIC.vendor_whitelisting
ALTER COLUMN database_on_premise TYPE VARCHAR;

ALTER TABLE PUBLIC.vendor_whitelisting
ALTER COLUMN font_family DROP NOT NULL;

ALTER TABLE PUBLIC.file_data
ADD COLUMN identifier VARCHAR;




CREATE INDEX charging_station_details_idx
ON charging_station_details (location_id);

CREATE INDEX vendor_customer_idx
ON vendor_customer (transaction_id);

CREATE INDEX vendor_account_details_idx
ON vendor_account_details (cin);

CREATE INDEX carbon_credit_details_idx
ON carbon_credit_details (transaction_id);
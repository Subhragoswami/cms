ALTER TABLE public.carbon_credit_details
    ALTER COLUMN vendor_code TYPE VARCHAR(50);

ALTER TABLE public.vendor_customer
    ALTER COLUMN vendor_code TYPE VARCHAR(50);

ALTER TABLE public.charging_sessions
    ALTER COLUMN vendor_code TYPE VARCHAR(50);

ALTER TABLE public.charging_station_details
    ALTER COLUMN vendor_code TYPE VARCHAR(50);

ALTER TABLE public.charger_details
    ALTER COLUMN vendor_code TYPE VARCHAR(50);

ALTER TABLE public.connector_details
    ALTER COLUMN vendor_code TYPE VARCHAR(50);

ALTER TABLE public.evse_details
    ALTER COLUMN vendor_code TYPE VARCHAR(50);

CREATE TABLE IF NOT EXISTS vendor_account_details (
    vendor_id UUID REFERENCES vendor(id) NOT NULL,
    cin VARCHAR UNIQUE,
    gst VARCHAR,
    pan VARCHAR,
    bank_account_name VARCHAR,
    bank_account_type VARCHAR NOT NULL,
    ifsc_code VARCHAR,
	CONSTRAINT VENDOR_ACCOUNT_DETAILS_PK PRIMARY KEY (ID)
) INHERITS (AUDITABLE_ENTITY);
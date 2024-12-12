CREATE TABLE IF NOT EXISTS public.carbon_credit_details(
    vendor_code VARCHAR(255),
    certificate_number VARCHAR(255),
    status VARCHAR(255),
    credit_score_points numeric,
    station_id BIGINT,
    transaction_id character varying(255),
    CONSTRAINT carbon_credit_details_pk PRIMARY KEY (id, vendor_code)
)INHERITS (public.auditable_entity);

CREATE TABLE IF NOT EXISTS vendor_customer (
    vendor_code VARCHAR,
    mobile VARCHAR(15) NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    user_type VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(255) UNIQUE,
   CONSTRAINT vendor_customer_id_pk PRIMARY KEY (id, vendor_code)
)INHERITS (AUDITABLE_ENTITY);


CREATE TABLE IF NOT EXISTS public.charging_sessions
(
    vendor_code VARCHAR,
    start_at TIMESTAMP,
    stop_at TIMESTAMP,
    cdr_token VARCHAR(255),
    location_name VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    address VARCHAR(255),
    vehicle_name VARCHAR(255),
    vehicle_number VARCHAR(255),
    used_energy numeric,
    currency VARCHAR(3),
    authorization_reference VARCHAR(255),
    connector_id VARCHAR(255),
    transaction_id VARCHAR(255),
    evse_id  VARCHAR(255),
    identity  VARCHAR(255),
    connector_number integer,
    vehicle_type  VARCHAR(255),
    location_id bigint,
    hash  VARCHAR,
    CONSTRAINT charging_session_id_pk PRIMARY KEY (id, vendor_code)
)INHERITS (public.auditable_entity);


CREATE TABLE IF NOT EXISTS public.charging_station_details(

    vendor_code VARCHAR,
    location_id BIGINT NOT NULL,
    name VARCHAR(255),
    parking_type VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    image VARCHAR(255),
    latitude double precision,
    longitude double precision,
    street1 VARCHAR(255),
    street2 VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255),
    status VARCHAR(50),
    pincode VARCHAR(255),
    hash VARCHAR,
    CONSTRAINT charging_station_details_pk PRIMARY KEY (id,vendor_code)
)INHERITS (public.auditable_entity);

CREATE TABLE IF NOT EXISTS public.charger_details
(
    vendor_code VARCHAR,
    identity VARCHAR(24),
    charger_name VARCHAR(255),
    charge_point_oem VARCHAR(25),
    charge_point_device VARCHAR(25),
    charge_point_connection_protocol VARCHAR(50),
    floor_level VARCHAR(50),
    qr_code VARCHAR(50),
    charger_id VARCHAR(50),
    station_type VARCHAR(50),
    charger_status VARCHAR(50),
    location_id bigint,
    CONSTRAINT charger_details_pk PRIMARY KEY (id, vendor_code)
)INHERITS (public.auditable_entity);

CREATE TABLE IF NOT EXISTS public.connector_details
(
    vendor_code VARCHAR,
    name VARCHAR(255),
    standard_name VARCHAR(255),
    format_name VARCHAR(255),
    power_type VARCHAR(255),
    cms_id VARCHAR(255),
    max_voltage integer,
    max_amperage integer,
    max_electric_power integer,
    terms_condition_url VARCHAR(255),
    connector_image VARCHAR(255),
    evse_id VARCHAR(255),
    connector_id VARCHAR(255),
    CONSTRAINT connector_details_id_pk PRIMARY KEY (id, vendor_code)
)INHERITS (public.auditable_entity);


CREATE TABLE IF NOT EXISTS public.evse_details
(
    vendor_code VARCHAR,
    physical_reference VARCHAR(50),
    max_output_power VARCHAR(50),
    status VARCHAR(50),
    connector_id integer,
    connector_status VARCHAR(50),
    availability VARCHAR(50),
    charger_id VARCHAR(50),
    evse_id VARCHAR(50),
    CONSTRAINT evse_details_id_pk PRIMARY KEY (id, vendor_code)
)INHERITS (public.auditable_entity);
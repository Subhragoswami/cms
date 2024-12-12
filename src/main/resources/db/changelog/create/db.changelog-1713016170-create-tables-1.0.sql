CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS PUBLIC.AUDITABLE_ENTITY
(
    id UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    date_created TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR,
    date_updated TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR,
    CONSTRAINT AUDITABLE_ENTITY_ID_PK PRIMARY KEY (ID)
)
TABLESPACE pg_default;

CREATE TABLE users (
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    user_name VARCHAR UNIQUE NOT NULL,
    email VARCHAR UNIQUE NOT NULL,
    password VARCHAR NOT NULL,
    status VARCHAR NOT NULL,
    phone_number VARCHAR UNIQUE,
    last_login TIMESTAMPTZ,
    login_attempts NUMERIC,
    last_password_updated TIMESTAMP,
    CONSTRAINT USERS_PK PRIMARY KEY (ID)
) INHERITS (AUDITABLE_ENTITY);

CREATE TABLE users_password_audit (
    user_id UUID REFERENCES users(id) NOT NULL,
    action VARCHAR NOT NULL,
    requested_by UUID,
    password_reset_token VARCHAR,
    password_reset_expiry TIMESTAMPTZ,
    status VARCHAR NOT NULL,
    CONSTRAINT USER_PASSWORD_AUDIT_ID_PK PRIMARY KEY (ID)
)INHERITS (AUDITABLE_ENTITY);

CREATE TABLE user_login_sessions (
    user_id UUID REFERENCES users(id) NOT NULL,
    login_time TIMESTAMPTZ NOT NULL,
    logout_time TIMESTAMPTZ,
    is_active BOOLEAN NOT NULL,
    CONSTRAINT USER_LOGIN_SESSION_ID_PK PRIMARY KEY (ID)
)INHERITS (AUDITABLE_ENTITY);

CREATE TABLE roles (
    name VARCHAR UNIQUE NOT NULL,
    description VARCHAR NOT NULL,
    CONSTRAINT ROLE_ID_PK PRIMARY KEY (ID)
)INHERITS (AUDITABLE_ENTITY);

CREATE TABLE user_roles (
    user_id UUID REFERENCES users(id),
    role_id UUID REFERENCES roles(id),
    CONSTRAINT USER_ROLE_ID_PK PRIMARY KEY (ID)
)INHERITS (AUDITABLE_ENTITY);

CREATE TABLE email_audit (
    user_id UUID REFERENCES users(id),
    vendor_id UUID,
    email_type VARCHAR NOT NULL,
    email_placeholder TEXT,
    CONSTRAINT EMAIL_AUDIT_ID_PK PRIMARY KEY (ID)
)INHERITS (AUDITABLE_ENTITY);


CREATE TABLE IF NOT EXISTS vendor (
    code VARCHAR UNIQUE NOT NULL,
    name VARCHAR NOT NULL,
    status VARCHAR NOT NULL,
    address1 TEXT,
    address2 TEXT,
    city VARCHAR,
    state VARCHAR,
    country VARCHAR,
    pincode VARCHAR,
	CONSTRAINT VENDOR_ID_PK PRIMARY KEY (ID)
) INHERITS (AUDITABLE_ENTITY);

CREATE TABLE IF NOT EXISTS vendor_contact (
    vendor_id UUID REFERENCES vendor(id),
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    email TEXT,
    phone_number VARCHAR,
	CONSTRAINT VENDOR_CONTACT_ID_PK PRIMARY KEY (ID)
) INHERITS (AUDITABLE_ENTITY);

CREATE TABLE IF NOT EXISTS vendor_whitelisting (
    vendor_id UUID REFERENCES vendor(id),
    font_family VARCHAR NOT NULL,
    primary_color TEXT,
    secondary_color VARCHAR,
    logo UUID,
    font_file UUID,
    domain TEXT,
    database_on_premise BOOLEAN,
    vendor_endpoint VARCHAR,
	CONSTRAINT VENDOR_WHITELISTING_ID_PK PRIMARY KEY (ID)
) INHERITS (AUDITABLE_ENTITY);

CREATE TABLE IF NOT EXISTS vendor_preference (
    vendor_id UUID REFERENCES vendor(id),
    product_id VARCHAR NOT NULL,
    total_max_admin TEXT,
    data_provider_protocol VARCHAR NOT NULL,
	CONSTRAINT VENDOR_PREFERENCE_ID_PK PRIMARY KEY (ID)
) INHERITS (AUDITABLE_ENTITY);

CREATE TABLE IF NOT EXISTS vendor_user(
    vendor_id UUID REFERENCES vendor(id),
    user_id UUID REFERENCES users(id)
)INHERITS (AUDITABLE_ENTITY);

CREATE TABLE IF NOT EXISTS file_data (
    id UUID PRIMARY KEY,
    type VARCHAR,
    content_type VARCHAR,
    data BYTEA
)INHERITS (AUDITABLE_ENTITY);
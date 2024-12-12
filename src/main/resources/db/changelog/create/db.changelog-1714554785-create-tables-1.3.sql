CREATE TABLE IF NOT EXISTS master (
    id UUID PRIMARY KEY,
    country_code VARCHAR,
    country_name VARCHAR,
    state_code VARCHAR,
    state_name VARCHAR
)INHERITS (AUDITABLE_ENTITY);
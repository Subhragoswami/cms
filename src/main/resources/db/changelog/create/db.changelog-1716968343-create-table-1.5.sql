CREATE TABLE IF NOT EXISTS carbon_credit_dashboard (
    vendor_code VARCHAR,
    total_carbon_credit_generated BIGINT,
    total_utilization numeric,
    CONSTRAINT carbon_credit_dashboard_pk PRIMARY KEY (id)
)INHERITS (AUDITABLE_ENTITY);

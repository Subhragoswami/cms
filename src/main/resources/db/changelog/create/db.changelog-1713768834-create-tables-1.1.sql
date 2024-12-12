CREATE TABLE IF NOT EXISTS modules (
    id UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    module_name VARCHAR UNIQUE NOT NULL,
    module_description VARCHAR NOT NULL,
    CONSTRAINT modules_pk PRIMARY KEY (id)
)INHERITS (AUDITABLE_ENTITY);

CREATE TABLE IF NOT EXISTS vendor_modules (
    id UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    vendor_id UUID REFERENCES vendor(id),
    module_id UUID REFERENCES modules(id),
    CONSTRAINT vendor_modules_pk PRIMARY KEY (id)
)INHERITS (AUDITABLE_ENTITY);
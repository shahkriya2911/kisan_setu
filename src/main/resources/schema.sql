-- =====================
-- MASTER TABLE UNIQUE CONSTRAINTS
-- =====================
CREATE UNIQUE INDEX IF NOT EXISTS ux_unit_master_unit_name
    ON unit_master (unit_name);

CREATE UNIQUE INDEX IF NOT EXISTS ux_packaging_master_packaging_type
    ON packaging_master (packaging_type);

CREATE UNIQUE INDEX IF NOT EXISTS ux_storage_master_storage_type
    ON storage_master (storage_type);

CREATE UNIQUE INDEX IF NOT EXISTS ux_state_master_name
    ON state_master (name);

CREATE UNIQUE INDEX IF NOT EXISTS ux_district_master_name_state_id
    ON district_master (name, state_id);

CREATE UNIQUE INDEX IF NOT EXISTS ux_crop_master_crop_name
    ON crop_master (crop_name);

-- =====================
-- TERMS AND POLICIES COLUMN TYPES
-- =====================
ALTER TABLE terms_and_policies
    ALTER COLUMN terms_of_service TYPE TEXT,
    ALTER COLUMN privacy_policy TYPE TEXT,
    ALTER COLUMN cookie_policy TYPE TEXT,
    ALTER COLUMN refund_policy TYPE TEXT,
    ALTER COLUMN community_guidelines TYPE TEXT;

CREATE TABLE IF NOT EXISTS report_reason_master (
    id SERIAL PRIMARY KEY,
    reason_name VARCHAR(100) UNIQUE NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS unique_fraud_type_name
ON fraud_type_master (type_name);

CREATE TABLE IF NOT EXISTS report_reason_master_Buyer (
    id SERIAL PRIMARY KEY,
    reason_name VARCHAR(100) UNIQUE NOT NULL
);

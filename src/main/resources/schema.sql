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

ALTER TABLE fraud_type_master
ADD CONSTRAINT unique_fraud_type_name UNIQUE (type_name);

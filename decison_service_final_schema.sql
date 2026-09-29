-- =========================================================================
-- RULE ENGINE FINAL DATABASE SCHEMA & SAMPLE DATA (SIMPLIFIED VERSION)
-- Version: 2.0 (Direct Fact-Action Mapping)
-- Description: Supports Guided Rule Builder with Domains (FactTypes), 
--              FactProperties, ActionProperties, and Allowed Values.
-- =========================================================================

-- 1. METADATA: DOMAINS
CREATE TABLE fact_type (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- 2. METADATA: PROPERTY DEFINITIONS (WHEN & THEN)
-- Link FactProperty directly to FactType
CREATE TABLE fact_property (
    id INT PRIMARY KEY,
    key VARCHAR(255) NOT NULL UNIQUE,
    label VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL, -- e.g., STRING, BOOLEAN, NUMBER
    fact_type_id INT,
    FOREIGN KEY (fact_type_id) REFERENCES fact_type(id)
);

CREATE TABLE fact_property_allowed_value (
    id INT PRIMARY KEY,
    fact_property_id INT,
    value VARCHAR(255) NOT NULL,
    label VARCHAR(255) NOT NULL,
    FOREIGN KEY (fact_property_id) REFERENCES fact_property(id)
);

-- Link ActionProperty directly to FactType (Simplified)
CREATE TABLE action_property (
    id INT PRIMARY KEY,
    key VARCHAR(255) NOT NULL UNIQUE,
    label VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    fact_type_id INT,
    FOREIGN KEY (fact_type_id) REFERENCES fact_type(id)
);

CREATE TABLE action_property_allowed_value (
    id INT PRIMARY KEY,
    action_property_id INT,
    value VARCHAR(255) NOT NULL,
    label VARCHAR(255) NOT NULL,
    FOREIGN KEY (action_property_id) REFERENCES action_property(id)
);

-- 3. DATA: RULE INSTANCES
CREATE TABLE rule (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    fact_type_id INT,
    enabled BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (fact_type_id) REFERENCES fact_type(id)
);

CREATE TABLE rule_condition (
    id BIGINT PRIMARY KEY,
    rule_id BIGINT,
    fact_property_id INT,
    operator VARCHAR(50) NOT NULL, -- e.g., ==, !=, >, <
    constant_value TEXT,
    FOREIGN KEY (rule_id) REFERENCES rule(id),
    FOREIGN KEY (fact_property_id) REFERENCES fact_property(id)
);

CREATE TABLE rule_action (
    id BIGINT PRIMARY KEY,
    rule_id BIGINT,
    action_property_id INT,
    output_value TEXT,
    FOREIGN KEY (rule_id) REFERENCES rule(id),
    FOREIGN KEY (action_property_id) REFERENCES action_property(id)
);

-- =========================================================================
-- SAMPLE DATA: ENFORCEMENT DOMAIN SETUP
-- =========================================================================

-- Step A: Setup the Context (Domain)
INSERT INTO fact_type (id, name, description) VALUES (1, 'ENFORCEMENT', 'Rules for security and access control.');

-- Step B: Setup Condition Definitions (The WHEN Options) - Linked to Domain 1
INSERT INTO fact_property (id, key, label, type, fact_type_id) VALUES (1, 'product_group', 'Product Group', 'STRING', 1);
INSERT INTO fact_property_allowed_value (id, fact_property_id, value, label) VALUES 
(1, 1, 'CONTRACT', 'Contract Parker'),
(2, 1, 'SHORT', 'Short-Term Parker');

-- Step C: Setup Action Definitions (The THEN Options) - Linked Directly to Domain 1
INSERT INTO action_property (id, key, label, type, fact_type_id) VALUES (1, 'OPEN_BARRIER', 'Open Barrier', 'BOOLEAN', 1);
INSERT INTO action_property_allowed_value (id, action_property_id, value, label) VALUES 
(1, 1, 'true', 'Yes (Open Gate)'),
(2, 1, 'false', 'No (Leave Closed)');

-- Step D: Create a Sample Rule (The Result)
INSERT INTO rule (id, name, description, fact_type_id) VALUES (101, 'Auto-Open for Contract', 'Open gate for contract parkers', 1);
INSERT INTO rule_condition (id, rule_id, fact_property_id, operator, constant_value) VALUES (1, 101, 1, '==', 'CONTRACT');
INSERT INTO rule_action (id, rule_id, action_property_id, output_value) VALUES (1, 101, 1, 'true');

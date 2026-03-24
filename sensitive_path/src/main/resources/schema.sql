CREATE TABLE IF NOT EXISTS fingerprint_library (
    record_id TEXT PRIMARY KEY,
    dataset TEXT NOT NULL,
    external_id TEXT,
    product_name TEXT NOT NULL,
    path TEXT NOT NULL,
    match_type TEXT NOT NULL,
    match_pattern TEXT NOT NULL,
    category TEXT,
    description TEXT,
    source_name TEXT NOT NULL,
    source_url TEXT,
    content_type TEXT,
    sample_body TEXT,
    hit_count INTEGER,
    updated_at TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_fingerprint_path
    ON fingerprint_library(path);

CREATE INDEX IF NOT EXISTS idx_fingerprint_dataset
    ON fingerprint_library(dataset);

CREATE INDEX IF NOT EXISTS idx_fingerprint_match_type
    ON fingerprint_library(match_type);

CREATE INDEX IF NOT EXISTS idx_fingerprint_product_name
    ON fingerprint_library(product_name);

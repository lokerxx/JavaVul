CREATE TABLE IF NOT EXISTS lab_challenge (
    id TEXT PRIMARY KEY,
    href TEXT NOT NULL,
    title TEXT NOT NULL,
    difficulty TEXT NOT NULL,
    topic TEXT NOT NULL,
    track_key TEXT NOT NULL,
    mode TEXT NOT NULL,
    source TEXT NOT NULL,
    summary TEXT NOT NULL,
    sort_order INTEGER NOT NULL,
    enabled INTEGER NOT NULL DEFAULT 1,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS lab_submission (
    submission_id INTEGER PRIMARY KEY AUTOINCREMENT,
    challenge_id TEXT NOT NULL,
    user_name TEXT,
    result TEXT NOT NULL,
    answer TEXT,
    notes TEXT,
    judge_type TEXT,
    judge_detail TEXT,
    created_at TEXT NOT NULL,
    FOREIGN KEY (challenge_id) REFERENCES lab_challenge(id)
);

CREATE TABLE IF NOT EXISTS lab_judge_rule (
    challenge_id TEXT PRIMARY KEY,
    judge_type TEXT NOT NULL,
    expected_answer TEXT,
    keyword_json TEXT,
    notes TEXT,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (challenge_id) REFERENCES lab_challenge(id)
);

CREATE INDEX IF NOT EXISTS idx_lab_challenge_track_key
    ON lab_challenge(track_key);

CREATE INDEX IF NOT EXISTS idx_lab_submission_challenge_id
    ON lab_submission(challenge_id);

CREATE INDEX IF NOT EXISTS idx_lab_judge_rule_judge_type
    ON lab_judge_rule(judge_type);

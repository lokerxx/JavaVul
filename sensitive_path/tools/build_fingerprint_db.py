import json
import sqlite3
from datetime import datetime, timezone
from pathlib import Path

import xlrd


ROOT = Path(__file__).resolve().parents[1]
FILE_DIR = ROOT / "src" / "main" / "resources" / "file"
IMPORT_DIR = ROOT / "src" / "main" / "resources" / "import"
DB_PATH = FILE_DIR / "fingerprint-library.db"
CMS_XLS = IMPORT_DIR / "cms.xls"
DAYU_JSON = IMPORT_DIR / "Dayu-Feature.json"
CMS_SOURCE_URL = "https://github.com/r0eXpeR/fingerprint/blob/main/CMS%E6%8C%87%E7%BA%B9/cms.xls"
DAYU_SOURCE_URL = "https://github.com/r0eXpeR/fingerprint/blob/main/Dayu/Feature.json"


def now_iso():
    return datetime.now(timezone.utc).isoformat()


def normalize_path(value):
    text = str(value or "").strip()
    if not text:
        return "/"
    return text if text.startswith("/") else "/" + text


def ensure_schema(conn):
    conn.executescript(
        """
        DROP TABLE IF EXISTS fingerprint_library;
        CREATE TABLE fingerprint_library (
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

        CREATE INDEX idx_fingerprint_path ON fingerprint_library(path);
        CREATE INDEX idx_fingerprint_dataset ON fingerprint_library(dataset);
        CREATE INDEX idx_fingerprint_match_type ON fingerprint_library(match_type);
        CREATE INDEX idx_fingerprint_product_name ON fingerprint_library(product_name);
        """
    )


def insert_record(conn, record):
    conn.execute(
        """
        INSERT INTO fingerprint_library (
            record_id, dataset, external_id, product_name, path, match_type, match_pattern,
            category, description, source_name, source_url, content_type, sample_body, hit_count, updated_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """,
        (
            record["record_id"],
            record["dataset"],
            record.get("external_id"),
            record["product_name"],
            record["path"],
            record["match_type"],
            record["match_pattern"],
            record.get("category"),
            record.get("description"),
            record["source_name"],
            record.get("source_url"),
            record.get("content_type"),
            record.get("sample_body"),
            record.get("hit_count"),
            record["updated_at"],
        ),
    )


def build_local_json_records():
    records = []
    for json_file in sorted(FILE_DIR.glob("*.json")):
        item = json.loads(json_file.read_text(encoding="utf-8"))
        external_id = json_file.stem
        path = normalize_path(item.get("path"))
        records.append(
            {
                "record_id": f"local-json:{external_id}",
                "dataset": "local-json",
                "external_id": external_id,
                "product_name": item.get("sourceName") or item.get("category") or external_id,
                "path": path,
                "match_type": "path_exact",
                "match_pattern": path,
                "category": item.get("category"),
                "description": item.get("description"),
                "source_name": item.get("sourceName") or "Local JSON fingerprint",
                "source_url": item.get("sourceUrl"),
                "content_type": item.get("contentType"),
                "sample_body": item.get("body"),
                "hit_count": None,
                "updated_at": now_iso(),
            }
        )
    return records


def build_cms_records():
    records = []
    workbook = xlrd.open_workbook(str(CMS_XLS))
    sheet = workbook.sheet_by_index(0)
    headers = [sheet.cell_value(0, idx) for idx in range(sheet.ncols)]
    index = {name: idx for idx, name in enumerate(headers)}
    for row_idx in range(1, sheet.nrows):
        row = [sheet.cell_value(row_idx, idx) for idx in range(sheet.ncols)]
        external_id = str(int(float(row[index["finger_id"]]))) if row[index["finger_id"]] != "" else ""
        product_name = str(row[index["cms_name"]]).strip()
        path = normalize_path(row[index["path"]])
        pattern = str(row[index["match_pattern"]]).strip()
        match_type = str(row[index["options"]]).strip().lower()
        hit_raw = row[index["hit"]]
        hit_count = int(float(hit_raw)) if hit_raw != "" else None
        if not all([external_id, product_name, path, pattern, match_type]):
            continue
        sample_body = None
        if match_type == "keyword":
            sample_body = f"<html><body>{pattern}</body></html>"
        elif match_type == "md5":
            sample_body = f"fingerprint_md5={pattern}"
        records.append(
            {
                "record_id": f"cms-xls:{external_id}",
                "dataset": "cms-xls",
                "external_id": external_id,
                "product_name": product_name,
                "path": path,
                "match_type": match_type,
                "match_pattern": pattern.lower(),
                "category": "cms",
                "description": "Imported from cms.xls fingerprint library",
                "source_name": "r0eXpeR fingerprint cms.xls",
                "source_url": CMS_SOURCE_URL,
                "content_type": None,
                "sample_body": sample_body,
                "hit_count": hit_count,
                "updated_at": now_iso(),
            }
        )
    return records


def dayu_match_type(type_id):
    mapping = {
        1: "md5",
        2: "keyword",
        3: "header_keyword",
    }
    return mapping.get(type_id, "keyword")


def build_dayu_records():
    records = []
    data = json.loads(DAYU_JSON.read_bytes().decode("gbk"))
    for item in data:
        external_id = str(item.get("id", "")).strip()
        product_name = str(item.get("program_name", "")).strip()
        path = normalize_path(item.get("url"))
        pattern = str(item.get("recognition_content", "")).strip()
        match_type = dayu_match_type(int(item.get("recognitionType_id", 0)))
        if not all([external_id, product_name, path, pattern]):
            continue
        sample_body = None
        if match_type == "keyword":
            sample_body = f"<html><body>{pattern}</body></html>"
        elif match_type == "header_keyword":
            sample_body = f"header_keyword={pattern}"
        elif match_type == "md5":
            sample_body = f"fingerprint_md5={pattern.lower()}"
        records.append(
            {
                "record_id": f"dayu-feature:{external_id}",
                "dataset": "dayu-feature",
                "external_id": external_id,
                "product_name": product_name,
                "path": path,
                "match_type": match_type,
                "match_pattern": pattern.lower() if match_type == "md5" else pattern,
                "category": item.get("manufacturerName"),
                "description": "Imported from Dayu Feature.json",
                "source_name": item.get("manufacturerName") or "Dayu Feature.json",
                "source_url": item.get("manufacturerUrl") or DAYU_SOURCE_URL,
                "content_type": None,
                "sample_body": sample_body,
                "hit_count": None,
                "updated_at": now_iso(),
            }
        )
    return records


def main():
    FILE_DIR.mkdir(parents=True, exist_ok=True)
    records = []
    records.extend(build_local_json_records())
    records.extend(build_cms_records())
    records.extend(build_dayu_records())

    conn = sqlite3.connect(DB_PATH)
    try:
        ensure_schema(conn)
        for record in records:
            insert_record(conn, record)
        conn.commit()
        summary = {
            "database": str(DB_PATH),
            "total": len(records),
            "local-json": len([r for r in records if r["dataset"] == "local-json"]),
            "cms-xls": len([r for r in records if r["dataset"] == "cms-xls"]),
            "dayu-feature": len([r for r in records if r["dataset"] == "dayu-feature"]),
        }
        print(json.dumps(summary, ensure_ascii=False, indent=2))
    finally:
        conn.close()


if __name__ == "__main__":
    main()

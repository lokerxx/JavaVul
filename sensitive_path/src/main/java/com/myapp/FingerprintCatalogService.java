package com.myapp;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class FingerprintCatalogService {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<FingerprintRecord> recordRowMapper = (rs, rowNum) -> {
        FingerprintRecord record = new FingerprintRecord();
        record.setRecordId(rs.getString("record_id"));
        record.setDataset(rs.getString("dataset"));
        record.setExternalId(rs.getString("external_id"));
        record.setProductName(rs.getString("product_name"));
        record.setPath(rs.getString("path"));
        record.setMatchType(rs.getString("match_type"));
        record.setMatchPattern(rs.getString("match_pattern"));
        record.setCategory(rs.getString("category"));
        record.setDescription(rs.getString("description"));
        record.setSourceName(rs.getString("source_name"));
        record.setSourceUrl(rs.getString("source_url"));
        record.setContentType(rs.getString("content_type"));
        record.setSampleBody(rs.getString("sample_body"));
        int hitCount = rs.getInt("hit_count");
        record.setHitCount(rs.wasNull() ? null : Integer.valueOf(hitCount));
        return record;
    };

    public FingerprintCatalogService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> reloadCatalog() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("message", "SQLite is already the primary source. Rebuild the DB with tools/build_fingerprint_db.py when source data changes.");
        result.put("database", "src/main/resources/file/fingerprint-library.db");
        result.put("overview", getOverview());
        return result;
    }

    public Map<String, Object> getOverview() {
        List<FingerprintRecord> items = listRecords(null, null, null);
        Map<String, Long> datasetCounts = items.stream()
            .collect(Collectors.groupingBy(FingerprintRecord::getDataset, LinkedHashMap::new, Collectors.counting()));
        Map<String, Long> matchTypeCounts = items.stream()
            .collect(Collectors.groupingBy(FingerprintRecord::getMatchType, LinkedHashMap::new, Collectors.counting()));

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("total", items.size());
        result.put("datasetCounts", datasetCounts);
        result.put("matchTypeCounts", matchTypeCounts);
        result.put("productCount", items.stream().map(FingerprintRecord::getProductName).distinct().count());
        result.put("pathCount", items.stream().map(FingerprintRecord::getPath).distinct().count());
        return result;
    }

    public List<FingerprintRecord> listRecords(String dataset, String matchType, String keyword) {
        StringBuilder sql = new StringBuilder(
            "SELECT record_id, dataset, external_id, product_name, path, match_type, match_pattern, category, description, source_name, source_url, content_type, sample_body, hit_count, updated_at " +
                "FROM fingerprint_library WHERE 1=1"
        );
        List<Object> args = new ArrayList<Object>();
        if (hasText(dataset)) {
            sql.append(" AND dataset = ?");
            args.add(dataset);
        }
        if (hasText(matchType)) {
            sql.append(" AND match_type = ?");
            args.add(matchType);
        }
        if (hasText(keyword)) {
            sql.append(" AND (LOWER(product_name) LIKE ? OR LOWER(path) LIKE ? OR LOWER(category) LIKE ? OR LOWER(description) LIKE ?)");
            String likeValue = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            args.add(likeValue);
            args.add(likeValue);
            args.add(likeValue);
            args.add(likeValue);
        }
        sql.append(" ORDER BY dataset ASC, product_name ASC, path ASC");
        return jdbcTemplate.query(sql.toString(), recordRowMapper, args.toArray());
    }

    public List<Map<String, Object>> listSummaries(String dataset, String matchType, String keyword) {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        for (FingerprintRecord record : listRecords(dataset, matchType, keyword)) {
            Map<String, Object> item = toMap(record);
            item.remove("sampleBody");
            items.add(item);
        }
        return items;
    }

    public FingerprintRecord getRecord(String recordId) {
        List<FingerprintRecord> records = jdbcTemplate.query(
            "SELECT record_id, dataset, external_id, product_name, path, match_type, match_pattern, category, description, source_name, source_url, content_type, sample_body, hit_count, updated_at " +
                "FROM fingerprint_library WHERE record_id = ?",
            recordRowMapper,
            recordId
        );
        return records.isEmpty() ? null : records.get(0);
    }

    public Map<String, Object> getRecordDetail(String recordId) {
        FingerprintRecord record = getRecord(recordId);
        return record == null ? null : toMap(record);
    }

    public Map<String, Object> getSample(String recordId) {
        FingerprintRecord record = getRecord(recordId);
        if (record == null) {
            return null;
        }
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("recordId", record.getRecordId());
        result.put("productName", record.getProductName());
        result.put("path", record.getPath());
        result.put("contentType", record.getContentType());
        result.put("sampleBody", record.getSampleBody());
        return result;
    }

    public List<FingerprintRecord> findByPath(String path) {
        return jdbcTemplate.query(
            "SELECT record_id, dataset, external_id, product_name, path, match_type, match_pattern, category, description, source_name, source_url, content_type, sample_body, hit_count, updated_at " +
                "FROM fingerprint_library WHERE path = ? ORDER BY dataset ASC, product_name ASC",
            recordRowMapper,
            normalizePath(path)
        );
    }

    public FingerprintRouteResponse buildRouteResponse(String path) {
        List<FingerprintRecord> records = findByPath(path);
        if (records.isEmpty()) {
            return null;
        }

        FingerprintRecord sampleSource = records.stream()
            .filter(item -> hasText(item.getSampleBody()) && "path_exact".equals(item.getMatchType()))
            .findFirst()
            .orElse(null);

        String contentType = detectContentType(path, records);
        byte[] body = buildRouteBody(path, contentType, sampleSource, records);

        Map<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("X-Fingerprint-Record-Count", String.valueOf(records.size()));
        headers.put("X-Fingerprint-Products", records.stream().map(FingerprintRecord::getProductName).distinct().collect(Collectors.joining(", ")));
        headers.put("X-Fingerprint-Datasets", records.stream().map(FingerprintRecord::getDataset).distinct().collect(Collectors.joining(", ")));

        List<String> md5Values = records.stream()
            .filter(item -> "md5".equals(item.getMatchType()))
            .map(FingerprintRecord::getMatchPattern)
            .collect(Collectors.toList());
        if (!md5Values.isEmpty()) {
            headers.put("X-Fingerprint-MD5", String.join(",", md5Values));
        }

        for (FingerprintRecord record : records) {
            if ("header_keyword".equals(record.getMatchType())) {
                String pattern = record.getMatchPattern();
                if (pattern != null && pattern.contains(":")) {
                    String[] parts = pattern.split(":", 2);
                    if (parts.length == 2 && hasText(parts[0]) && hasText(parts[1])) {
                        headers.put(parts[0].trim(), parts[1].trim());
                    }
                } else if (hasText(pattern)) {
                    headers.put("Set-Cookie", pattern + "=1");
                }
            }
        }

        return new FingerprintRouteResponse(contentType, body, headers);
    }

    public Map<String, Object> match(Map<String, Object> payload) {
        String path = normalizePath(stringValue(payload.get("path")));
        String responseBody = stringValue(payload.get("responseBody"));
        String responseHeaders = stringValue(payload.get("responseHeaders"));
        String combined = (responseHeaders + "\n" + responseBody).toLowerCase(Locale.ROOT);
        String providedMd5 = stringValue(payload.get("md5"));
        String responseMd5 = hasText(providedMd5) ? providedMd5.trim().toLowerCase(Locale.ROOT) : md5Hex(responseBody);

        List<FingerprintRecord> candidates = findByPath(path);
        List<Map<String, Object>> matches = new ArrayList<Map<String, Object>>();

        for (FingerprintRecord record : candidates) {
            String reason = null;
            int score = 0;
            String pattern = stringValue(record.getMatchPattern());

            if ("path_exact".equals(record.getMatchType())) {
                reason = "Path exactly matches a local fingerprint route.";
                score = 60;
            } else if ("md5".equals(record.getMatchType())) {
                String lowerPattern = pattern.toLowerCase(Locale.ROOT);
                if (hasText(responseMd5) && responseMd5.equals(lowerPattern)) {
                    reason = "Response MD5 matches the fingerprint rule.";
                    score = 100;
                } else if (combined.contains(lowerPattern)) {
                    reason = "Response content or headers expose the expected MD5 marker.";
                    score = 78;
                }
            } else if ("keyword".equals(record.getMatchType()) && combined.contains(pattern.toLowerCase(Locale.ROOT))) {
                reason = "Response body contains the keyword fingerprint pattern.";
                score = 85;
            } else if ("header_keyword".equals(record.getMatchType()) && combined.contains(pattern.toLowerCase(Locale.ROOT))) {
                reason = "Response headers or body contain the header-style fingerprint pattern.";
                score = 88;
            }

            if (reason != null) {
                Map<String, Object> item = toMap(record);
                item.put("matchReason", reason);
                item.put("score", score);
                matches.add(item);
            }
        }

        matches.sort((left, right) -> Integer.compare(intValue(right.get("score")), intValue(left.get("score"))));

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("path", path);
        result.put("computedMd5", responseMd5);
        result.put("candidateCount", candidates.size());
        result.put("matchCount", matches.size());
        result.put("matches", matches);
        return result;
    }

    private String buildSyntheticBody(List<FingerprintRecord> records) {
        List<String> lines = new ArrayList<String>();
        lines.add("Fingerprint route generated from SQLite library.");
        for (FingerprintRecord record : records) {
            lines.add("[" + record.getProductName() + "] " + record.getMatchType() + " => " + record.getMatchPattern());
        }
        return String.join("\n", lines);
    }

    private byte[] buildRouteBody(String path, String contentType, FingerprintRecord sampleSource, List<FingerprintRecord> records) {
        if (sampleSource != null && hasText(sampleSource.getSampleBody())) {
            return sampleSource.getSampleBody().getBytes(StandardCharsets.UTF_8);
        }
        if (isBinaryRoute(path, contentType)) {
            return binaryPlaceholder(path);
        }
        return buildSyntheticBody(records).getBytes(StandardCharsets.UTF_8);
    }

    private boolean isBinaryRoute(String path, String contentType) {
        String lowerPath = stringValue(path).toLowerCase(Locale.ROOT);
        String lowerType = stringValue(contentType).toLowerCase(Locale.ROOT);
        return lowerType.startsWith("image/")
            || lowerPath.endsWith(".ico")
            || lowerPath.endsWith(".bmp")
            || lowerPath.endsWith(".webp");
    }

    private byte[] binaryPlaceholder(String path) {
        String lowerPath = stringValue(path).toLowerCase(Locale.ROOT);
        if (lowerPath.endsWith(".png")) {
            return Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+nR8sAAAAASUVORK5CYII=");
        }
        if (lowerPath.endsWith(".gif")) {
            return Base64.getDecoder().decode("R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==");
        }
        if (lowerPath.endsWith(".ico")) {
            return new byte[] {0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0, 32, 0, 48, 0, 0, 0, 22, 0, 0, 0};
        }
        return Base64.getDecoder().decode("/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxAQEBUQEBAVFRUVFRUVFRUVFRUVFRUVFRUXFhUVFRUYHSggGBolGxUVITEhJSkrLi4uFx8zODMsNygtLisBCgoKDg0OGhAQGi0dHR0tLSstLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLf/AABEIAAEAAgMBIgACEQEDEQH/xAAXAAEBAQEAAAAAAAAAAAAAAAAAAQID/8QAFBABAAAAAAAAAAAAAAAAAAAAAP/aAAwDAQACEAMQAAAB6AAAAP/EABQQAQAAAAAAAAAAAAAAAAAAADD/2gAIAQEAAQUCf//EABQRAQAAAAAAAAAAAAAAAAAAADD/2gAIAQMBAT8BH//EABQRAQAAAAAAAAAAAAAAAAAAADD/2gAIAQIBAT8BH//Z");
    }

    private String detectContentType(String path, List<FingerprintRecord> records) {
        for (FingerprintRecord record : records) {
            if (hasText(record.getContentType())) {
                return record.getContentType();
            }
        }
        String lowerPath = stringValue(path).toLowerCase(Locale.ROOT);
        if (lowerPath.endsWith(".json")) {
            return MediaType.APPLICATION_JSON_VALUE;
        }
        if (lowerPath.endsWith(".html") || "/".equals(lowerPath)) {
            return "text/html;charset=UTF-8";
        }
        if (lowerPath.endsWith(".xml")) {
            return "application/xml;charset=UTF-8";
        }
        if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lowerPath.endsWith(".png")) {
            return "image/png";
        }
        if (lowerPath.endsWith(".gif")) {
            return "image/gif";
        }
        if (lowerPath.endsWith(".css")) {
            return "text/css;charset=UTF-8";
        }
        if (lowerPath.endsWith(".js")) {
            return "application/javascript;charset=UTF-8";
        }
        return "text/plain;charset=UTF-8";
    }

    private Map<String, Object> toMap(FingerprintRecord record) {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("recordId", record.getRecordId());
        item.put("dataset", record.getDataset());
        item.put("externalId", record.getExternalId());
        item.put("productName", record.getProductName());
        item.put("path", record.getPath());
        item.put("matchType", record.getMatchType());
        item.put("matchPattern", record.getMatchPattern());
        item.put("category", record.getCategory());
        item.put("description", record.getDescription());
        item.put("sourceName", record.getSourceName());
        item.put("sourceUrl", record.getSourceUrl());
        item.put("contentType", record.getContentType());
        item.put("sampleBody", record.getSampleBody());
        item.put("hitCount", record.getHitCount());
        return item;
    }

    private String md5Hex(String value) {
        if (!hasText(value)) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte item : hashed) {
                hex.append(String.format("%02x", item));
            }
            return hex.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to compute md5", ex);
        }
    }

    private String normalizePath(String value) {
        String path = stringValue(value).trim();
        if (path.isEmpty()) {
            return "/";
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static int intValue(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
}

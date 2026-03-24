package com.myapp.jshook;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class LabCatalogService {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Map<String, Object>> challengeMapper = (rs, rowNum) -> {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("id", rs.getString("id"));
        item.put("href", rs.getString("href"));
        item.put("title", rs.getString("title"));
        item.put("difficulty", rs.getString("difficulty"));
        item.put("topic", rs.getString("topic"));
        item.put("trackKey", rs.getString("track_key"));
        item.put("mode", rs.getString("mode"));
        item.put("source", rs.getString("source"));
        item.put("summary", rs.getString("summary"));
        item.put("sort", rs.getInt("sort_order"));
        item.put("enabled", rs.getInt("enabled") == 1);
        item.put("updatedAt", rs.getString("updated_at"));
        return item;
    };

    private final RowMapper<Map<String, Object>> submissionMapper = (rs, rowNum) -> {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("submissionId", rs.getLong("submission_id"));
        item.put("challengeId", rs.getString("challenge_id"));
        item.put("userName", rs.getString("user_name"));
        item.put("result", rs.getString("result"));
        item.put("answer", rs.getString("answer"));
        item.put("notes", rs.getString("notes"));
        item.put("judgeType", rs.getString("judge_type"));
        item.put("judgeDetail", rs.getString("judge_detail"));
        item.put("createdAt", rs.getString("created_at"));
        return item;
    };

    private final RowMapper<Map<String, Object>> judgeRuleMapper = (rs, rowNum) -> {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("challengeId", rs.getString("challenge_id"));
        item.put("judgeType", rs.getString("judge_type"));
        item.put("expectedAnswer", rs.getString("expected_answer"));
        item.put("keywords", parseKeywords(rs.getString("keyword_json")));
        item.put("notes", rs.getString("notes"));
        item.put("updatedAt", rs.getString("updated_at"));
        return item;
    };

    public LabCatalogService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initializeCatalog() {
        migrateTables();
        syncChallengesFromJson();
        syncJudgeRulesFromJson();
    }

    public int syncChallengesFromJson() {
        List<Map<String, Object>> challenges = loadChallengesFromJson();
        String now = Instant.now().toString();
        for (Map<String, Object> item : challenges) {
            jdbcTemplate.update(
                "INSERT INTO lab_challenge (id, href, title, difficulty, topic, track_key, mode, source, summary, sort_order, enabled, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT(id) DO UPDATE SET href=excluded.href, title=excluded.title, difficulty=excluded.difficulty, " +
                    "topic=excluded.topic, track_key=excluded.track_key, mode=excluded.mode, source=excluded.source, " +
                    "summary=excluded.summary, sort_order=excluded.sort_order, enabled=excluded.enabled, updated_at=excluded.updated_at",
                stringValue(item.get("id")),
                stringValue(item.get("href")),
                stringValue(item.get("title")),
                stringValue(item.get("difficulty")),
                stringValue(item.get("topic")),
                stringValue(item.get("trackKey")),
                stringValue(item.get("mode")),
                stringValue(item.get("source")),
                stringValue(item.get("summary")),
                intValue(item.get("sort")),
                1,
                now
            );
        }
        return challenges.size();
    }

    public int syncJudgeRulesFromJson() {
        List<Map<String, Object>> rules = loadJudgeRulesFromJson();
        String now = Instant.now().toString();
        for (Map<String, Object> item : rules) {
            jdbcTemplate.update(
                "INSERT INTO lab_judge_rule (challenge_id, judge_type, expected_answer, keyword_json, notes, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT(challenge_id) DO UPDATE SET judge_type=excluded.judge_type, expected_answer=excluded.expected_answer, " +
                    "keyword_json=excluded.keyword_json, notes=excluded.notes, updated_at=excluded.updated_at",
                stringValue(item.get("challengeId")),
                stringValue(item.get("judgeType")),
                emptyToNull(stringValue(item.get("expectedAnswer"))),
                keywordsToJson(item.get("keywords")),
                emptyToNull(stringValue(item.get("notes"))),
                now
            );
        }
        return rules.size();
    }

    public List<Map<String, Object>> listChallenges(String trackKey, String difficulty, String mode) {
        StringBuilder sql = new StringBuilder(
            "SELECT id, href, title, difficulty, topic, track_key, mode, source, summary, sort_order, enabled, updated_at " +
                "FROM lab_challenge WHERE enabled = 1"
        );
        List<Object> args = new ArrayList<Object>();
        if (hasText(trackKey)) {
            sql.append(" AND track_key = ?");
            args.add(trackKey);
        }
        if (hasText(difficulty)) {
            sql.append(" AND difficulty = ?");
            args.add(difficulty);
        }
        if (hasText(mode)) {
            sql.append(" AND mode = ?");
            args.add(mode);
        }
        sql.append(" ORDER BY sort_order ASC");
        return jdbcTemplate.query(sql.toString(), challengeMapper, args.toArray());
    }

    public Map<String, Object> getOverview() {
        List<Map<String, Object>> items = listChallenges(null, null, null);
        Map<String, Long> trackCounts = items.stream()
            .collect(Collectors.groupingBy(item -> stringValue(item.get("trackKey")), LinkedHashMap::new, Collectors.counting()));

        Integer submissionCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM lab_submission", Integer.class);
        Integer ruleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM lab_judge_rule", Integer.class);
        Integer autoJudgeCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM lab_judge_rule WHERE judge_type <> 'manual-review'",
            Integer.class
        );

        Map<String, Object> overview = new LinkedHashMap<String, Object>();
        overview.put("totalChallenges", items.size());
        overview.put("apiBackedChallenges", items.stream().filter(item -> "api-backed".equals(item.get("mode"))).count());
        overview.put("advancedChallenges", items.stream().filter(item -> "advanced".equals(item.get("difficulty"))).count());
        overview.put("trackCounts", trackCounts);
        overview.put("submissionCount", submissionCount == null ? 0 : submissionCount);
        overview.put("judgeRuleCount", ruleCount == null ? 0 : ruleCount);
        overview.put("autoJudgeRuleCount", autoJudgeCount == null ? 0 : autoJudgeCount);
        return overview;
    }

    public Map<String, Object> getChallenge(String challengeId) {
        List<Map<String, Object>> items = jdbcTemplate.query(
            "SELECT id, href, title, difficulty, topic, track_key, mode, source, summary, sort_order, enabled, updated_at " +
                "FROM lab_challenge WHERE id = ?",
            challengeMapper,
            challengeId
        );
        return items.isEmpty() ? Collections.<String, Object>emptyMap() : items.get(0);
    }

    public List<Map<String, Object>> listJudgeRules() {
        return jdbcTemplate.query(
            "SELECT challenge_id, judge_type, expected_answer, keyword_json, notes, updated_at " +
                "FROM lab_judge_rule ORDER BY challenge_id ASC",
            judgeRuleMapper
        );
    }

    public Map<String, Object> saveSubmission(Map<String, Object> body) {
        String challengeId = stringValue(body.get("challengeId"));
        ensureChallengeExists(challengeId);
        return insertSubmission(
            challengeId,
            stringValue(body.get("userName")),
            hasText(stringValue(body.get("result"))) ? stringValue(body.get("result")) : "pending",
            stringValue(body.get("answer")),
            stringValue(body.get("notes")),
            stringValue(body.get("judgeType")),
            stringValue(body.get("judgeDetail"))
        );
    }

    public Map<String, Object> judgeSubmission(Map<String, Object> body) {
        String challengeId = stringValue(body.get("challengeId"));
        String answer = stringValue(body.get("answer"));
        ensureChallengeExists(challengeId);
        if (!hasText(answer)) {
            throw new IllegalArgumentException("answer is required");
        }

        Map<String, Object> rule = getJudgeRule(challengeId);
        String judgeType = rule.isEmpty() ? "manual-review" : stringValue(rule.get("judgeType"));
        String result = "manual_review";
        String judgeDetail = "No automatic judge rule configured.";

        if ("exact".equals(judgeType)) {
            String expected = normalize(stringValue(rule.get("expectedAnswer")));
            if (normalize(answer).equals(expected)) {
                result = "passed";
                judgeDetail = "Matched exact expected answer.";
            } else {
                result = "failed";
                judgeDetail = "Answer did not match the exact expected value.";
            }
        } else if ("contains_all".equals(judgeType)) {
            List<String> keywords = safeKeywordList(rule.get("keywords"));
            List<String> missing = keywords.stream()
                .filter(keyword -> !normalize(answer).contains(normalize(keyword)))
                .collect(Collectors.toList());
            if (missing.isEmpty()) {
                result = "passed";
                judgeDetail = "Answer contains all required keywords.";
            } else {
                result = "failed";
                judgeDetail = "Missing keywords: " + String.join(", ", missing);
            }
        } else if ("non_empty".equals(judgeType)) {
            if (normalize(answer).length() >= 6) {
                result = "passed";
                judgeDetail = "Answer is non-empty and reached minimal length.";
            } else {
                result = "failed";
                judgeDetail = "Answer is too short.";
            }
        }

        Map<String, Object> stored = insertSubmission(
            challengeId,
            stringValue(body.get("userName")),
            result,
            answer,
            stringValue(body.get("notes")),
            judgeType,
            judgeDetail
        );
        stored.put("judgeType", judgeType);
        stored.put("judgeDetail", judgeDetail);
        return stored;
    }

    public List<Map<String, Object>> listSubmissions(String challengeId) {
        if (hasText(challengeId)) {
            return jdbcTemplate.query(
                "SELECT submission_id, challenge_id, user_name, result, answer, notes, judge_type, judge_detail, created_at " +
                    "FROM lab_submission WHERE challenge_id = ? ORDER BY submission_id DESC",
                submissionMapper,
                challengeId
            );
        }
        return jdbcTemplate.query(
            "SELECT submission_id, challenge_id, user_name, result, answer, notes, judge_type, judge_detail, created_at " +
                "FROM lab_submission ORDER BY submission_id DESC LIMIT 100",
            submissionMapper
        );
    }

    private void migrateTables() {
        addColumnIfMissing("lab_submission", "judge_type", "TEXT");
        addColumnIfMissing("lab_submission", "judge_detail", "TEXT");
    }

    private void addColumnIfMissing(String table, String column, String type) {
        try {
            jdbcTemplate.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
        } catch (Exception ignored) {
        }
    }

    private Map<String, Object> insertSubmission(
        String challengeId,
        String userName,
        String result,
        String answer,
        String notes,
        String judgeType,
        String judgeDetail
    ) {
        String createdAt = Instant.now().toString();
        jdbcTemplate.update(
            "INSERT INTO lab_submission (challenge_id, user_name, result, answer, notes, judge_type, judge_detail, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
            challengeId,
            emptyToNull(userName),
            result,
            emptyToNull(answer),
            emptyToNull(notes),
            emptyToNull(judgeType),
            emptyToNull(judgeDetail),
            createdAt
        );
        Long submissionId = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);

        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("submissionId", submissionId);
        response.put("challengeId", challengeId);
        response.put("result", result);
        response.put("createdAt", createdAt);
        return response;
    }

    private Map<String, Object> getJudgeRule(String challengeId) {
        List<Map<String, Object>> rules = jdbcTemplate.query(
            "SELECT challenge_id, judge_type, expected_answer, keyword_json, notes, updated_at FROM lab_judge_rule WHERE challenge_id = ?",
            judgeRuleMapper,
            challengeId
        );
        return rules.isEmpty() ? Collections.<String, Object>emptyMap() : rules.get(0);
    }

    private void ensureChallengeExists(String challengeId) {
        if (!hasText(challengeId)) {
            throw new IllegalArgumentException("challengeId is required");
        }
        Integer challengeCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM lab_challenge WHERE id = ?",
            Integer.class,
            challengeId
        );
        if (challengeCount == null || challengeCount == 0) {
            throw new IllegalArgumentException("challengeId not found");
        }
    }

    private List<Map<String, Object>> loadChallengesFromJson() {
        return readJsonList("static/labs/challenges.json");
    }

    private List<Map<String, Object>> loadJudgeRulesFromJson() {
        return readJsonList("judge-rules.json");
    }

    private List<Map<String, Object>> readJsonList(String classpathLocation) {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        try (InputStream inputStream = resource.getInputStream()) {
            return MAPPER.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load " + classpathLocation, ex);
        }
    }

    private List<String> parseKeywords(String keywordJson) {
        if (!hasText(keywordJson)) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.readValue(keywordJson, new TypeReference<List<String>>() {});
        } catch (IOException ex) {
            return Collections.emptyList();
        }
    }

    private String keywordsToJson(Object value) {
        try {
            if (value instanceof List) {
                return MAPPER.writeValueAsString(value);
            }
            return MAPPER.writeValueAsString(Collections.emptyList());
        } catch (IOException ex) {
            return "[]";
        }
    }

    private static List<String> safeKeywordList(Object value) {
        if (value instanceof List) {
            List<?> raw = (List<?>) value;
            List<String> result = new ArrayList<String>();
            for (Object item : raw) {
                result.add(String.valueOf(item));
            }
            return result;
        }
        return Collections.emptyList();
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

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String emptyToNull(String value) {
        return hasText(value) ? value : null;
    }

    private static String normalize(String value) {
        return stringValue(value).trim().toLowerCase();
    }
}

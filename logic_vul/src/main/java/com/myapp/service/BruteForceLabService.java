package com.myapp.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BruteForceLabService {
    private static final String CAPTCHA_ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";
    private static final String WEAK_PASSWORD_DICTIONARY = "123456, password, qwerty, admin123, guest, root123";
    private static final long CAPTCHA_TTL_MILLIS = 2 * 60 * 1000L;
    private static final int SAFE_MAX_FAILURES = 5;
    private static final long SAFE_LOCK_MILLIS = 60 * 1000L;

    private static final RowMapper<WeakUserRecord> USER_ROW_MAPPER = new RowMapper<WeakUserRecord>() {
        @Override
        public WeakUserRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new WeakUserRecord(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("display_name"),
                    rs.getString("role")
            );
        }
    };

    private final JdbcTemplate jdbcTemplate;
    private final SecureRandom random = new SecureRandom();
    private final Map<String, Integer> vulnerableAttemptCounter = new ConcurrentHashMap<String, Integer>();
    private final Map<String, Integer> safeAttemptCounter = new ConcurrentHashMap<String, Integer>();
    private final Map<String, CaptchaChallenge> captchaChallenges = new ConcurrentHashMap<String, CaptchaChallenge>();
    private final Map<String, LoginGuard> safeLoginGuards = new ConcurrentHashMap<String, LoginGuard>();

    public BruteForceLabService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> vulnerableHints() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("storage", "SQLite: ./logic_vul.db -> brute_force_users");
        data.put("usernames", usernames());
        data.put("dictionary", WEAK_PASSWORD_DICTIONARY);
        data.put("warning", "漏洞版没有图形验证码、失败锁定、冷却时间和统一错误提示");
        return data;
    }

    public Map<String, Object> vulnerableLogin(String username, String password) {
        String normalizedUsername = normalize(username);
        int attemptCount = increaseCounter(vulnerableAttemptCounter, normalizedUsername);
        WeakUserRecord user = findByUsername(normalizedUsername);
        if (user == null) {
            Map<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("success", false);
            data.put("mode", "vulnerable");
            data.put("attemptCount", attemptCount);
            data.put("message", "用户名不存在");
            data.put("hint", "接口会区分用户名不存在和密码错误，容易被做用户名枚举");
            return data;
        }
        if (!user.password.equals(password)) {
            Map<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("success", false);
            data.put("mode", "vulnerable");
            data.put("attemptCount", attemptCount);
            data.put("message", "密码错误");
            data.put("user", sanitize(user));
            data.put("hint", "没有验证码和锁定策略，可以继续尝试常见弱口令");
            return data;
        }

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("success", true);
        data.put("mode", "vulnerable");
        data.put("attemptCount", attemptCount);
        data.put("message", "登录成功：命中 SQLite 中的弱口令账号");
        data.put("user", sanitize(user));
        data.put("warning", "这是登录爆破演示页面，请勿在真实系统保留这种配置");
        return data;
    }

    public Map<String, Object> createCaptcha() {
        cleanupExpiredCaptchas();
        String token = UUID.randomUUID().toString().replace("-", "");
        String code = randomCode(5);
        captchaChallenges.put(token, new CaptchaChallenge(code, System.currentTimeMillis() + CAPTCHA_TTL_MILLIS));

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("captchaToken", token);
        data.put("expiresInSeconds", CAPTCHA_TTL_MILLIS / 1000);
        data.put("imageUrl", "/auth/bruteforce-safe/captcha/image?token=" + token);
        data.put("rule", "验证码由数字和字母组成，单次有效");
        return data;
    }

    public String captchaSvg(String token) {
        cleanupExpiredCaptchas();
        CaptchaChallenge challenge = captchaChallenges.get(token);
        if (challenge == null || challenge.expiresAt < System.currentTimeMillis()) {
            return renderSvg("EXPRD");
        }
        return renderSvg(challenge.code);
    }

    public Map<String, Object> safeLogin(String username, String password, String captchaToken, String captchaCode) {
        String normalizedUsername = normalize(username);
        cleanupExpiredCaptchas();

        LoginGuard guard = getGuard(normalizedUsername);
        long lockedSeconds = guard.lockedSeconds();
        if (lockedSeconds > 0) {
            Map<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("success", false);
            data.put("mode", "captcha-protected");
            data.put("message", "失败次数过多，账号已被临时锁定");
            data.put("lockedSeconds", lockedSeconds);
            data.put("captchaRequired", true);
            return data;
        }

        if (!verifyCaptcha(captchaToken, captchaCode)) {
            Map<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("success", false);
            data.put("mode", "captcha-protected");
            data.put("message", "用户名、密码或验证码错误");
            data.put("captchaRequired", true);
            return data;
        }

        int attemptCount = increaseCounter(safeAttemptCounter, normalizedUsername);
        WeakUserRecord user = findByUsername(normalizedUsername);
        if (user == null || !user.password.equals(password)) {
            int failures = guard.recordFailure();
            Map<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("success", false);
            data.put("mode", "captcha-protected");
            data.put("attemptCount", attemptCount);
            data.put("message", "用户名、密码或验证码错误");
            data.put("remainingBeforeLock", Math.max(0, SAFE_MAX_FAILURES - failures));
            if (guard.lockedSeconds() > 0) {
                data.put("lockedSeconds", guard.lockedSeconds());
            }
            data.put("hint", "安全版统一错误提示，并要求每次都提交图形验证码");
            return data;
        }

        guard.reset();
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("success", true);
        data.put("mode", "captcha-protected");
        data.put("attemptCount", attemptCount);
        data.put("message", "登录成功：已通过图形验证码与口令校验");
        data.put("user", sanitize(user));
        data.put("defense", "图形验证码 + 统一错误提示 + 临时锁定");
        return data;
    }

    private boolean verifyCaptcha(String token, String inputCode) {
        if (blank(token) || blank(inputCode)) {
            return false;
        }
        CaptchaChallenge challenge = captchaChallenges.remove(token);
        if (challenge == null || challenge.expiresAt < System.currentTimeMillis()) {
            return false;
        }
        return challenge.code.equalsIgnoreCase(inputCode.trim());
    }

    private List<Map<String, Object>> usernames() {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        List<WeakUserRecord> rows = jdbcTemplate.query(
                "SELECT id, username, password, display_name, role FROM brute_force_users WHERE enabled = 1 ORDER BY id",
                USER_ROW_MAPPER
        );
        for (WeakUserRecord row : rows) {
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("username", row.username);
            item.put("displayName", row.displayName);
            item.put("role", row.role);
            items.add(item);
        }
        return items;
    }

    private WeakUserRecord findByUsername(String username) {
        if (blank(username)) {
            return null;
        }
        List<WeakUserRecord> rows = jdbcTemplate.query(
                "SELECT id, username, password, display_name, role FROM brute_force_users WHERE enabled = 1 AND username = ?",
                USER_ROW_MAPPER,
                username
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    private Map<String, Object> sanitize(WeakUserRecord user) {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("id", user.id);
        data.put("username", user.username);
        data.put("displayName", user.displayName);
        data.put("role", user.role);
        return data;
    }

    private String renderSvg(String code) {
        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns='http://www.w3.org/2000/svg' width='180' height='64' viewBox='0 0 180 64'>");
        svg.append("<rect width='100%' height='100%' rx='12' ry='12' fill='#f8fafc'/>");
        for (int i = 0; i < 6; i++) {
            svg.append("<line x1='").append(random.nextInt(180)).append("' y1='").append(random.nextInt(64))
                    .append("' x2='").append(random.nextInt(180)).append("' y2='").append(random.nextInt(64))
                    .append("' stroke='").append(randomColor()).append("' stroke-width='1.4' opacity='0.45'/>");
        }
        for (int i = 0; i < code.length(); i++) {
            int x = 20 + i * 30;
            int y = 40 + random.nextInt(12);
            int rotate = random.nextInt(31) - 15;
            svg.append("<text x='").append(x).append("' y='").append(y)
                    .append("' font-size='30' font-family='monospace' fill='").append(randomColor())
                    .append("' transform='rotate(").append(rotate).append(" ").append(x).append(" ").append(y).append(")'>")
                    .append(code.charAt(i))
                    .append("</text>");
        }
        for (int i = 0; i < 18; i++) {
            svg.append("<circle cx='").append(random.nextInt(180)).append("' cy='").append(random.nextInt(64))
                    .append("' r='").append(1 + random.nextInt(3))
                    .append("' fill='").append(randomColor()).append("' opacity='0.35'/>");
        }
        svg.append("</svg>");
        return svg.toString();
    }

    private String randomCode(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(CAPTCHA_ALPHABET.charAt(random.nextInt(CAPTCHA_ALPHABET.length())));
        }
        return builder.toString();
    }

    private String randomColor() {
        return String.format("#%02x%02x%02x", 40 + random.nextInt(140), 40 + random.nextInt(140), 40 + random.nextInt(140));
    }

    private LoginGuard getGuard(String username) {
        return safeLoginGuards.computeIfAbsent(blank(username) ? "anonymous" : username, ignored -> new LoginGuard());
    }

    private int increaseCounter(Map<String, Integer> counterMap, String key) {
        String normalizedKey = blank(key) ? "anonymous" : key;
        int next = counterMap.containsKey(normalizedKey) ? counterMap.get(normalizedKey) + 1 : 1;
        counterMap.put(normalizedKey, next);
        return next;
    }

    private void cleanupExpiredCaptchas() {
        long now = System.currentTimeMillis();
        List<String> expiredTokens = new ArrayList<String>();
        for (Map.Entry<String, CaptchaChallenge> entry : captchaChallenges.entrySet()) {
            if (entry.getValue().expiresAt < now) {
                expiredTokens.add(entry.getKey());
            }
        }
        for (String expiredToken : expiredTokens) {
            captchaChallenges.remove(expiredToken);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static final class WeakUserRecord {
        private final Long id;
        private final String username;
        private final String password;
        private final String displayName;
        private final String role;

        private WeakUserRecord(Long id, String username, String password, String displayName, String role) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.displayName = displayName;
            this.role = role;
        }
    }

    private static final class CaptchaChallenge {
        private final String code;
        private final long expiresAt;

        private CaptchaChallenge(String code, long expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    private static final class LoginGuard {
        private int failures;
        private long lockedUntil;

        private int recordFailure() {
            long now = System.currentTimeMillis();
            if (lockedUntil > now) {
                return failures;
            }
            if (lockedUntil != 0L && lockedUntil <= now) {
                failures = 0;
                lockedUntil = 0L;
            }
            failures++;
            if (failures >= SAFE_MAX_FAILURES) {
                lockedUntil = now + SAFE_LOCK_MILLIS;
            }
            return failures;
        }

        private long lockedSeconds() {
            long remain = lockedUntil - System.currentTimeMillis();
            return remain <= 0 ? 0 : (remain + 999) / 1000;
        }

        private void reset() {
            failures = 0;
            lockedUntil = 0L;
        }
    }
}

package com.myapp.support;

import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class RememberMeOracleService {

    public static final String DEMO_KEY_BASE64 = "UGFkZGluZ09yYWNsZUtleQ==";

    private final byte[] demoKey = Base64.getDecoder().decode(DEMO_KEY_BASE64);
    private final SecureRandom secureRandom = new SecureRandom();

    public void applyDemoKey(CookieRememberMeManager rememberMeManager) {
        rememberMeManager.setCipherKey(demoKey);
    }

    public String getDemoKeyBase64() {
        return DEMO_KEY_BASE64;
    }

    public String generateValidCookie() {
        SimplePrincipalCollection principals = new SimplePrincipalCollection("admin", "demoRealm");
        return encrypt(serialize(principals));
    }

    public String generatePaddingErrorCookie() {
        byte[] raw = Base64.getDecoder().decode(generateValidCookie());
        raw[raw.length - 1] = (byte) (raw[raw.length - 1] ^ 0x01);
        return Base64.getEncoder().encodeToString(raw);
    }

    public String generateContentErrorCookie() {
        return encrypt("not-a-java-serialized-object".getBytes(StandardCharsets.UTF_8));
    }

    public OracleResult probe(String cookieValue) {
        if (cookieValue == null || cookieValue.trim().isEmpty()) {
            return OracleResult.invalid("cookie 不能为空。");
        }

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(cookieValue.trim());
        } catch (IllegalArgumentException ex) {
            return OracleResult.invalid("cookie 不是合法的 Base64。");
        }

        if (decoded.length <= 16 || decoded.length % 16 != 0) {
            return OracleResult.padding("cookie 长度不符合 AES-CBC 分组要求。");
        }

        byte[] plaintext;
        try {
            plaintext = decrypt(decoded);
        } catch (BadPaddingException ex) {
            return OracleResult.padding("解密阶段出现 padding 错误。");
        } catch (IllegalBlockSizeException ex) {
            return OracleResult.padding("解密阶段出现分组长度错误。");
        } catch (GeneralSecurityException ex) {
            return OracleResult.invalid("解密失败：" + ex.getClass().getSimpleName());
        }

        try {
            Object object = deserialize(plaintext);
            String principalInfo = object instanceof SimplePrincipalCollection
                    ? ((SimplePrincipalCollection) object).getPrimaryPrincipal().toString()
                    : object.getClass().getName();
            return OracleResult.success("解密与反序列化都成功。", principalInfo);
        } catch (Exception ex) {
            return OracleResult.deserialize("padding 正确，但反序列化失败：" + ex.getClass().getSimpleName());
        }
    }

    public Map<String, Object> sample(String type) {
        String normalized = type == null ? "valid" : type;
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("type", normalized);

        if ("padding".equals(normalized)) {
            result.put("name", "Padding 错误样本");
            result.put("cookie", generatePaddingErrorCookie());
            result.put("description", "基于正常 Cookie 翻转最后一个字节，强制触发 padding 错误。");
            return result;
        }
        if ("content".equals(normalized)) {
            result.put("name", "内容错误样本");
            result.put("cookie", generateContentErrorCookie());
            result.put("description", "使用相同 key 正常加密，但明文并不是合法 Java 序列化对象。");
            return result;
        }

        result.put("name", "正常样本");
        result.put("cookie", generateValidCookie());
        result.put("description", "由同一 AES-CBC key 生成的合法 rememberMe 样本。");
        return result;
    }

    public Map<String, Object> mutate(String cookieValue, int index, int xorValue) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        if (cookieValue == null || cookieValue.trim().isEmpty()) {
            result.put("ok", false);
            result.put("message", "cookie 不能为空。");
            return result;
        }

        byte[] raw;
        try {
            raw = Base64.getDecoder().decode(cookieValue.trim());
        } catch (IllegalArgumentException ex) {
            result.put("ok", false);
            result.put("message", "cookie 不是合法的 Base64。");
            return result;
        }

        if (index < 0 || index >= raw.length) {
            result.put("ok", false);
            result.put("message", "字节下标越界，可用范围：0-" + (raw.length - 1));
            return result;
        }

        int normalizedXor = xorValue & 0xFF;
        int before = raw[index] & 0xFF;
        raw[index] = (byte) (raw[index] ^ normalizedXor);
        int after = raw[index] & 0xFF;
        String mutated = Base64.getEncoder().encodeToString(raw);

        result.put("ok", true);
        result.put("index", index);
        result.put("xor", normalizedXor);
        result.put("before", before);
        result.put("after", after);
        result.put("cookie", mutated);
        return result;
    }

    public Map<String, Object> sweep(String cookieValue, int start, int count, int xorValue) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        if (cookieValue == null || cookieValue.trim().isEmpty()) {
            result.put("ok", false);
            result.put("message", "cookie 不能为空。");
            return result;
        }

        byte[] raw;
        try {
            raw = Base64.getDecoder().decode(cookieValue.trim());
        } catch (IllegalArgumentException ex) {
            result.put("ok", false);
            result.put("message", "cookie 不是合法的 Base64。");
            return result;
        }

        int safeStart = Math.max(0, start);
        int safeCount = Math.max(1, Math.min(count, 64));
        int endExclusive = Math.min(raw.length, safeStart + safeCount);
        if (safeStart >= raw.length) {
            result.put("ok", false);
            result.put("message", "起始下标越界。");
            return result;
        }

        int normalizedXor = xorValue & 0xFF;
        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        for (int i = safeStart; i < endExclusive; i++) {
            byte[] copy = raw.clone();
            copy[i] = (byte) (copy[i] ^ normalizedXor);
            String mutated = Base64.getEncoder().encodeToString(copy);
            OracleResult probeResult = probe(mutated);

            Map<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("index", i);
            row.put("status", probeResult.getStatus());
            row.put("stage", probeResult.getStage());
            row.put("message", probeResult.getMessage());
            records.add(row);
        }

        result.put("ok", true);
        result.put("start", safeStart);
        result.put("count", records.size());
        result.put("xor", normalizedXor);
        result.put("records", records);
        return result;
    }

    private String encrypt(byte[] plaintext) {
        try {
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(demoKey, "AES"), new IvParameterSpec(iv));
            byte[] ciphertext = cipher.doFinal(plaintext);

            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("rememberMe sample generation failed", ex);
        }
    }

    private byte[] decrypt(byte[] cookieBytes) throws GeneralSecurityException {
        byte[] iv = new byte[16];
        byte[] ciphertext = new byte[cookieBytes.length - 16];
        System.arraycopy(cookieBytes, 0, iv, 0, 16);
        System.arraycopy(cookieBytes, 16, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(demoKey, "AES"), new IvParameterSpec(iv));
        return cipher.doFinal(ciphertext);
    }

    private byte[] serialize(Object object) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("serialization failed", ex);
        }
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
        return objectInputStream.readObject();
    }

    public static class OracleResult {
        private final String stage;
        private final int status;
        private final String message;
        private final String principal;

        private OracleResult(String stage, int status, String message, String principal) {
            this.stage = stage;
            this.status = status;
            this.message = message;
            this.principal = principal;
        }

        public static OracleResult success(String message, String principal) {
            return new OracleResult("success", 200, message, principal);
        }

        public static OracleResult deserialize(String message) {
            return new OracleResult("deserialization_error", 400, message, null);
        }

        public static OracleResult padding(String message) {
            return new OracleResult("padding_error", 500, message, null);
        }

        public static OracleResult invalid(String message) {
            return new OracleResult("invalid_input", 422, message, null);
        }

        public String getStage() {
            return stage;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getPrincipal() {
            return principal;
        }
    }
}

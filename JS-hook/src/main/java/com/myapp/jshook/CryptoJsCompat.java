package com.myapp.jshook;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoJsCompat {
    private static final byte[] SALTED = "Salted__".getBytes(StandardCharsets.US_ASCII);
    private static final SecureRandom RANDOM = new SecureRandom();

    private CryptoJsCompat() {
    }

    public static String encrypt(String plainText, String passphrase) {
        try {
            byte[] salt = new byte[8];
            RANDOM.nextBytes(salt);
            KeyAndIv keyAndIv = evpBytesToKey(passphrase.getBytes(StandardCharsets.UTF_8), salt, 32, 16);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyAndIv.key, "AES"), new IvParameterSpec(keyAndIv.iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] output = new byte[SALTED.length + salt.length + cipherText.length];
            System.arraycopy(SALTED, 0, output, 0, SALTED.length);
            System.arraycopy(salt, 0, output, SALTED.length, salt.length);
            System.arraycopy(cipherText, 0, output, SALTED.length + salt.length, cipherText.length);
            return Base64.getEncoder().encodeToString(output);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Failed to encrypt CryptoJS payload", ex);
        }
    }

    public static String decrypt(String encryptedText, String passphrase) {
        try {
            byte[] allBytes = Base64.getDecoder().decode(encryptedText);
            byte[] salt = null;
            byte[] cipherText = allBytes;

            if (allBytes.length > 16 && startsWithSalted(allBytes)) {
                salt = Arrays.copyOfRange(allBytes, 8, 16);
                cipherText = Arrays.copyOfRange(allBytes, 16, allBytes.length);
            }

            KeyAndIv keyAndIv = evpBytesToKey(
                passphrase.getBytes(StandardCharsets.UTF_8),
                salt,
                32,
                16
            );
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyAndIv.key, "AES"), new IvParameterSpec(keyAndIv.iv));
            byte[] plain = cipher.doFinal(cipherText);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to decrypt CryptoJS payload", ex);
        }
    }

    public static byte[] sha256Bytes(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static String hmacSha256Hex(String value, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return toHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static String md5Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return toHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }

    public static byte[] fromHex(String hex) {
        String normalized = hex == null ? "" : hex.trim();
        if (normalized.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string");
        }
        byte[] bytes = new byte[normalized.length() / 2];
        for (int i = 0; i < normalized.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(normalized.substring(i, i + 2), 16);
        }
        return bytes;
    }

    private static boolean startsWithSalted(byte[] bytes) {
        for (int i = 0; i < SALTED.length; i++) {
            if (bytes[i] != SALTED[i]) {
                return false;
            }
        }
        return true;
    }

    private static KeyAndIv evpBytesToKey(byte[] password, byte[] salt, int keyLength, int ivLength)
        throws GeneralSecurityException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] derived = new byte[0];
        byte[] block = new byte[0];

        while (derived.length < keyLength + ivLength) {
            md5.reset();
            md5.update(block);
            md5.update(password);
            if (salt != null) {
                md5.update(salt, 0, 8);
            }
            block = md5.digest();
            byte[] next = new byte[derived.length + block.length];
            System.arraycopy(derived, 0, next, 0, derived.length);
            System.arraycopy(block, 0, next, derived.length, block.length);
            derived = next;
        }

        return new KeyAndIv(
            Arrays.copyOfRange(derived, 0, keyLength),
            Arrays.copyOfRange(derived, keyLength, keyLength + ivLength)
        );
    }

    private static final class KeyAndIv {
        private final byte[] key;
        private final byte[] iv;

        private KeyAndIv(byte[] key, byte[] iv) {
            this.key = key;
            this.iv = iv;
        }
    }
}

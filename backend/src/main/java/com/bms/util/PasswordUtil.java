package com.bms.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Very small helper that turns a plain-text password into a SHA-256 hash
 * before it is stored, and checks a login attempt by hashing the entered
 * password and comparing it to the stored hash.
 *
 * Note for learning purposes: in a real production system you would use
 * BCrypt (via Spring Security) instead of raw SHA-256, because BCrypt adds
 * a random "salt" and is deliberately slow, which makes it far more
 * resistant to brute-force attacks. Plain SHA-256 is used here only to
 * keep the project dependency-free and easy to follow for a beginner.
 */
public class PasswordUtil {

    public static String hash(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawPassword.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public static boolean matches(String rawPassword, String storedHash) {
        return hash(rawPassword).equals(storedHash);
    }
}

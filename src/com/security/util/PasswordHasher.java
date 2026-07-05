package com.security.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PasswordHasher - utility class for secure password hashing using SHA-256 + salt.
 * Demonstrates OOP: Utility class with static methods, Security best practices
 */
public class PasswordHasher {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String SEPARATOR = ":";

    private PasswordHasher() {
    }

    public static String hash(String plainPassword) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] saltBytes = new byte[16];
            random.nextBytes(saltBytes);
            String salt = Base64.getEncoder().encodeToString(saltBytes);

            String toHash = salt + plainPassword;
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(toHash.getBytes());
            String hash = Base64.getEncoder().encodeToString(hashBytes);

            return salt + SEPARATOR + hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available: " + HASH_ALGORITHM, e);
        }
    }

    public static boolean verify(String plainPassword, String storedHash) {
        try {
            String[] parts = storedHash.split(SEPARATOR);
            if (parts.length != 2) return false;

            String salt = parts[0];
            String expectedHash = parts[1];

            String toHash = salt + plainPassword;
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(toHash.getBytes());
            String actualHash = Base64.getEncoder().encodeToString(hashBytes);

            return MessageDigest.isEqual(
                    Base64.getDecoder().decode(expectedHash),
                    Base64.getDecoder().decode(actualHash)
            );
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            return false;
        }
    }
}

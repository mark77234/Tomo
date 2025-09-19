package com.example.tomo.global;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IdConverter {

    // 문자열 → Long
    public static long stringToLong(String str) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException("Input string must not be null or blank");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(str.getBytes(StandardCharsets.UTF_8));

            // 첫 8바이트만 사용해 long으로 변환
            long result = 0;
            for (int i = 0; i < 8; i++) {
                result = (result << 8) | (hash[i] & 0xff);
            }

            // 음수 방지 (항상 양수 반환)
            return result & Long.MAX_VALUE;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}

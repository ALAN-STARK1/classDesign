package com.example.indras.common.util;

import com.example.indras.common.exception.BizException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public final class JwtUtil {

    private JwtUtil() {
    }

    public static String generateToken(Long userId, String username, String role, String secret, long expireSeconds) {
        long exp = Instant.now().getEpochSecond() + expireSeconds;
        String payload = userId + "|" + username + "|" + role + "|" + exp;
        String signature = hmac(payload, secret);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8))
                + "." + signature;
    }

    public static TokenClaims parseToken(String token, String secret) {
        if (token == null || !token.contains(".")) {
            throw BizException.unauthorized("Token 无效");
        }
        String[] parts = token.split("\\.", 2);
        String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        if (!hmac(payload, secret).equals(parts[1])) {
            throw BizException.unauthorized("Token 无效");
        }
        String[] fields = payload.split("\\|");
        long exp = Long.parseLong(fields[3]);
        if (Instant.now().getEpochSecond() > exp) {
            throw BizException.unauthorized("Token 已过期");
        }
        return new TokenClaims(Long.parseLong(fields[0]), fields[1], fields[2], exp);
    }

    private static String hmac(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
        } catch (Exception ex) {
            throw new IllegalStateException("JWT 签名失败", ex);
        }
    }

    public record TokenClaims(Long userId, String username, String role, long expireAt) {
    }
}

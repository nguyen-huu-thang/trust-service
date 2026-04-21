package vn.xime.trust.application.usecase.cert;

import vn.xime.trust.domain.model.Id;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

/**
 * TokenService
 *
 * JWT-like token:
 * BASE64URL(header).BASE64URL(payload).SIGNATURE
 *
 * - HMAC-SHA256
 * - self-contained
 * - dùng cho refresh token
 */
public class TokenService {

    private static final String HMAC_ALGO = "HmacSHA256";

    private final byte[] secret;

    public TokenService(String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    // =========================
    // GENERATE
    // =========================

    public String generate(
            String tokenId,
            String serviceId,
            String shardId,
            Id certId,
            Instant expiresAt
    ) {
        Objects.requireNonNull(tokenId);
        Objects.requireNonNull(serviceId);
        Objects.requireNonNull(shardId);
        Objects.requireNonNull(certId);
        Objects.requireNonNull(expiresAt);

        long now = Instant.now().getEpochSecond();

        // header
        String headerJson = """
                {"alg":"HS256","typ":"RT"}
                """;

        // payload
        String payloadJson = """
                {
                  "tid":"%s",
                  "sid":"%s",
                  "shid":"%s",
                  "cid":"%s",
                  "iat":%d,
                  "exp":%d,
                  "ver":1
                }
                """.formatted(
                tokenId,
                serviceId,
                shardId,
                certId.toString(),
                now,
                expiresAt.getEpochSecond()
        );

        String header = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

        String unsigned = header + "." + payload;

        String signature = sign(unsigned);

        return unsigned + "." + signature;
    }

    // =========================
    // VERIFY + PARSE
    // =========================

    public TokenPayload verifyAndParse(String token) {

        String[] parts = token.split("\\.");

        if (parts.length != 3) {
            throw new IllegalStateException("Invalid token format");
        }

        String header = parts[0];
        String payload = parts[1];
        String signature = parts[2];

        String unsigned = header + "." + payload;

        String expectedSignature = sign(unsigned);

        if (!constantTimeEquals(signature, expectedSignature)) {
            throw new IllegalStateException("Invalid token signature");
        }

        String payloadJson = new String(base64UrlDecode(payload), StandardCharsets.UTF_8);

        return parsePayload(payloadJson);
    }

    // =========================
    // HASH (FOR DB)
    // =========================

    public String hash(String token) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret, HMAC_ALGO));
            byte[] raw = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(raw);
        } catch (Exception e) {
            throw new RuntimeException("Hash failed", e);
        }
    }

    // =========================
    // SIGN
    // =========================

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret, HMAC_ALGO));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(raw);
        } catch (Exception e) {
            throw new RuntimeException("Sign failed", e);
        }
    }

    // =========================
    // PARSE PAYLOAD (simple)
    // =========================

    private TokenPayload parsePayload(String json) {
        // ⚠️ MVP: parse thủ công (tránh thêm lib)
        // production có thể dùng Jackson

        return new TokenPayload(
                extract(json, "tid"),
                extract(json, "sid"),
                extract(json, "shid"),
                extract(json, "cid"),
                Long.parseLong(extract(json, "iat")),
                Long.parseLong(extract(json, "exp"))
        );
    }

    private String extract(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);

        if (start == -1) {
            throw new IllegalStateException("Missing field: " + key);
        }

        start += pattern.length();

        // string value
        if (json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            return json.substring(start + 1, end);
        }

        // number
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }

        return json.substring(start, end);
    }

    // =========================
    // BASE64 URL
    // =========================

    private String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private byte[] base64UrlDecode(String data) {
        return Base64.getUrlDecoder().decode(data);
    }

    // =========================
    // TIMING SAFE COMPARE
    // =========================

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    // =========================
    // DTO
    // =========================

    public record TokenPayload(
            String tokenId,
            String serviceId,
            String shardId,
            String certId,
            long issuedAt,
            long expiresAt
    ) {
        public boolean isExpired(Instant now) {
            return now.getEpochSecond() >= expiresAt;
        }
    }
}
package vn.xime.trust.infrastructure.security;

import vn.xime.trust.application.port.out.TokenCodec;
import vn.xime.trust.domain.model.TokenPayload;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HmacTokenCodec implements TokenCodec {

    private static final String HMAC_ALGO = "HmacSHA256";

    private final byte[] secret;

    public HmacTokenCodec(String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    // =========================
    // ENCODE
    // =========================
    @Override
    public String encode(TokenPayload payload) {

        String headerJson = """
                {"alg":"HS256","typ":"RT"}
                """;

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
                payload.getTokenId(),
                payload.getServiceId(),
                payload.getShardId(),
                payload.getCertId(),
                payload.getIssuedAt(),
                payload.getExpiresAt()
        );

        String header = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String body = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

        String unsigned = header + "." + body;
        String signature = sign(unsigned);

        return unsigned + "." + signature;
    }

    // =========================
    // DECODE
    // =========================
    @Override
    public TokenPayload decode(String token) {

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
    // HASH
    // =========================
    @Override
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
    // PARSE JSON (MVP)
    // =========================
    private TokenPayload parsePayload(String json) {
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

        if (json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            return json.substring(start + 1, end);
        }

        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }

        return json.substring(start, end);
    }

    // =========================
    // BASE64
    // =========================
    private String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private byte[] base64UrlDecode(String data) {
        return Base64.getUrlDecoder().decode(data);
    }

    // =========================
    // TIMING SAFE
    // =========================
    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
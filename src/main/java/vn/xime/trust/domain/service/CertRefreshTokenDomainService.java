package vn.xime.trust.domain.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import vn.xime.trust.domain.model.CertRefreshToken;


public class CertRefreshTokenDomainService {

    // =========================
    // CONFIG
    // =========================

    // 🔥 7 YEARS retention
    private static final long HARD_DELETE_RETENTION_SECONDS =
            60L * 60 * 24 * 365 * 7;

    // =========================
    // VALIDATE
    // =========================

    public void validateToken(String a, String b) {
        if (a == null || b == null) {
            throw new IllegalStateException("Invalid token");
        }
        if (!constantTimeEquals(a, b)) {
            throw new IllegalStateException("Invalid token");
        }
    }

    public void validateCert(String privateKey1, String privateKey2) {
        if (privateKey1 == null || privateKey2 == null) {
            throw new IllegalStateException("Invalid certificate or private key");
        }
        if (!constantTimeEquals(privateKey1, privateKey2)) {
            throw new IllegalStateException("Invalid certificate or private key");
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);

        int max = Math.max(aBytes.length, bBytes.length);
        int result = aBytes.length ^ bBytes.length;

        for (int i = 0; i < max; i++) {
            byte x = i < aBytes.length ? aBytes[i] : 0;
            byte y = i < bBytes.length ? bBytes[i] : 0;
            result |= x ^ y;
        }

        return result == 0;
    }

    // =========================
    // SOFT DELETE
    // =========================

    public boolean shouldBeDeleted(CertRefreshToken token, Instant now) {
        return token.isExpired(now);
    }

    // =========================
    // HARD DELETE
    // =========================

    public boolean shouldBeHardDeleted(CertRefreshToken token, Instant now) {

        // chỉ xét token đã expired
        if (!token.isExpired(now)) {
            return false;
        }

        return token.getExpiresAt()
                .plusSeconds(HARD_DELETE_RETENTION_SECONDS)
                .isBefore(now);
    }

    // =========================
    // VALID STATE
    // =========================

    public boolean isUsable(CertRefreshToken token, Instant now) {
        return token.isValid(now) && token.getUsedAt() == null;
    }
}
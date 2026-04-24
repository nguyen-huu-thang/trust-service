package vn.xime.trust.domain.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.Certificate;

public class CertRefreshTokenDomainService {

    // =========================
    // VALIDATE
    // =========================

    /**
     * validate token trước khi rotate cert
     */
    public void validateToken(String a, String b) {
        if (a == null || b == null) {
            throw new IllegalStateException("Invalid token");
        }
        if (!constantTimeEquals(a, b)) {
            throw new IllegalStateException("Invalid token");
        }
    }

    public void validateCert(Certificate cert, String privateKeyEncrypted) {
        if (cert == null || privateKeyEncrypted == null) {
            throw new IllegalStateException("Invalid certificate or private key");
        }
        if (!constantTimeEquals(cert.getPrivateKeyEncrypted(), privateKeyEncrypted)) {
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

        return result != 0;
    }
}
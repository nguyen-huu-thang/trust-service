package vn.xime.trust.domain.policy;

import vn.xime.trust.domain.model.Certificate;

import java.time.Instant;
import java.util.Objects;

/**
 * Certificate Issuance Policy
 *
 * Chịu trách nhiệm:
 * - quyết định khi nào cần cấp cert mới
 * - tính expiresAt cho cert mới
 *
 * ⚠️ Domain logic thuần (không phụ thuộc DB, infra)
 */
public class CertificateIssuancePolicy {

    // =========================
    // DEFAULT CONFIG (MVP)
    // =========================

    // rotation interval (ví dụ: 100 ngày)
    private final long rotationIntervalSeconds = 60L * 60 * 24 * 100;

    // lifetime (ví dụ: 365 ngày)
    private final long certificateLifetimeSeconds = 60L * 60 * 24 * 365;

    // rotate sớm trước khi cert hết hạn
    // ví dụ: còn dưới 1 ngày thì rotate
    private final long expirationSafetyWindowSeconds = 60L * 60 * 24;

    // =========================
    // CORE LOGIC
    // =========================

    /**
     * Có cần issue cert mới không?
     *
     * Rule:
     * - nếu chưa có cert → true
     * - nếu quá rotation interval → true
     * - nếu cert sắp hết hạn → true
     * - nếu cert đã expired → true
     * - ngược lại → false
     */
    public boolean shouldIssueNewCertificate(
            Certificate latest,
            Instant now
    ) {
        Objects.requireNonNull(now, "now is required");

        if (latest == null) {
            return true;
        }

        // =========================
        // RULE 1:
        // rotate theo rotation interval
        // =========================

        Instant nextRotationTime = latest.getIssuedAt()
                .plusSeconds(rotationIntervalSeconds);

        boolean rotationDue =
                !now.isBefore(nextRotationTime);

        // =========================
        // RULE 2:
        // rotate nếu cert sắp hết hạn
        // =========================

        Instant expirationSafetyTime = latest.getExpiresAt()
                .minusSeconds(expirationSafetyWindowSeconds);

        boolean nearExpiration =
                !now.isBefore(expirationSafetyTime);

        return rotationDue || nearExpiration;
    }

    /**
     * Thời điểm rotate tiếp theo
     */
    public Instant calculateNextRotationTime(Certificate latest) {
        if (latest == null) {
            throw new IllegalArgumentException("latest certificate is required");
        }

        return latest.getIssuedAt()
                .plusSeconds(rotationIntervalSeconds);
    }

    /**
     * Tính expiresAt cho cert mới
     */
    public Instant calculateExpiresAt(Instant issuedAt) {
        Objects.requireNonNull(issuedAt, "issuedAt is required");

        return issuedAt.plusSeconds(certificateLifetimeSeconds);
    }

    // =========================
    // OPTIONAL HELPERS
    // =========================

    /**
     * Có thể reuse cert hiện tại không?
     */
    public boolean canReuseCurrentCertificate(
            Certificate latest,
            Instant now
    ) {
        if (latest == null) {
            return false;
        }

        return !shouldIssueNewCertificate(latest, now)
                && !latest.isExpired(now);
    }

    /**
     * Guard: đảm bảo cert hiện tại còn dùng được để rotate
     */
    public void ensureCanRotate(Certificate current, Instant now) {
        if (current == null) {
            throw new IllegalStateException("No current certificate");
        }

        if (current.isExpired(now)) {
            throw new IllegalStateException(
                    "Current certificate expired → require manual intervention"
            );
        }
    }

    // =========================
    // GETTERS
    // =========================

    public long getRotationIntervalSeconds() {
        return rotationIntervalSeconds;
    }

    public long getCertificateLifetimeSeconds() {
        return certificateLifetimeSeconds;
    }

    public long getExpirationSafetyWindowSeconds() {
        return expirationSafetyWindowSeconds;
    }
}
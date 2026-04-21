package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.Certificate;

import java.time.Instant;
import java.util.List;

public class CertificateValidationService {

    // =========================
    // BASIC VALIDATION
    // =========================

    /**
     * validate 1 cert có dùng được cho authentication không
     */
    public void validateActive(Certificate cert, Instant now) {
        if (cert == null) {
            throw new IllegalArgumentException("Certificate is required");
        }

        cert.ensureActive(now);
    }

    // =========================
    // ISSUE VALIDATION
    // =========================

    /**
     * validate khi issue cert mới
     */
    public void validateNewCertificate(
            List<Certificate> existingCerts,
            Instant newExpiresAt,
            Instant now
    ) {
        if (newExpiresAt == null) {
            throw new IllegalArgumentException("expiresAt is required");
        }

        if (!newExpiresAt.isAfter(now)) {
            throw new IllegalArgumentException("expiresAt must be in the future");
        }

        // =========================
        // OPTIONAL RULES (SAFE GUARD)
        // =========================

        // limit số cert ACTIVE cùng lúc (tránh leak / abuse)
        long activeCount = existingCerts.stream()
                .filter(c -> c.isActive(now))
                .count();

        if (activeCount >= 5) {
            throw new IllegalStateException(
                    "Too many active certificates for this service"
            );
        }
    }

    // =========================
    // ROTATION VALIDATION
    // =========================

    /**
     * validate trước khi rotate
     */
    public void validateRotationPreconditions(
            Certificate currentCert,
            Instant now
    ) {
        if (currentCert == null) {
            throw new IllegalStateException("Current certificate not found");
        }

        currentCert.ensureActive(now);
    }

    // =========================
    // REVOKE VALIDATION
    // =========================

    /**
     * validate revoke
     */
    public void validateRevoke(Certificate cert) {
        if (cert == null) {
            throw new IllegalArgumentException("Certificate is required");
        }

        // nếu đã revoked rồi → idempotent (không throw)
        if (cert.getStatus().name().equals("REVOKED")) {
            return;
        }
    }

    // =========================
    // CONSISTENCY CHECK (OPTIONAL)
    // =========================

    /**
     * đảm bảo chỉ có 1 cert active (nếu bạn muốn strict)
     */
    public void ensureSingleActiveCertificate(
            List<Certificate> certs,
            Instant now
    ) {
        long activeCount = certs.stream()
                .filter(c -> c.isActive(now))
                .count();

        if (activeCount > 1) {
            throw new IllegalStateException(
                    "Multiple active certificates detected"
            );
        }
    }
}
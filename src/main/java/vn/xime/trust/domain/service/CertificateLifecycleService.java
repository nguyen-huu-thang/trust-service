package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.CertificateStatus;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class CertificateLifecycleService {

    // 🔥 5 YEARS
    private static final long HARD_DELETE_RETENTION_SECONDS =
            60L * 60 * 24 * 365 * 5;

    // =========================
    // FILTER (COMMON)
    // =========================

    public List<Certificate> getAllActive(List<Certificate> certs, Instant now) {
        return certs.stream()
                .filter(c -> c.isActive(now))
                .collect(Collectors.toList());
    }

    public List<Certificate> getAllExpired(List<Certificate> certs, Instant now) {
        return certs.stream()
                .filter(c -> c.isExpired(now))
                .collect(Collectors.toList());
    }

    public List<Certificate> getAllRevoked(List<Certificate> certs) {
        return certs.stream()
                .filter(c -> c.getStatus() == CertificateStatus.REVOKED)
                .collect(Collectors.toList());
    }

    // =========================
    // SOFT DELETE RULE
    // =========================

    public boolean shouldBeDeleted(Certificate cert, Instant now) {
        return cert.isExpired(now);
    }

    // =========================
    // HARD DELETE RULE (NEW)
    // =========================

    public boolean shouldBeHardDeleted(Certificate cert, Instant now) {

        // chỉ xét cert đã expired
        if (!cert.isExpired(now)) {
            return false;
        }

        return cert.getExpiresAt()
                .plusSeconds(HARD_DELETE_RETENTION_SECONDS)
                .isBefore(now);
    }

    // =========================
    // EXPIRE TRANSITION (OPTIONAL)
    // =========================

    /**
     * kiểm tra cert đã hết hạn chưa (dùng cho audit / batch)
     */
    public boolean isExpired(Certificate cert, Instant now) {
        return cert.isExpired(now);
    }

    // =========================
    // REVOKE CHECK
    // =========================

    /**
     * cert có bị revoke không
     */
    public boolean isRevoked(Certificate cert) {
        return cert.getStatus() == CertificateStatus.REVOKED;
    }

    // =========================
    // SAFETY CHECK
    // =========================

    /**
     * kiểm tra service có còn cert active nào không
     */
    public boolean hasActiveCertificate(
            List<Certificate> certs,
            Instant now
    ) {
        return certs.stream().anyMatch(c -> c.isActive(now));
    }
}
package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.Certificate;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CertificateSelectionService {

    // =========================
    // COMMON FILTER
    // =========================

    private List<Certificate> filterActive(List<Certificate> certs, Instant now) {
        return certs.stream()
                .filter(c -> c.isActive(now))
                .collect(Collectors.toList());
    }

    // =========================
    // CURRENT CERT (STRICT)
    // =========================

    /**
     * Lấy cert đang active (dùng cho mTLS runtime)
     * - phải tồn tại
     * - lấy cert mới nhất (issuedAt lớn nhất)
     */
    public Certificate getCurrentCertificate(
            List<Certificate> certs,
            Instant now
    ) {
        return filterActive(certs, now).stream()
                .max(Comparator.comparing(Certificate::getIssuedAt))
                .orElseThrow(() ->
                        new IllegalStateException("No active certificate found")
                );
    }

    // =========================
    // CURRENT CERT (SAFE)
    // =========================

    /**
     * version Optional (không throw)
     */
    public Optional<Certificate> findCurrentCertificate(
            List<Certificate> certs,
            Instant now
    ) {
        return filterActive(certs, now).stream()
                .max(Comparator.comparing(Certificate::getIssuedAt));
    }

    // =========================
    // ALL ACTIVE CERTS
    // =========================

    /**
     * dùng cho debug / audit
     */
    public List<Certificate> getAllActiveCertificates(
            List<Certificate> certs,
            Instant now
    ) {
        return filterActive(certs, now);
    }

    // =========================
    // LATEST CERT (IGNORE STATUS)
    // =========================

    /**
     * lấy cert mới nhất theo issuedAt (bất kể active hay không)
     * dùng cho admin / debug
     */
    public Optional<Certificate> findLatestCertificate(
            List<Certificate> certs
    ) {
        return certs.stream()
                .max(Comparator.comparing(Certificate::getIssuedAt));
    }
}
package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.Certificate;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CertificateDomainService {

    /**
     * lấy cert hiện tại (mTLS)
     */
    public Optional<Certificate> findCurrentCertificate(
            List<Certificate> certs,
            Instant now
    ) {
        return certs.stream()
                .filter(c -> c.isActive(now))
                .max(Comparator.comparing(Certificate::getIssuedAt));
    }

    /**
     * validate cert dùng để authenticate
     */
    public void validateCertificate(Certificate cert, Instant now) {
        cert.ensureActive(now);
    }
}
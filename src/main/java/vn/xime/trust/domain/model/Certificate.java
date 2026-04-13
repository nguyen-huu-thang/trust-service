package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class Certificate {

    private final String kid;
    private final String serviceId;

    private final String publicCert;
    private final String privateKeyEncrypted;

    private final Instant issuedAt;
    private final Instant expiresAt;

    private final CertificateStatus status;

    public Certificate(
            String kid,
            String serviceId,
            String publicCert,
            String privateKeyEncrypted,
            Instant issuedAt,
            Instant expiresAt,
            CertificateStatus status
    ) {
        if (expiresAt.isBefore(issuedAt)) {
            throw new IllegalArgumentException("expiresAt must be after issuedAt");
        }

        this.kid = Objects.requireNonNull(kid);
        this.serviceId = Objects.requireNonNull(serviceId);
        this.publicCert = Objects.requireNonNull(publicCert);
        this.privateKeyEncrypted = Objects.requireNonNull(privateKeyEncrypted);
        this.issuedAt = Objects.requireNonNull(issuedAt);
        this.expiresAt = Objects.requireNonNull(expiresAt);
        this.status = Objects.requireNonNull(status);
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public boolean isActive(Instant now) {
        return status == CertificateStatus.ACTIVE
                && !now.isBefore(issuedAt)
                && now.isBefore(expiresAt);
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public void ensureActive(Instant now) {
        if (!isActive(now)) {
            throw new IllegalStateException("Certificate is not active");
        }
    }

    // =========================
    // GETTERS
    // =========================

    public String getKid() {
        return kid;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getPublicCert() {
        return publicCert;
    }

    public String getPrivateKeyEncrypted() {
        return privateKeyEncrypted;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public CertificateStatus getStatus() {
        return status;
    }
}
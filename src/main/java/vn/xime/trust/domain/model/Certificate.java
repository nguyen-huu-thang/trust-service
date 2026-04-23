package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class Certificate {

    private final Id id;

    private final String serviceId;

    private final String publicCert;
    private final String privateKeyEncrypted;

    private final Instant issuedAt;
    private final Instant expiresAt;

    private final CertificateStatus status;
    private final boolean deleted;

    public Certificate(
            Id id,
            String serviceId,
            String publicCert,
            String privateKeyEncrypted,
            Instant issuedAt,
            Instant expiresAt,
            CertificateStatus status,
            boolean deleted
    ) {
        if (expiresAt.isBefore(issuedAt)) {
            throw new IllegalArgumentException("expiresAt must be after issuedAt");
        }

        this.id = Objects.requireNonNull(id);
        this.serviceId = Objects.requireNonNull(serviceId);
        this.publicCert = Objects.requireNonNull(publicCert);
        this.privateKeyEncrypted = Objects.requireNonNull(privateKeyEncrypted);
        this.issuedAt = Objects.requireNonNull(issuedAt);
        this.expiresAt = Objects.requireNonNull(expiresAt);
        this.status = Objects.requireNonNull(status);
        this.deleted = deleted;
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

    public Id getId() {
        return id;
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

    public boolean isDeleted() {
        return deleted;
    }

    // =========================
    // STATE CHANGE
    // =========================

    public Certificate markDeleted() {
        return new Certificate(
                id,
                serviceId,
                publicCert,
                privateKeyEncrypted,
                issuedAt,
                expiresAt,
                CertificateStatus.EXPIRED,
                true
        );
    }

    public Certificate markRevoked() {
        return new Certificate(
                id,
                serviceId,
                publicCert,
                privateKeyEncrypted,
                issuedAt,
                expiresAt,
                CertificateStatus.REVOKED,
                deleted
        );
    }
}
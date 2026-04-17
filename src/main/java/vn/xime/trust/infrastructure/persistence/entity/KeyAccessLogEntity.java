package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "key_access_logs",
        indexes = {
                @Index(
                        name = "idx_key_access_logs_signer_time",
                        columnList = "signer_service_id,requested_at DESC"
                ),
                @Index(
                        name = "idx_key_access_logs_key",
                        columnList = "key_id"
                )
        }
)
public class KeyAccessLogEntity {

    // =========================
    // ID (KSUID)
    // =========================

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BYTEA")
    private byte[] id;

    // =========================
    // REFERENCE (NO FK)
    // =========================

    @Column(name = "key_id", columnDefinition = "BYTEA")
    private byte[] keyId;

    @Column(name = "signer_service_id", length = 20)
    private String signerServiceId;

    @Column(name = "verifier_service_id", length = 20)
    private String verifierServiceId;

    // =========================
    // ACTION
    // =========================

    @Column(name = "action", length = 50)
    private String action;

    /**
     * true nếu có trả private key
     */
    @Column(name = "include_private")
    private Boolean includePrivate;

    // =========================
    // TIME
    // =========================

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    // =========================
    // METADATA
    // =========================

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "success")
    private Boolean success;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // =========================
    // GETTER / SETTER
    // =========================

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getKeyId() {
        return keyId;
    }

    public void setKeyId(byte[] keyId) {
        this.keyId = keyId;
    }

    public String getSignerServiceId() {
        return signerServiceId;
    }

    public void setSignerServiceId(String signerServiceId) {
        this.signerServiceId = signerServiceId;
    }

    public String getVerifierServiceId() {
        return verifierServiceId;
    }

    public void setVerifierServiceId(String verifierServiceId) {
        this.verifierServiceId = verifierServiceId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getIncludePrivate() {
        return includePrivate;
    }

    public void setIncludePrivate(Boolean includePrivate) {
        this.includePrivate = includePrivate;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
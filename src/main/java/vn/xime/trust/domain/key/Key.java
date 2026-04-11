package vn.xime.trust.domain.key;

import java.time.Instant;
import java.util.Objects;

public class Key {

    private final String kid;
    private final String serviceName;

    private final String publicKey;
    private final String privateKeyEncrypted;

    private final KeyAlgorithm algorithm;
    private final int keySize;

    // Status chỉ mang tính hỗ trợ (không phải nguồn quyết định chính)
    private KeyStatus status;

    private final Instant createdAt;

    /**
     * Thời điểm bắt đầu dùng để SIGN
     */
    private final Instant activateAt;

    /**
     * Thời điểm NGỪNG VERIFY
     * (quan trọng nhất)
     */
    private final Instant expiresAt;

    private boolean deleted;

    public Key(
            String kid,
            String serviceName,
            String publicKey,
            String privateKeyEncrypted,
            KeyAlgorithm algorithm,
            int keySize,
            KeyStatus status,
            Instant createdAt,
            Instant activateAt,
            Instant expiresAt,
            boolean deleted
    ) {
        this.kid = kid;
        this.serviceName = serviceName;
        this.publicKey = publicKey;
        this.privateKeyEncrypted = privateKeyEncrypted;
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.status = status;
        this.createdAt = createdAt;
        this.activateAt = activateAt;
        this.expiresAt = expiresAt;
        this.deleted = deleted;
    }

    // =========================
    // Business Logic
    // =========================

    /**
     * Có thể dùng để SIGN không
     */
    public boolean isUsableForSign(Instant now) {
        return !deleted
                && activateAt != null
                && !now.isBefore(activateAt)
                && !isExpiredForVerify(now); // chưa hết verify thì vẫn sign được (giả định)
    }

    /**
     * Có thể dùng để VERIFY không
     */
    public boolean isUsableForVerify(Instant now) {
        return !deleted
                && (expiresAt == null || now.isBefore(expiresAt));
    }

    /**
     * Đã hết hạn VERIFY chưa
     */
    public boolean isExpiredForVerify(Instant now) {
        return expiresAt != null && !now.isBefore(expiresAt);
    }

    /**
     * Active theo thời gian (dùng cho SIGN)
     */
    public boolean isActivated(Instant now) {
        return activateAt != null && !now.isBefore(activateAt);
    }

    // =========================
    // Lifecycle helpers
    // =========================

    public void markAsCurrent() {
        this.status = KeyStatus.CURRENT;
    }

    public void markAsOld() {
        this.status = KeyStatus.OLD;
    }

    public void markDeleted() {
        this.deleted = true;
    }

    // =========================
    // Getter
    // =========================

    public String getKid() {
        return kid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKeyEncrypted() {
        return privateKeyEncrypted;
    }

    public KeyAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getKeySize() {
        return keySize;
    }

    public KeyStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getActivateAt() {
        return activateAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    // =========================
    // Equality
    // =========================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key key)) return false;
        return Objects.equals(kid, key.kid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kid);
    }
}
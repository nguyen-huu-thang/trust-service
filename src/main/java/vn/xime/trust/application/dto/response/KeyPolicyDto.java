package vn.xime.trust.application.dto.response;

import java.time.Instant;

public class KeyPolicyDto {

    private final String id;
    private final String signerServiceId;
    private final String verifierServiceId;

    private final String algorithm;
    private final int keySize;

    private final long keyLifetimeSec;
    private final long rotationIntervalSeconds;
    private final long preloadSec;

    private final Instant createdAt;
    private final Instant updatedAt;

    public KeyPolicyDto(
        String id,
        String signerServiceId,
        String verifierServiceId,
        String algorithm,
        int keySize,
        long keyLifetimeSec,
        long rotationIntervalSeconds,
        long preloadSec,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = id;
        this.signerServiceId = signerServiceId;
        this.verifierServiceId = verifierServiceId;
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.keyLifetimeSec = keyLifetimeSec;
        this.rotationIntervalSeconds = rotationIntervalSeconds;
        this.preloadSec = preloadSec;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getSignerServiceId() {
        return signerServiceId;
    }

    public String getVerifierServiceId() {
        return verifierServiceId;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public int getKeySize() {
        return keySize;
    }

    public long getKeyLifetimeSec() {
        return keyLifetimeSec;
    }

    public long getRotationIntervalSeconds() {
        return rotationIntervalSeconds;
    }

    public long getPreloadSec() {
        return preloadSec;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
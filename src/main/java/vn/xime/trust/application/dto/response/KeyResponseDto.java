package vn.xime.trust.application.dto.response;

import java.time.Instant;

public class KeyResponseDto {

    private final String id;
    private final String signerServiceId;
    private final String verifierServiceId;
    private final String algorithm;
    private final int keySize;
    private final Instant activateAt;
    private final Instant expiresAt;

    public KeyResponseDto(
            String id,
            String signerServiceId,
            String verifierServiceId,
            String algorithm,
            int keySize,
            Instant activateAt,
            Instant expiresAt
    ) {
        this.id = id;
        this.signerServiceId = signerServiceId;
        this.verifierServiceId = verifierServiceId;
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.activateAt = activateAt;
        this.expiresAt = expiresAt;
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

    public Instant getActivateAt() {
        return activateAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
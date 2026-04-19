package vn.xime.trust.application.dto.response;

import java.time.Instant;

public class PrivateKeyDto {

    private final String kid;
    private final String signerServiceId;
    private final String algorithm;
    private final int keySize;
    private final String privateKey;
    private final Instant activateAt;
    private final Instant expiresAt;

    public PrivateKeyDto(
            String kid,
            String signerServiceId,
            String algorithm,
            int keySize,
            String privateKey,
            Instant activateAt,
            Instant expiresAt
    ) {
        this.kid = kid;
        this.signerServiceId = signerServiceId;
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.privateKey = privateKey;
        this.activateAt = activateAt;
        this.expiresAt = expiresAt;
    }

    public String getKid() {
        return kid;
    }

    public String getSignerServiceId() {
        return signerServiceId;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }

    public int getKeySize() {
        return keySize;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public Instant getActivateAt() {
        return activateAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
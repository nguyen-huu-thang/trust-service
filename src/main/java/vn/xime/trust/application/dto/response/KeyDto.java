package vn.xime.trust.application.dto.response;

import java.time.Instant;

public class KeyDto {

    private final String kid;
    private final String publicKey;
    private final String algorithm;
    private final int keySize;
    private final Instant activateAt;
    private final Instant expiresAt;

    public KeyDto(
            String kid,
            String publicKey,
            String algorithm,
            int keySize,
            Instant activateAt,
            Instant expiresAt
    ) {
        this.kid = kid;
        this.publicKey = publicKey;
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.activateAt = activateAt;
        this.expiresAt = expiresAt;
    }

    public String getKid() {
        return kid;
    }

    public String getPublicKey() {
        return publicKey;
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
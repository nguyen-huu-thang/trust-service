package vn.xime.trust.application.dto.response;

import java.time.Instant;

public class PublicKeyDto {

    private final String kid;
    private final String verifierServiceId;
    private final String algorithm;
    private final int keySize;
    private final String publicKey;
    private final Instant activateAt;
    private final Instant expiresAt;

    public PublicKeyDto(
            String kid,
            String verifierServiceId,
            String algorithm,
            int keySize,
            String publicKey,
            Instant activateAt,
            Instant expiresAt
    ) {
        this.kid = kid;
        this.verifierServiceId = verifierServiceId;
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.publicKey = publicKey;
        this.activateAt = activateAt;
        this.expiresAt = expiresAt;
    }

    public String getKid() {
        return kid;
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
    
    public String getPublicKey() {
        return publicKey;
    }

    public Instant getActivateAt() {
        return activateAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
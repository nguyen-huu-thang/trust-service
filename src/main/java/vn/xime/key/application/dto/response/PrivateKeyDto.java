package vn.xime.key.application.dto.response;

import java.time.Instant;

public class PrivateKeyDto {

    private final String kid;
    private final String publicKey;
    private final String privateKey;

    private final Instant activateAt;
    private final Instant expiresAt;

    public PrivateKeyDto(
            String kid,
            String publicKey,
            String privateKey,
            Instant activateAt,
            Instant expiresAt
    ) {
        this.kid = kid;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.activateAt = activateAt;
        this.expiresAt = expiresAt;
    }

    public String getKid() {
        return kid;
    }

    public String getPublicKey() {
        return publicKey;
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
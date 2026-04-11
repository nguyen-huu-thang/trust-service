package vn.xime.trust.application.dto.response;

import java.time.Instant;

public class PublicKeyDto {

    private final String kid;
    private final String publicKey;

    private final Instant activateAt;
    private final Instant expiresAt;

    public PublicKeyDto(
            String kid,
            String publicKey,
            Instant activateAt,
            Instant expiresAt
    ) {
        this.kid = kid;
        this.publicKey = publicKey;
        this.activateAt = activateAt;
        this.expiresAt = expiresAt;
    }

    public String getKid() {
        return kid;
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
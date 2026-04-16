package vn.xime.trust.application.dto.response;

import java.time.Instant;

public class TrustDto {

    private final String signerServiceId;
    private final String verifierServiceId;

    private final long keyLifetimeSec;
    private final long jwtTtlSec;
    private final long preloadSec;

    private final Instant createdAt;

    public TrustDto(
            String signerServiceId,
            String verifierServiceId,
            long keyLifetimeSec,
            long jwtTtlSec,
            long preloadSec,
            Instant createdAt
    ) {
        this.signerServiceId = signerServiceId;
        this.verifierServiceId = verifierServiceId;
        this.keyLifetimeSec = keyLifetimeSec;
        this.jwtTtlSec = jwtTtlSec;
        this.preloadSec = preloadSec;
        this.createdAt = createdAt;
    }

    public String getSignerServiceId() {
        return signerServiceId;
    }

    public String getVerifierServiceId() {
        return verifierServiceId;
    }

    public long getKeyLifetimeSec() {
        return keyLifetimeSec;
    }

    public long getJwtTtlSec() {
        return jwtTtlSec;
    }

    public long getPreloadSec() {
        return preloadSec;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
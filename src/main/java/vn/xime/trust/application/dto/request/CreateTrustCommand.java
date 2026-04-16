package vn.xime.trust.application.dto.request;

public class CreateTrustCommand {

    private final String id;
    private final String signerServiceId;
    private final String verifierServiceId;

    private final long keyLifetimeSec;
    private final long jwtTtlSec;
    private final long preloadSec;

    public CreateTrustCommand(
            String id,
            String signerServiceId,
            String verifierServiceId,
            long keyLifetimeSec,
            long jwtTtlSec,
            long preloadSec
    ) {
        this.id = id;
        this.signerServiceId = signerServiceId;
        this.verifierServiceId = verifierServiceId;
        this.keyLifetimeSec = keyLifetimeSec;
        this.jwtTtlSec = jwtTtlSec;
        this.preloadSec = preloadSec;
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

    public long getKeyLifetimeSec() {
        return keyLifetimeSec;
    }

    public long getJwtTtlSec() {
        return jwtTtlSec;
    }

    public long getPreloadSec() {
        return preloadSec;
    }
}
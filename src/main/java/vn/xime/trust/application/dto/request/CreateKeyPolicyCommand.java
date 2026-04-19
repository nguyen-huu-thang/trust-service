package vn.xime.trust.application.dto.request;

public class CreateKeyPolicyCommand {

    private final String signerServiceId;
    private final String verifierServiceId;

    private final long keyLifetimeSec;
    private final long jwtTtlSec;
    private final long preloadSec;

    public CreateKeyPolicyCommand(
            String signerServiceId,
            String verifierServiceId,
            long keyLifetimeSec,
            long jwtTtlSec,
            long preloadSec
    ) {
        this.signerServiceId = signerServiceId;
        this.verifierServiceId = verifierServiceId;
        this.keyLifetimeSec = keyLifetimeSec;
        this.jwtTtlSec = jwtTtlSec;
        this.preloadSec = preloadSec;
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
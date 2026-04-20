package vn.xime.trust.application.dto.request;

public class CreateKeyPolicyCommand {

    private final String signerServiceId;
    private final String verifierServiceId;

    // 🔥 NEW
    private final String algorithm;
    private final int keySize;

    private final long keyLifetimeSec;
    private final long jwtTtlSec;
    private final long preloadSec;

    public CreateKeyPolicyCommand(
            String signerServiceId,
            String verifierServiceId,
            String algorithm,
            int keySize,
            long keyLifetimeSec,
            long jwtTtlSec,
            long preloadSec
    ) {
        this.signerServiceId = signerServiceId;
        this.verifierServiceId = verifierServiceId;
        this.algorithm = algorithm;
        this.keySize = keySize;
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

    public String getAlgorithm() {
        return algorithm;
    }

    public int getKeySize() {
        return keySize;
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
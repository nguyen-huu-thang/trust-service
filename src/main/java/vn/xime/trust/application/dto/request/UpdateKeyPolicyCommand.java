package vn.xime.trust.application.dto.request;

public class UpdateKeyPolicyCommand {

    private final String id;

    // 🔥 NEW
    private final String algorithm;
    private final int keySize;

    private final long keyLifetimeSec;
    private final long jwtTtlSec;
    private final long preloadSec;

    public UpdateKeyPolicyCommand(
            String id,
            String algorithm,
            int keySize,
            long keyLifetimeSec,
            long jwtTtlSec,
            long preloadSec
    ) {
        this.id = id;
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.keyLifetimeSec = keyLifetimeSec;
        this.jwtTtlSec = jwtTtlSec;
        this.preloadSec = preloadSec;
    }

    public String getId() {
        return id;
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
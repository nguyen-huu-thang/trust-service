package vn.xime.trust.application.dto.request;

public class UpdateKeyPolicyCommand {

    private final String id;

    // optional fields (nullable for partial update)
    private final String algorithm;
    private final Integer keySize;

    private final Long keyLifetimeSec;
    private final Long rotationIntervalSeconds;
    private final Long preloadSec;

    public UpdateKeyPolicyCommand(
            String id,
            String algorithm,
            Integer keySize,
            Long keyLifetimeSec,
            Long rotationIntervalSeconds,
            Long preloadSec
    ) {
        this.id = id;
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.keyLifetimeSec = keyLifetimeSec;
        this.rotationIntervalSeconds = rotationIntervalSeconds;
        this.preloadSec = preloadSec;
    }

    public String getId() {
        return id;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public Integer getKeySize() {
        return keySize;
    }

    public Long getKeyLifetimeSec() {
        return keyLifetimeSec;
    }

    public Long getRotationIntervalSeconds() {
        return rotationIntervalSeconds;
    }

    public Long getPreloadSec() {
        return preloadSec;
    }
}
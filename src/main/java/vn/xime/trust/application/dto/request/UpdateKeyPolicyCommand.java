package vn.xime.trust.application.dto.request;

public class UpdateKeyPolicyCommand {

    private final String id;

    private final long keyLifetimeSec;
    private final long jwtTtlSec;
    private final long preloadSec;

    public UpdateKeyPolicyCommand(
            String id,
            long keyLifetimeSec,
            long jwtTtlSec,
            long preloadSec
    ) {
        this.id = id;
        this.keyLifetimeSec = keyLifetimeSec;
        this.jwtTtlSec = jwtTtlSec;
        this.preloadSec = preloadSec;
    }

    public String getId() {
        return id;
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
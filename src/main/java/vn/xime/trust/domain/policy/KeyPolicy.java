package vn.xime.trust.domain.policy;

public class KeyPolicy {

    private final long keyLifetimeSeconds;
    private final long jwtTtlSeconds;
    private final long preloadSeconds;

    public KeyPolicy(long keyLifetimeSeconds, long jwtTtlSeconds, long preloadSeconds) {
        this.keyLifetimeSeconds = keyLifetimeSeconds;
        this.jwtTtlSeconds = jwtTtlSeconds;
        this.preloadSeconds = preloadSeconds;
    }

    public long getKeyLifetimeSeconds() {
        return keyLifetimeSeconds;
    }

    public long getJwtTtlSeconds() {
        return jwtTtlSeconds;
    }

    public long getPreloadSeconds() {
        return preloadSeconds;
    }
}
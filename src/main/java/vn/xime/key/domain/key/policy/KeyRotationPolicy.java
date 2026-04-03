package vn.xime.key.domain.key.policy;

import vn.xime.key.domain.key.Key;
import vn.xime.key.domain.key.KeyPolicy;

import java.time.Instant;

/**
 * Domain Service: KeyRotationPolicy
 *
 * =========================
 * Vai trò:
 * =========================
 * * Quyết định:
 *   - khi nào tạo key mới
 *   - activateAt / expiresAt của key mới
 *
 * =========================
 * Nguyên tắc:
 * =========================
 * * Rotation KHÔNG invalidate key cũ
 * * expiresAt phải >= activateAt(next) + jwtTTL
 */
public class KeyRotationPolicy {

    /**
     * Tính activateAt cho key mới
     */
    public Instant calculateActivateAt(Instant now, KeyPolicy policy) {
        return now.plusSeconds(policy.getPreloadSeconds());
    }

    /**
     * Tính expiresAt cho key hiện tại
     */
    public Instant calculateExpiresAt(Instant nextActivateAt, KeyPolicy policy) {
        return nextActivateAt.plusSeconds(policy.getJwtTtlSeconds());
    }

    /**
     * Có cần rotate không
     */
    public boolean shouldRotate(Key currentKey, Instant now, KeyPolicy policy) {
        if (currentKey == null) return true;

        Instant rotateTime = currentKey.getActivateAt()
                .plusSeconds(policy.getKeyLifetimeSeconds());

        return !now.isBefore(rotateTime);
    }
}
package vn.xime.trust.domain.policy;

import java.time.Instant;

import vn.xime.trust.domain.model.Key;

/**
 * Domain Service: KeyValidationPolicy
 *
 * =========================
 * Vai trò:
 * =========================
 * * Validate domain rule trước khi save
 */
public class KeyValidationPolicy {

    public void validate(Key key) {
        if (key.getActivateAt() == null) {
            throw new IllegalArgumentException("activateAt must not be null");
        }

        if (key.getExpiresAt() != null &&
                key.getExpiresAt().isBefore(key.getActivateAt())) {
            throw new IllegalArgumentException("expiresAt must be >= activateAt");
        }

        if (key.getKeySize() <= 0) {
            throw new IllegalArgumentException("invalid key size");
        }
    }

    public void validateForSigning(Key key, Instant now) {
        if (!key.isUsableForSign(now)) {
            throw new IllegalStateException("Key is not usable for signing");
        }
    }
}
package vn.xime.trust.domain.service;

import vn.xime.trust.domain.policy.KeyPolicy;

import java.time.Instant;

public class KeyPolicyDomainService {

    /**
     * tính thời điểm activate của key mới
     */
    public Instant calculateNextActivateAt(
            KeyPolicy policy,
            Instant currentActivateAt
    ) {
        return policy.calculateNextActivationTime(currentActivateAt);
    }

    /**
     * validate policy trước khi apply
     */
    public void validatePolicy(KeyPolicy policy) {
        if (!policy.isValidForJwtSafety()) {
            throw new IllegalArgumentException(
                    "keyLifetime must be >= jwtTtl"
            );
        }
    }
}
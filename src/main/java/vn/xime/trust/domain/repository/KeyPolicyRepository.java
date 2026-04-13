package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.policy.KeyPolicy;

import java.util.Optional;

public interface KeyPolicyRepository {

    KeyPolicy save(KeyPolicy policy);

    Optional<KeyPolicy> findByServiceId(String serviceId);

}
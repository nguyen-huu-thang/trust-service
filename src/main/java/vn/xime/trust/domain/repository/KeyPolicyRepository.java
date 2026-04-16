package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.KeyPolicy;

import java.util.List;
import java.util.Optional;

public interface KeyPolicyRepository {

    KeyPolicy save(KeyPolicy trust);

    Optional<KeyPolicy> findByPair(String signerServiceId, String verifierServiceId);

    List<KeyPolicy> findBySignerServiceId(String signerServiceId);

    List<KeyPolicy> findByVerifierServiceId(String verifierServiceId);
}
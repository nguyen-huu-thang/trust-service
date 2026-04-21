package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.model.Id;


import java.util.List;
import java.util.Optional;

public interface KeyPolicyRepository {

    KeyPolicy save(KeyPolicy trust);

    Optional<KeyPolicy> findById(Id id);

    Optional<KeyPolicy> findByPair(String signerServiceId, String verifierServiceId);

    List<KeyPolicy> findBySignerServiceId(String signerServiceId);

    List<KeyPolicy> findByVerifierServiceId(String verifierServiceId);

    List<KeyPolicy> findAll();

    boolean deleteById(Id id);
}
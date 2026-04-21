package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.KeyAlgorithm;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.infrastructure.persistence.entity.KeyPolicyEntity;

import java.util.Arrays;

public class KeyPolicyMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static KeyPolicy toDomain(KeyPolicyEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("KeyPolicyEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getSignerServiceId(), "signerServiceId");
        requireNonNull(e.getVerifierServiceId(), "verifierServiceId");
        requireNonNull(e.getAlgorithm(), "algorithm");
        requireNonNull(e.getKeySize(), "keySize");
        requireNonNull(e.getKeyLifetimeSeconds(), "keyLifetimeSeconds");
        requireNonNull(e.getRotationIntervalSeconds(), "rotationIntervalSeconds");
        requireNonNull(e.getPreloadSeconds(), "preloadSeconds");
        requireNonNull(e.getCreatedAt(), "createdAt");

        KeyAlgorithm algorithm = toAlgorithm(e.getAlgorithm());

        return new KeyPolicy(
                toId(e.getId()),
                e.getSignerServiceId(),
                e.getVerifierServiceId(),
                algorithm,
                e.getKeySize(),
                e.getKeyLifetimeSeconds(),
                e.getRotationIntervalSeconds(),
                e.getPreloadSeconds(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static KeyPolicyEntity toEntity(KeyPolicy d) {

        if (d == null) {
            throw new IllegalArgumentException("KeyPolicy must not be null");
        }

        KeyPolicyEntity e = new KeyPolicyEntity();

        e.setId(toBytes(d.getId()));
        e.setSignerServiceId(d.getSignerServiceId());
        e.setVerifierServiceId(d.getVerifierServiceId());

        // 🔥 enum -> string
        e.setAlgorithm(d.getAlgorithm().name());
        e.setKeySize(d.getKeySize());

        e.setKeyLifetimeSeconds(d.getKeyLifetimeSeconds());
        e.setRotationIntervalSeconds(d.getRotationIntervalSeconds());
        e.setPreloadSeconds(d.getPreloadSeconds());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());

        return e;
    }

    // =========================
    // ALGORITHM MAPPING
    // =========================

    private static KeyAlgorithm toAlgorithm(String raw) {
        try {
            return KeyAlgorithm.valueOf(raw.trim().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalStateException("Invalid algorithm in DB: " + raw);
        }
    }

    // =========================
    // ID MAPPING
    // =========================

    private static Id toId(byte[] bytes) {
        return new Id(copy(bytes));
    }

    private static byte[] toBytes(Id id) {
        return copy(id.toBytes());
    }

    private static byte[] copy(byte[] src) {
        return src == null ? null : Arrays.copyOf(src, src.length);
    }

    // =========================
    // Helpers
    // =========================

    private static void requireNonNull(Object value, String field) {
        if (value == null) {
            throw new IllegalStateException(field + " must not be null");
        }
    }
}
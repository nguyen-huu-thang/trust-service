package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.model.KeyAlgorithm;
import vn.xime.trust.infrastructure.persistence.entity.KeyEntity;

import java.util.Arrays;

public class KeyMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static Key toDomain(KeyEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("KeyEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getSignerServiceId(), "signerServiceId");
        requireNonNull(e.getVerifierServiceId(), "verifierServiceId");
        requireNonNull(e.getPublicKey(), "publicKey");
        requireNonNull(e.getPrivateKeyEncrypted(), "privateKeyEncrypted");
        requireNonNull(e.getAlgorithm(), "algorithm");
        requireNonNull(e.getKeySize(), "keySize");
        requireNonNull(e.getCreatedAt(), "createdAt");
        requireNonNull(e.getActivateAt(), "activateAt");
        requireNonNull(e.getExpiresAt(), "expiresAt");
        requireNonNull(e.getIsDeleted(), "isDeleted");

        return new Key(
                toId(e.getId()),
                e.getSignerServiceId(),
                e.getVerifierServiceId(),
                e.getPublicKey(),
                e.getPrivateKeyEncrypted(),
                mapAlgorithm(e.getAlgorithm()),
                e.getKeySize(),
                e.getCreatedAt(),
                e.getActivateAt(),
                e.getExpiresAt(),
                e.getIsDeleted()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static KeyEntity toEntity(Key d) {

        if (d == null) {
            throw new IllegalArgumentException("Key must not be null");
        }

        KeyEntity e = new KeyEntity();

        e.setId(toBytes(d.getId()));
        e.setSignerServiceId(d.getSignerServiceId());
        e.setVerifierServiceId(d.getVerifierServiceId());
        e.setPublicKey(d.getPublicKey());
        e.setPrivateKeyEncrypted(d.getPrivateKeyEncrypted());
        e.setAlgorithm(d.getAlgorithm().name());
        e.setKeySize(d.getKeySize());
        e.setCreatedAt(d.getCreatedAt());
        e.setActivateAt(d.getActivateAt());
        e.setExpiresAt(d.getExpiresAt());
        e.setIsDeleted(d.isDeleted());

        return e;
    }

    // =========================
    // ID MAPPING (QUAN TRỌNG)
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

    private static KeyAlgorithm mapAlgorithm(String algorithm) {

        try {
            return KeyAlgorithm.valueOf(algorithm.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid algorithm: " + algorithm);
        }
    }

    private static void requireNonNull(Object value, String field) {
        if (value == null) {
            throw new IllegalStateException(field + " must not be null");
        }
    }
}
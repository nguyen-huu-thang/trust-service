package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.infrastructure.persistence.entity.CertRefreshTokenEntity;

import java.util.Arrays;

public class CertRefreshTokenMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static CertRefreshToken toDomain(CertRefreshTokenEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("CertRefreshTokenEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getTokenHash(), "tokenHash");
        requireNonNull(e.getIssuedAt(), "issuedAt");
        requireNonNull(e.getExpiresAt(), "expiresAt");
        // usedAt có thể null → không check
        // isDeleted luôn có default → không cần check

        return new CertRefreshToken(
                toId(e.getId()),
                e.getTokenHash(),
                e.isBootstrap(),
                e.getIssuedAt(),
                e.getExpiresAt(),
                e.getUsedAt(),   // nullable OK
                e.isDeleted()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static CertRefreshTokenEntity toEntity(CertRefreshToken d) {

        if (d == null) {
            throw new IllegalArgumentException("CertRefreshToken must not be null");
        }

        CertRefreshTokenEntity e = new CertRefreshTokenEntity();

        e.setId(toBytes(d.getId()));
        e.setTokenHash(d.getTokenHash());
        e.setBootstrap(d.isBootstrap());
        e.setIssuedAt(d.getIssuedAt());
        e.setExpiresAt(d.getExpiresAt());
        e.setUsedAt(d.getUsedAt()); // nullable
        e.setDeleted(d.isDeleted());

        return e;
    }

    // =========================
    // ID mapping
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
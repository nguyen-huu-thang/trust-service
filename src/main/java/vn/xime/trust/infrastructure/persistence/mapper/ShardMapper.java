package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.model.ShardStatus;
import vn.xime.trust.infrastructure.persistence.entity.ShardEntity;

public class ShardMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static Shard toDomain(ShardEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("ShardEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getServiceId(), "serviceId");
        requireNonNull(e.getStatus(), "status");
        requireNonNull(e.getCreatedAt(), "createdAt");

        return new Shard(
                e.getId(),
                e.getServiceId(),
                e.getHost(),
                e.getPort(),
                mapStatus(e.getStatus()),
                e.getCreatedAt()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static ShardEntity toEntity(Shard d) {

        if (d == null) {
            throw new IllegalArgumentException("Shard must not be null");
        }

        ShardEntity e = new ShardEntity();

        e.setId(d.getId());
        e.setServiceId(d.getServiceId());
        e.setHost(d.getHost());
        e.setPort(d.getPort());
        e.setStatus(d.getStatus().name());
        e.setCreatedAt(d.getCreatedAt());

        return e;
    }

    // =========================
    // Helpers
    // =========================

    private static ShardStatus mapStatus(String status) {
        try {
            return ShardStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid shard status: " + status);
        }
    }

    private static void requireNonNull(Object value, String field) {
        if (value == null) {
            throw new IllegalStateException(field + " must not be null");
        }
    }
}
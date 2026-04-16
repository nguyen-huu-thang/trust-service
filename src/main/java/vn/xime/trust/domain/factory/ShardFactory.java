package vn.xime.trust.domain.factory;

import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.model.ShardStatus;

import java.time.Instant;


public class ShardFactory {

    public Shard create(
        String id,
        String serviceId,
        String host,
        Integer port
    ) {
    
    // =========================
    // VALIDATE (DOMAIN LEVEL)
    // =========================

    if (id == null || id.isBlank()) {
        throw new IllegalArgumentException("id is required");
    }
    if (serviceId == null || serviceId.isBlank()) {
        throw new IllegalArgumentException("serviceId is required");
    }

    return new Shard(
        id,
        serviceId,
        host,
        port,
        ShardStatus.INACTIVE,
        Instant.now()
    );
    }
}
package vn.xime.key.infrastructure.persistence.mapper;

import vn.xime.key.infrastructure.persistence.entity.KeyEventEntity;

public class KeyEventMapper {

    public static KeyEventEntity toEntity(
            String kid,
            String serviceName,
            String eventType,
            String metadata,
            java.time.Instant createdAt
    ) {
        KeyEventEntity entity = new KeyEventEntity();

        entity.setKid(kid);
        entity.setServiceName(serviceName);
        entity.setEventType(eventType);
        entity.setMetadata(metadata);
        entity.setCreatedAt(createdAt);

        return entity;
    }
}
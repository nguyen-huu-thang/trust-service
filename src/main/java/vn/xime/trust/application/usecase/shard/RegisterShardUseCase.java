package vn.xime.trust.application.usecase.shard;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.RegisterShardCommand;
import vn.xime.trust.domain.factory.ShardFactory;
import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.repository.ShardRepository;

@Component
public class RegisterShardUseCase {

    private final ShardRepository shardRepository;
    private final ServiceRepository serviceRepository;
    private final ShardFactory shardFactory;

    public RegisterShardUseCase(
            ShardRepository shardRepository,
            ServiceRepository serviceRepository,
            ShardFactory shardFactory
    ) {
        this.shardRepository = shardRepository;
        this.serviceRepository = serviceRepository;
        this.shardFactory = shardFactory;
    }

    @Transactional
    public String execute(RegisterShardCommand cmd) {

        // =========================
        // VALIDATE
        // =========================

        if (cmd.getId() == null || cmd.getId().isBlank()) {
            throw new IllegalArgumentException("shard id is required");
        }

        if (cmd.getServiceId() == null || cmd.getServiceId().isBlank()) {
            throw new IllegalArgumentException("serviceId is required");
        }

        if (cmd.getHost() == null || cmd.getHost().isBlank()) {
            throw new IllegalArgumentException("host is required");
        }

        if (!serviceRepository.existsById(cmd.getServiceId())) {
            throw new IllegalStateException(
                    "Service not found: " + cmd.getServiceId()
            );
        }

        // =========================
        // BUILD DOMAIN
        // =========================

        Shard shard = shardFactory.create(
                cmd.getId(),
                cmd.getServiceId(),
                cmd.getHost(),
                cmd.getPort()
        );

        // =========================
        // SAVE
        // =========================

        shardRepository.save(shard);

        return shard.getId();
    }
}
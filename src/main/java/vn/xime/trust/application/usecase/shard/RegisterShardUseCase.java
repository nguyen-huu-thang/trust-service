package vn.xime.trust.application.usecase.shard;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.RegisterShardCommand;
import vn.xime.trust.application.dto.response.ShardDto;
import vn.xime.trust.domain.factory.ShardFactory;
import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.repository.ShardRepository;
import vn.xime.trust.application.mapper.ShardMapper;

@Component
public class RegisterShardUseCase {

    private final ShardRepository shardRepository;
    private final ServiceRepository serviceRepository;
    private final ShardFactory shardFactory;
    private final ShardMapper shardMapper;

    public RegisterShardUseCase(
            ShardRepository shardRepository,
            ServiceRepository serviceRepository,
            ShardFactory shardFactory,
            ShardMapper shardMapper
    ) {
        this.shardRepository = shardRepository;
        this.serviceRepository = serviceRepository;
        this.shardFactory = shardFactory;
        this.shardMapper = shardMapper;
    }

    @Transactional
    public ShardDto execute(RegisterShardCommand cmd) {

        if (cmd.getId() == null || cmd.getId().isBlank()) {
            throw new IllegalArgumentException("shard id is required");
        }

        if (!serviceRepository.existsById(cmd.getServiceId())) {
            throw new IllegalStateException("Service not found: " + cmd.getServiceId());
        }

        if (shardRepository.existsById(cmd.getId())) {
            throw new IllegalStateException("Shard already exists: " + cmd.getId());
        }

        Shard shard = shardFactory.create(
                cmd.getId(),
                cmd.getServiceId(),
                cmd.getHost(),
                cmd.getPort()
        );

        shardRepository.save(shard);

        return shardMapper.toDto(shard);
    }
}
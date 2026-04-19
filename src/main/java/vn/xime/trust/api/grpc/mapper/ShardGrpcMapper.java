package vn.xime.trust.api.grpc.mapper;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.response.ShardDto;

@Component
public class ShardGrpcMapper {

    public vn.xime.trust.grpc.internal.shard.ShardDto toProto(ShardDto dto) {
        return vn.xime.trust.grpc.internal.shard.ShardDto.newBuilder()
                .setId(dto.getId())
                .setServiceId(dto.getServiceId())
                .setHost(dto.getHost())
                .setPort(dto.getPort())
                .setStatus(dto.getStatus())
                .setCreatedAt(dto.getCreatedAt().toEpochMilli()) // ⚠️ nếu sau này đổi sang Timestamp thì sửa ở đây
                .build();
    }
}
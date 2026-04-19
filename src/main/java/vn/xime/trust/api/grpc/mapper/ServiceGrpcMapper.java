package vn.xime.trust.api.grpc.mapper;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.response.ServiceDto;

@Component
public class ServiceGrpcMapper {

    public vn.xime.trust.grpc.internal.service.ServiceDto toProto(ServiceDto dto) {
        return vn.xime.trust.grpc.internal.service.ServiceDto.newBuilder()
                .setId(dto.getId())
                .setName(dto.getName())
                .setTenant(dto.getTenant() == null ? "" : dto.getTenant()) // xử lý null ở đây
                .setStatus(dto.getStatus())
                .setCreatedAt(dto.getCreatedAt().toEpochMilli()) // ⚠️ sau này đổi Timestamp thì sửa tại đây
                .build();
    }
}
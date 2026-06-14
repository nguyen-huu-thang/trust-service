package vn.xime.trust.api.grpc.mapper;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.response.AdminKeyDto;
import vn.xime.trust.grpc.internal.key.KeyDto;


@Component
public class KeyGrpcMapper {

    public KeyDto toProto(AdminKeyDto dto) {
        return KeyDto.newBuilder()
                .setId(dto.getId())
                .setSignerServiceId(dto.getSignerServiceId())
                .setVerifierServiceId(dto.getVerifierServiceId())
                .setAlgorithm(dto.getAlgorithm())
                .setKeySize(dto.getKeySize())
                .setActivateAt(dto.getActivateAt().toEpochMilli())
                .setExpiresAt(dto.getExpiresAt().toEpochMilli())
                .setIsDeleted(dto.isDeleted())
                .build();
    }
}

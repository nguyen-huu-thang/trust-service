package vn.xime.trust.api.grpc.mapper;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.response.KeyPolicyDto;

@Component
public class KeyPolicyGrpcMapper {

    public vn.xime.trust.grpc.internal.keypolicy.KeyPolicyDto toProto(KeyPolicyDto dto) {
        return vn.xime.trust.grpc.internal.keypolicy.KeyPolicyDto.newBuilder()
                .setId(dto.getId())
                .setSignerServiceId(dto.getSignerServiceId())
                .setVerifierServiceId(dto.getVerifierServiceId())
                .setAlgorithm(dto.getAlgorithm())
                .setKeySize(dto.getKeySize())
                .setKeyLifetimeSeconds(dto.getKeyLifetimeSec())
                .setRotationIntervalSeconds(dto.getRotationIntervalSeconds())
                .setPreloadSeconds(dto.getPreloadSec())
                .setCreatedAt(dto.getCreatedAt().toEpochMilli())
                .setUpdatedAt(
                        dto.getUpdatedAt() != null
                                ? dto.getUpdatedAt().toEpochMilli()
                                : 0
                )
                .build();
    }
}
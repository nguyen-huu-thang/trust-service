package vn.xime.trust.api.grpc.mapper;

import org.springframework.stereotype.Component;
import io.grpc.Status;
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
                .build();
    }

    // ==================================================
    // ERROR MAPPER
    // ==================================================

    public RuntimeException toStatus(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException();
        }

        if (e instanceof IllegalStateException) {
            return Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException();
        }

        return Status.INTERNAL
                .withDescription(e.getMessage())
                .asRuntimeException();
    }
}

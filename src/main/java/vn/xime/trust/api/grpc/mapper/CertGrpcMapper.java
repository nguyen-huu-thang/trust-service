package vn.xime.trust.api.grpc.mapper;

import org.springframework.stereotype.Component;

import vn.xime.trust.application.dto.response.AdminCertDto;
import vn.xime.trust.application.dto.response.BootstrapDto;
import vn.xime.trust.application.dto.response.CertRefreshTokenDto;
import vn.xime.trust.grpc.internal.cert.*;

@Component
public class CertGrpcMapper {

    // ==================================================
    // CERTIFICATE
    // ==================================================

    public CertificateResponse toProto(AdminCertDto dto) {
        return CertificateResponse.newBuilder()
                .setId(dto.getId())
                .setServiceId(dto.getServiceId())
                .setPublicCert(dto.getPublicCert())
                .setIssuedAt(dto.getIssuedAt().toEpochMilli())
                .setExpiresAt(dto.getExpiresAt().toEpochMilli())
                .setStatus(dto.getStatus().name())
                .setDeleted(dto.isDeleted())
                .build();
    }

    // ==================================================
    // BOOTSTRAP
    // ==================================================

    public BootstrapCertResponse toProto(BootstrapDto dto) {
        return BootstrapCertResponse.newBuilder()
                .setCertificate(
                        CertificateResponse.newBuilder()
                                .setId(dto.getIdCert())
                                .setServiceId(dto.getServiceId())
                                .setPublicCert(dto.getPublicCert())
                                .setPrivateKey(dto.getPrivateKey())
                                .setIssuedAt(dto.getIssuedAt().toEpochMilli())
                                .setExpiresAt(dto.getExpiresAt().toEpochMilli())
                                .setStatus("ACTIVE") // bootstrap luôn active
                                .setDeleted(false)
                                .build()
                )
                .setTokenId(dto.getIdToken())
                .setRefreshToken(dto.getToken())
                .build();
    }

    // ==================================================
    // TOKEN
    // ==================================================

    public TokenResponse toProto(CertRefreshTokenDto dto) {

        long usedAt = dto.getUsedAt() != null
                ? dto.getUsedAt().toEpochMilli()
                : 0L;

        return TokenResponse.newBuilder()
                .setId(dto.getId())
                .setIsBootstrap(dto.isBootstrap())
                .setIssuedAt(dto.getIssuedAt().toEpochMilli())
                .setExpiresAt(dto.getExpiresAt().toEpochMilli())
                .setUsedAt(usedAt)
                .setDeleted(dto.isDeleted())
                .build();
    }
}
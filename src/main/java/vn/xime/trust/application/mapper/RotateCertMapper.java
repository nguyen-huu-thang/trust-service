package vn.xime.trust.application.mapper;

import org.springframework.stereotype.Component;

import vn.xime.trust.application.dto.response.RotateCertDto;

import vn.xime.trust.domain.model.Certificate;

@Component
public class RotateCertMapper {
    
    public RotateCertDto toDto(Certificate targetCert, String privateKey, String idPlayload, String token) {
        return new RotateCertDto(
            targetCert.getId().toString(),
            targetCert.getPublicCert(),
            privateKey,
            idPlayload,
            token,
            targetCert.getIssuedAt(),
            targetCert.getExpiresAt()
        );
    }
}

package vn.xime.trust.application.mapper;

import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.application.dto.response.CertificateResponseDto;

public class CertificateMapper {
    
    public CertificateResponseDto toResponseDto(Certificate cert, String decryptedPrivateKey) {
        return new CertificateResponseDto(
                cert.getId(),
                cert.getServiceId(),
                cert.getKeyId(),
                cert.getAlgorithm().name(),
                cert.getKeySize(),
                decryptedPrivateKey,
                cert.getActivateAt(),
                cert.getExpiresAt()
        );
    }
}
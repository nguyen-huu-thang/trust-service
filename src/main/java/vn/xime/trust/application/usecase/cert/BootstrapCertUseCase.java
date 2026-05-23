package vn.xime.trust.application.usecase.cert;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.service.CertificateSelectionService;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.repository.ShardRepository;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.domain.repository.CertRefreshTokenRepository;
import vn.xime.trust.application.service.cert.GenerateCertificate;
import vn.xime.trust.application.service.cert.GenerateRefreshToken;
import vn.xime.trust.application.mapper.BootstrapMapper;
import vn.xime.trust.application.dto.request.BootstrapCommand;
import vn.xime.trust.application.dto.response.BootstrapDto;
import vn.xime.trust.application.dto.response.TokenDto;
import vn.xime.trust.application.port.out.KeyEncryptionService;


@Component
@RequiredArgsConstructor
public class BootstrapCertUseCase {

    private final CertificateSelectionService certificateSelectionService;
    private final ServiceRepository serviceRepository;
    private final ShardRepository shardRepository;
    private final CertificateRepository certificateRepository;
    private final CertRefreshTokenRepository certRefreshTokenRepository;
    private final GenerateCertificate generateCert;
    private final GenerateRefreshToken generateRefreshToken;
    private final BootstrapMapper mapper;
    private final KeyEncryptionService encryptionService;


    public BootstrapDto execute(BootstrapCommand cmd) {

        Service service = serviceRepository.findById(cmd.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (!service.isActive()) {
            throw new RuntimeException("Service is not active");
        }

        Shard shard = shardRepository.findById(cmd.getShardId())
                .orElseThrow(() ->
                        new IllegalStateException("No shard for token")
                );
        
        if (!shard.isActive()) {
            throw new IllegalStateException("Shard is not active");
        }

        if (!Objects.equals(shard.getServiceId(), service.getId())) {
            throw new IllegalStateException("Shard does not belong to service");
        }

        List<Certificate> certs = certificateRepository.findByServiceId(cmd.getServiceId());

        Certificate cert;

        try {
            cert = certificateSelectionService.getCurrentCertificate(certs, Instant.now());
        }
        catch (IllegalStateException e) {
            for (Certificate c : certs) {
                c = c.markDeleted();
                certificateRepository.save(c);
            }
            List <CertRefreshToken> certRefresh = certRefreshTokenRepository.findByServiceId(cmd.getServiceId());
            for (CertRefreshToken c : certRefresh) {
                c = c.markDeleted();
                certRefreshTokenRepository.save(c);
            }
            cert = generateCert.serviceBootstrap(cmd.getServiceId());
        }

        List <CertRefreshToken> certRefresh = certRefreshTokenRepository.findByShardId(cmd.getShardId());
        for (CertRefreshToken c : certRefresh) {
            c = c.markDeleted();
            certRefreshTokenRepository.save(c);
        }

        TokenDto newToken = generateRefreshToken.execute(
                cmd.getServiceId(),
                cmd.getShardId(),
                cert,
                true
        );

        return mapper.toDto(cert, encryptionService.decrypt(cert.getPrivateKeyEncrypted()), newToken);
    }
}

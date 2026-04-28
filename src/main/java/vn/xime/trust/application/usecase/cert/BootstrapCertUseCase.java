package vn.xime.trust.application.usecase.cert;

import java.util.List;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.repository.ShardRepository;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.application.mapper.BootstrapMapper;
import vn.xime.trust.application.dto.request.BootstrapCommand;
import vn.xime.trust.application.dto.response.BootstrapDto;
import vn.xime.trust.application.dto.response.TokenDto;
import vn.xime.trust.application.port.out.KeyEncryptionService;


@Component
@RequiredArgsConstructor
public class BootstrapCertUseCase {

    private final ServiceRepository serviceRepository;
    private final ShardRepository shardRepository;
    private final CertificateRepository certificateRepository;
    private final GenerateCertificateUseCase generateCert;
    private final GenerateRefreshTokenUseCase generateRefreshToken;
    private final BootstrapMapper mapper;
    private final KeyEncryptionService encryptionService;

    // bước 1: kiểm tra service có tồn tại hay không
    // bước 2: lấy tất cả các cert của service (nếu có)
    // bước 3: vô hiệu hóa tất cả các cert trên (nếu có)
    // bước 4: tạo mới 1 cert bootstrap có thời hạn ngắn.
    // bước 5: tạo mới 1 cert refresh token bootstrap
    // bước 6: trả về cert + token cho client
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

        if (shard.getServiceId() != service.getId()) {
            throw new IllegalStateException("Shard does not belong to service");
        }

        List<Certificate> certs = certificateRepository.findByServiceId(cmd.getServiceId());

        if (certs != null && !certs.isEmpty()) {
            for (Certificate cert : certs) {
                cert.markDeleted();
                certificateRepository.save(cert);
            }
        }

        Certificate newCert = generateCert.serviceBootstrap(cmd.getServiceId());

        TokenDto newToken = generateRefreshToken.execute(
                cmd.getServiceId(),
                cmd.getShardId(),
                newCert
        );

        return mapper.toDto(newCert, encryptionService.decrypt(newCert.getPrivateKeyEncrypted()), newToken.getRawToken());
    }
}

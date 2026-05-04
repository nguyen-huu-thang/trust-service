package vn.xime.trust.application.usecase.cert;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;


import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.model.TokenPayload;
import vn.xime.trust.domain.policy.CertificateIssuancePolicy;
import vn.xime.trust.domain.repository.CertRefreshTokenRepository;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.repository.ShardRepository;
import vn.xime.trust.domain.service.CertRefreshTokenDomainService;
import vn.xime.trust.domain.service.CertificateSelectionService;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.application.dto.response.RotateCertDto;
import vn.xime.trust.application.dto.response.TokenDto;
import vn.xime.trust.application.port.out.TokenCodec;
import vn.xime.trust.application.port.out.KeyEncryptionService;
import vn.xime.trust.application.mapper.RotateCertMapper;



@Component
public class RotateCertificateUseCase {

    private final CertificateRepository certificateRepository;
    private final CertRefreshTokenRepository tokenRepository;
    private final ServiceRepository serviceRepository;
    private final ShardRepository shardRepository;

    private final CertificateSelectionService selectionService;
    private final CertRefreshTokenDomainService tokenDomainService;

    private final GenerateCertificateUseCase generateCert;

    private final CertificateIssuancePolicy issuancePolicy;

    private final RotateCertMapper mapper;

    private final GenerateRefreshTokenUseCase generateRefreshToken;
    private final TokenCodec tokenCodec;
    private final KeyEncryptionService encryptionService;

    public RotateCertificateUseCase(
        CertificateRepository certificateRepository,
        CertRefreshTokenRepository tokenRepository,
        ServiceRepository serviceRepository,
        ShardRepository shardRepository,
        CertificateSelectionService selectionService,
        CertRefreshTokenDomainService tokenDomainService,
        GenerateCertificateUseCase generateCert,
        CertificateIssuancePolicy issuancePolicy,
        RotateCertMapper mapper,
        GenerateRefreshTokenUseCase generateRefreshToken,
        TokenCodec tokenCodec,
        KeyEncryptionService encryptionService
    ) {
        this.certificateRepository = certificateRepository;
        this.tokenRepository = tokenRepository;
        this.serviceRepository = serviceRepository;
        this.shardRepository = shardRepository;
        this.selectionService = selectionService;
        this.tokenDomainService = tokenDomainService;
        this.issuancePolicy = issuancePolicy;
        this.mapper = mapper;
        this.tokenCodec = tokenCodec;
        this.encryptionService = encryptionService;
        this.generateCert = generateCert;
        this.generateRefreshToken = generateRefreshToken;
    }

    // =========================
    // EXECUTE
    // =========================

    public RotateCertDto execute(
        String tokenId,
        String token,
        String privateKeyCert
    ) {
        Objects.requireNonNull(tokenId);
        Objects.requireNonNull(token);
        Objects.requireNonNull(privateKeyCert);

        Instant now = Instant.now();

        // =========================
        // 1. LOAD TOKEN
        // =========================

        CertRefreshToken tokenRecord = tokenRepository.findById(IdService.fromString(tokenId))
                .orElseThrow(() ->
                        new IllegalStateException("No record for token id")
                );

        
        // =========================
        // 2. TOKEN -> PAYLOAD
        // =========================

        TokenPayload payload = tokenCodec.decode(token);
        
        // =========================
        // 3. VALIDATE TOKEN
        // =========================

        if (!tokenRecord.isValid(now)) {
            throw new IllegalStateException("Token is not valid");
        }

        tokenDomainService.validateToken(tokenCodec.hash(token), tokenRecord.getTokenHash());

        Service service = serviceRepository.findById(payload.getServiceId())
                .orElseThrow(() ->
                        new IllegalStateException("No service for token")
                );

        if (!service.isActive()) {
            throw new IllegalStateException("Service is not active");
        }

        Shard shard = shardRepository.findById(payload.getShardId())
                .orElseThrow(() ->
                        new IllegalStateException("No shard for token")
                );
        
        if (!shard.isActive()) {
            throw new IllegalStateException("Shard is not active");
        }

        if (!Objects.equals(shard.getServiceId(), service.getId())) {
            throw new IllegalStateException("Shard does not belong to service");
        }

        Certificate currentCert = certificateRepository.findById(IdService.fromString(payload.getCertId()))
                .orElseThrow(() ->
                        new IllegalStateException("No certificate for token")
                );
        
        issuancePolicy.ensureCanRotate(currentCert, now);

        tokenDomainService.validateCert(encryptionService.decrypt(currentCert.getPrivateKeyEncrypted()), privateKeyCert);

        // =========================
        // 4. LOAD CERTS
        // =========================

        List<Certificate> certs = certificateRepository.findByServiceId(payload.getServiceId());

        if (certs.isEmpty()) {
            throw new IllegalStateException("No certificate for service");
        }

        // latest cert (mới nhất)
        Certificate latestCert = selectionService.findLatestCertificate(certs).orElseThrow();

        // =========================
        // 5. DECIDE CERT
        // =========================

        Certificate targetCert;

        if (issuancePolicy.canReuseCurrentCertificate(latestCert, now)) {
            // 🔥 tái sử dụng cert đã tồn tại
            targetCert = latestCert;

        } else {
            // 🔥 phát hành cert mới

            Instant expiresAt = issuancePolicy.calculateExpiresAt(now);

            targetCert = generateCert.rotateCert(payload.getServiceId(), expiresAt);

            certificateRepository.save(targetCert);
        }

        // =========================
        // 6. MARK USED TOKENS
        // =========================

        CertRefreshToken usedToken = tokenRecord.markUsed(now);
        tokenRepository.save(usedToken);

        // =========================
        // 7. ISSUE NEW TOKEN
        // =========================

        TokenDto newToken = generateRefreshToken.execute(
                payload.getServiceId(),
                payload.getShardId(),
                targetCert,
                false
        );

        // =========================
        // RESULT
        // =========================

        return mapper.toDto(targetCert, encryptionService.decrypt(targetCert.getPrivateKeyEncrypted()), newToken.getPayload().getTokenId(), newToken.getRawToken());
    }
}
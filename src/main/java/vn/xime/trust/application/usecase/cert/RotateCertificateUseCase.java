package vn.xime.trust.application.usecase.cert;

import vn.xime.trust.domain.factory.CertRefreshTokenFactory;
import vn.xime.trust.domain.factory.CertificateFactory;
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
import vn.xime.trust.application.port.out.TokenCodec;
import vn.xime.trust.application.port.out.KeyEncryptionService;
import vn.xime.trust.application.mapper.RotateCertMapper;



import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Rotate Certificate UseCase
 *
 * Flow:
 * - validate refresh token
 * - check current cert
 * - decide reuse / issue new cert
 * - consume token
 * - issue new refresh token
 */
public class RotateCertificateUseCase {

    private final CertificateRepository certificateRepository;
    private final CertRefreshTokenRepository tokenRepository;
    private final ServiceRepository serviceRepository;
    private final ShardRepository shardRepository;

    private final CertificateSelectionService selectionService;
    private final CertRefreshTokenDomainService tokenDomainService;

    private final CertificateFactory certificateFactory;
    private final CertRefreshTokenFactory tokenFactory;

    private final CertificateIssuancePolicy issuancePolicy;

    private final RotateCertMapper mapper;

    private final TokenCodec tokenCodec;
    private final KeyEncryptionService encryptionService;

    public RotateCertificateUseCase(
        CertificateRepository certificateRepository,
        CertRefreshTokenRepository tokenRepository,
        ServiceRepository serviceRepository,
        ShardRepository shardRepository,
        CertificateSelectionService selectionService,
        CertRefreshTokenDomainService tokenDomainService,
        CertificateFactory certificateFactory,
        CertRefreshTokenFactory tokenFactory,
        CertificateIssuancePolicy issuancePolicy,
        RotateCertMapper mapper,
        TokenCodec tokenCodec,
        KeyEncryptionService encryptionService
    ) {
        this.certificateRepository = certificateRepository;
        this.tokenRepository = tokenRepository;
        this.serviceRepository = serviceRepository;
        this.shardRepository = shardRepository;
        this.selectionService = selectionService;
        this.tokenDomainService = tokenDomainService;
        this.certificateFactory = certificateFactory;
        this.tokenFactory = tokenFactory;
        this.issuancePolicy = issuancePolicy;
        this.mapper = mapper;
        this.tokenCodec = tokenCodec;
        this.encryptionService = encryptionService;
    }

    // =========================
    // EXECUTE
    // =========================

    public RotateCertDto execute(
        String id,
        String token,
        String privateKeyCert
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(token);
        Objects.requireNonNull(privateKeyCert);

        Instant now = Instant.now();

        // =========================
        // 1. LOAD TOKEN
        // =========================

        CertRefreshToken tokenRecord = tokenRepository.findById(IdService.fromString(id))
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

        if (shard.getServiceId() != service.getId()) {
            throw new IllegalStateException("Shard does not belong to service");
        }

        Certificate currentCert = certificateRepository.findById(IdService.fromString(payload.getCertId()))
                .orElseThrow(() ->
                        new IllegalStateException("No certificate for token")
                );
        
        issuancePolicy.ensureCanRotate(currentCert, now);

        tokenDomainService.validateCert(currentCert, privateKeyCert);

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
            // 🔥 reuse cert đã tồn tại
            targetCert = latestCert;

        } else {
            // 🔥 issue cert mới

            Instant expiresAt = issuancePolicy.calculateExpiresAt(now);

            // ⚠️ TODO: generate real cert + encrypt key
            String publicCert = "TODO_PUBLIC_CERT";
            String privateKeyEncrypted = "TODO_PRIVATE_KEY";

            targetCert = certificateFactory.create(
                    payload.getServiceId(),
                    publicCert,
                    privateKeyEncrypted,
                    expiresAt
            );

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

        Instant tokenExpiresAt = targetCert.getExpiresAt();

        CertRefreshToken newToken = tokenFactory.create(
                false,
                tokenExpiresAt
        );

        TokenPayload newPayload = new TokenPayload(
                IdService.toString(newToken.getId()),
                payload.getServiceId(),
                payload.getShardId(),
                targetCert.getId().toString(),
                newToken.getIssuedAt().toEpochMilli(),
                tokenExpiresAt.toEpochMilli()
        );

        String newRawToken = tokenCodec.encode(newPayload);

        String newTokenHash = tokenCodec.hash(newRawToken);

        newToken = newToken.markTokenHash(newTokenHash);

        tokenRepository.save(newToken);

        // =========================
        // RESULT
        // =========================

        return mapper.toDto(targetCert, encryptionService.decrypt(targetCert.getPrivateKeyEncrypted()), IdService.toString(newToken.getId()), newRawToken);
    }
}
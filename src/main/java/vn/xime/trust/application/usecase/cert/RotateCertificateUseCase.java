package vn.xime.trust.application.usecase.cert;

import vn.xime.trust.domain.factory.CertRefreshTokenFactory;
import vn.xime.trust.domain.factory.CertificateFactory;
import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.policy.CertificateIssuancePolicy;
import vn.xime.trust.domain.repository.CertRefreshTokenRepository;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.domain.service.CertRefreshTokenDomainService;
import vn.xime.trust.domain.service.CertificateSelectionService;

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

    private final CertificateSelectionService selectionService;
    private final CertRefreshTokenDomainService tokenDomainService;

    private final CertificateFactory certificateFactory;
    private final CertRefreshTokenFactory tokenFactory;

    private final CertificateIssuancePolicy issuancePolicy;

    public RotateCertificateUseCase(
            CertificateRepository certificateRepository,
            CertRefreshTokenRepository tokenRepository,
            CertificateSelectionService selectionService,
            CertRefreshTokenDomainService tokenDomainService,
            CertificateFactory certificateFactory,
            CertRefreshTokenFactory tokenFactory,
            CertificateIssuancePolicy issuancePolicy
    ) {
        this.certificateRepository = certificateRepository;
        this.tokenRepository = tokenRepository;
        this.selectionService = selectionService;
        this.tokenDomainService = tokenDomainService;
        this.certificateFactory = certificateFactory;
        this.tokenFactory = tokenFactory;
        this.issuancePolicy = issuancePolicy;
    }

    // =========================
    // EXECUTE
    // =========================

    public Result execute(
            String serviceId,
            String rawToken,
            String tokenHash,          // hash(rawToken)
            String shardId             // lấy từ payload token
    ) {

        Objects.requireNonNull(serviceId);
        Objects.requireNonNull(rawToken);
        Objects.requireNonNull(tokenHash);
        Objects.requireNonNull(shardId);

        Instant now = Instant.now();

        // =========================
        // 1. LOAD TOKEN
        // =========================

        CertRefreshToken token = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() ->
                        new IllegalStateException("Refresh token not found")
                );

        // =========================
        // 2. LOAD CERTS
        // =========================

        List<Certificate> certs =
                certificateRepository.findByServiceId(serviceId);

        if (certs.isEmpty()) {
            throw new IllegalStateException("No certificate for service");
        }

        // current cert (đang dùng mTLS)
        Certificate currentCert =
                selectionService.getCurrentCertificate(certs, now);

        // latest cert (mới nhất)
        Certificate latestCert =
                selectionService.findLatestCertificate(certs)
                        .orElseThrow();

        // =========================
        // 3. VALIDATE TOKEN
        // =========================

        tokenDomainService.validateToken(
                token,
                serviceId,
                currentCert.getId(),
                now
        );

        // =========================
        // 4. VALIDATE CURRENT CERT
        // =========================

        issuancePolicy.ensureCanRotate(currentCert, now);

        // =========================
        // 5. DECIDE CERT
        // =========================

        Certificate targetCert;

        if (issuancePolicy.canReuseCurrentCertificate(latestCert, now)) {
            // 🔥 reuse cert đã tồn tại
            targetCert = latestCert;

        } else {
            // 🔥 issue cert mới

            Instant expiresAt =
                    issuancePolicy.calculateExpiresAt(now);

            // ⚠️ TODO: generate real cert + encrypt key
            String publicCert = "TODO_PUBLIC_CERT";
            String privateKeyEncrypted = "TODO_PRIVATE_KEY";

            targetCert = certificateFactory.create(
                    serviceId,
                    publicCert,
                    privateKeyEncrypted,
                    expiresAt
            );

            certificateRepository.save(targetCert);
        }

        // =========================
        // 6. CONSUME TOKEN (ONE-TIME)
        // =========================

        CertRefreshToken usedToken =
                tokenDomainService.validateAndConsume(
                        token,
                        serviceId,
                        currentCert.getId(),
                        now
                );

        tokenRepository.save(usedToken);

        // =========================
        // 7. ISSUE NEW TOKEN
        // =========================

        Instant tokenExpiresAt = targetCert.getExpiresAt();

        String newRawToken = generateRawToken(
                serviceId,
                shardId,
                targetCert.getId(),
                tokenExpiresAt
        );

        String newTokenHash = hash(newRawToken);

        CertRefreshToken newToken = tokenFactory.create(
                serviceId,
                newTokenHash,
                targetCert.getId(),
                tokenExpiresAt,
                "rotation"
        );

        tokenRepository.save(newToken);

        // =========================
        // RESULT
        // =========================

        return new Result(
                targetCert,
                newRawToken,
                tokenExpiresAt
        );
    }

    // =========================
    // TOKEN UTILS (TEMP)
    // =========================

    private String generateRawToken(
            String serviceId,
            String shardId,
            Id certId,
            Instant expiresAt
    ) {
        // TODO: replace bằng TokenService (HMAC JWT-like)
        return serviceId + "." + shardId + "." + certId;
    }

    private String hash(String token) {
        // TODO: replace bằng SHA-256
        return Integer.toHexString(token.hashCode());
    }

    // =========================
    // RESULT DTO
    // =========================

    public record Result(
            Certificate certificate,
            String refreshToken,
            Instant expiresAt
    ) {
    }
}
package vn.xime.trust.application.usecase.cert;

import vn.xime.trust.domain.factory.CertRefreshTokenFactory;
import vn.xime.trust.domain.factory.IdFactory;
import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.repository.CertRefreshTokenRepository;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.domain.service.CertificateSelectionService;
import vn.xime.trust.domain.service.CertificateValidationService;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Generate Refresh Token
 *
 * Dùng cho:
 * - bootstrap
 * - cấp lại token cho shard
 *
 * KHÔNG tạo cert mới
 * chỉ bind token với cert hiện tại
 */
public class GenerateRefreshTokenUseCase {

    private final CertificateRepository certificateRepository;
    private final CertRefreshTokenRepository tokenRepository;

    private final CertificateSelectionService selectionService;
    private final CertificateValidationService validationService;

    private final CertRefreshTokenFactory tokenFactory;
    private final TokenService tokenService;

    public GenerateRefreshTokenUseCase(
            CertificateRepository certificateRepository,
            CertRefreshTokenRepository tokenRepository,
            CertificateSelectionService selectionService,
            CertificateValidationService validationService,
            CertRefreshTokenFactory tokenFactory,
            TokenService tokenService
    ) {
        this.certificateRepository = certificateRepository;
        this.tokenRepository = tokenRepository;
        this.selectionService = selectionService;
        this.validationService = validationService;
        this.tokenFactory = tokenFactory;
        this.tokenService = tokenService;
    }

    // =========================
    // EXECUTE
    // =========================

    public Result execute(
            String serviceId,
            String shardId,
            String issuedBy
    ) {

        Objects.requireNonNull(serviceId, "serviceId is required");
        Objects.requireNonNull(shardId, "shardId is required");

        Instant now = Instant.now();

        // =========================
        // 1. LOAD CERTS
        // =========================

        List<Certificate> certs = certificateRepository.findByServiceId(serviceId);

        if (certs.isEmpty()) {
            throw new IllegalStateException("No certificate for service");
        }

        // =========================
        // 2. SELECT CURRENT CERT
        // =========================

        Certificate currentCert = selectionService.getCurrentCertificate(certs, now);

        // validate cert usable
        validationService.validateActive(currentCert, now);

        // =========================
        // 3. GENERATE TOKEN
        // =========================

        String tokenId = IdFactory.generate().toString();

        Instant expiresAt = currentCert.getExpiresAt(); // align với cert

        String rawToken = tokenService.generate(
                tokenId,
                serviceId,
                shardId,
                currentCert.getId(),
                expiresAt
        );

        String tokenHash = tokenService.hash(rawToken);

        // =========================
        // 4. SAVE TOKEN
        // =========================

        CertRefreshToken token = tokenFactory.create(
                serviceId,
                tokenHash,
                currentCert.getId(),
                expiresAt,
                issuedBy != null ? issuedBy : "system"
        );

        tokenRepository.save(token);

        // =========================
        // RESULT
        // =========================

        return new Result(
                rawToken,
                expiresAt,
                currentCert
        );
    }


    // =========================
    // RESULT DTO
    // =========================

    public record Result(
            String refreshToken,
            Instant expiresAt,
            Certificate certificate
    ) {
    }
}
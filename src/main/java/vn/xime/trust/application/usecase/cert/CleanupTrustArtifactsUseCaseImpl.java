package vn.xime.trust.application.usecase.cert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.port.in.CleanupExpiredCertificatesUseCase;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.domain.repository.CertRefreshTokenRepository;
import vn.xime.trust.domain.service.CertificateLifecycleService;
import vn.xime.trust.domain.service.CertRefreshTokenDomainService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanupTrustArtifactsUseCaseImpl implements CleanupExpiredCertificatesUseCase {

    private final CertificateRepository certificateRepository;
    private final CertRefreshTokenRepository tokenRepository;

    private final CertificateLifecycleService certLifecycle;
    private final CertRefreshTokenDomainService tokenLifecycle;

    @Override
    @Transactional
    public void execute() {

        Instant now = Instant.now();

        // =========================
        // CERTIFICATE CLEANUP
        // =========================

        cleanupCertificates(now);

        // =========================
        // TOKEN CLEANUP
        // =========================

        cleanupTokens(now);
    }

    // =========================================================
    // CERTIFICATE
    // =========================================================

    private void cleanupCertificates(Instant now) {

        // -------- SOFT DELETE --------
        List<Certificate> activeCerts = certificateRepository.findAllNotDeleted();

        List<Certificate> toSoftDelete = new ArrayList<>();

        for (Certificate cert : activeCerts) {
            try {
                if (certLifecycle.shouldBeDeleted(cert, now)) {
                    toSoftDelete.add(cert.markDeleted());
                }
            } catch (Exception e) {
                log.error(
                        "Failed soft-delete cert id={} service={}",
                        cert.getId(),
                        cert.getServiceId(),
                        e
                );
            }
        }

        for (Certificate cert : toSoftDelete) {
            certificateRepository.save(cert);
        }

        // -------- HARD DELETE --------
        List<Certificate> deletedCerts = certificateRepository.findAllDeleted();

        List<Certificate> toHardDelete = new ArrayList<>();

        for (Certificate cert : deletedCerts) {
            try {
                if (certLifecycle.shouldBeHardDeleted(cert, now)) {
                    toHardDelete.add(cert);
                }
            } catch (Exception e) {
                log.error(
                        "Failed hard-delete cert id={} service={}",
                        cert.getId(),
                        cert.getServiceId(),
                        e
                );
            }
        }

        if (!toHardDelete.isEmpty()) {
            certificateRepository.deleteAllByIds(
                    toHardDelete.stream()
                            .map(Certificate::getId)
                            .toList()
            );

            log.info("Hard deleted {} certificates", toHardDelete.size());
        }
    }

    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    private void cleanupTokens(Instant now) {

        // -------- SOFT DELETE --------
        List<CertRefreshToken> activeTokens = tokenRepository.findAllNotDeleted();

        List<CertRefreshToken> toSoftDelete = new ArrayList<>();

        for (CertRefreshToken token : activeTokens) {
            try {
                if (tokenLifecycle.shouldBeDeleted(token, now)) {
                    toSoftDelete.add(token.markDeleted());
                }
            } catch (Exception e) {
                log.error(
                        "Failed soft-delete token id={}",
                        token.getId(),
                        e
                );
            }
        }

        for (CertRefreshToken token : toSoftDelete) {
            tokenRepository.save(token);
        }

        // -------- HARD DELETE --------
        List<CertRefreshToken> deletedTokens = tokenRepository.findAllDeleted();

        List<CertRefreshToken> toHardDelete = new ArrayList<>();

        for (CertRefreshToken token : deletedTokens) {
            try {
                if (tokenLifecycle.shouldBeHardDeleted(token, now)) {
                    toHardDelete.add(token);
                }
            } catch (Exception e) {
                log.error(
                        "Failed hard-delete token id={}",
                        token.getId(),
                        e
                );
            }
        }

        if (!toHardDelete.isEmpty()) {
            tokenRepository.deleteAllByIds(
                    toHardDelete.stream()
                            .map(CertRefreshToken::getId)
                            .toList()
            );

            log.info("Hard deleted {} refresh tokens", toHardDelete.size());
        }
    }
}
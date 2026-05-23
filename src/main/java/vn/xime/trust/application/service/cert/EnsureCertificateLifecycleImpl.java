package vn.xime.trust.application.service.cert;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import vn.xime.trust.application.port.in.EnsureCertificateLifecycle;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.policy.CertificateIssuancePolicy;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.service.CertificateSelectionService;

import java.time.Instant;
import java.util.List;

/**
 * Ensure Certificate Lifecycle
 *
 * Scheduler entry point:
 * - đảm bảo mỗi service luôn có cert hợp lệ
 * - tự động rotate theo policy của cert
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EnsureCertificateLifecycleImpl implements EnsureCertificateLifecycle {

    private final ServiceRepository serviceRepository;
    private final CertificateRepository certificateRepository;
    private final GenerateCertificate generateCert;
    private final CertificateSelectionService selectionService;
    private final CertificateIssuancePolicy issuancePolicy;

    @Override
    public void execute() {

        Instant now = Instant.now();

        List<String> serviceIds = serviceRepository.findAllActiveServices()
                .stream()
                .map(s -> s.getId())
                .toList();

        for (String serviceId : serviceIds) {

            try {
                processService(serviceId, now);

            } catch (Exception e) {
                log.error("Failed to ensure cert lifecycle for service={}", serviceId, e);
            }
        }
    }

    // =========================
    // PROCESS ONE SERVICE
    // =========================

    private void processService(String serviceId, Instant now) {

        List<Certificate> certs = certificateRepository.findByServiceId(serviceId);

        Certificate latest = selectionService
                .findLatestCertificate(certs)
                .orElse(null);

        // =========================
        // DECIDE
        // =========================

        if (!issuancePolicy.shouldIssueNewCertificate(latest, now)) {
            return;
        }

        // =========================
        // ISSUE NEW CERT
        // =========================

        Instant expiresAt = issuancePolicy.calculateExpiresAt(now);

        Certificate newCert = generateCert.rotateCert(serviceId, expiresAt);

        try {
            certificateRepository.save(newCert);

            log.info(
                    "Issued new certificate for service={} certId={}",
                    serviceId,
                    newCert.getId()
            );

        } catch (Exception e) {

            // =========================
            // MULTI-INSTANCE SAFE
            // =========================

            // nếu instance khác đã tạo rồi → ignore
            log.warn(
                    "Cert may already be created concurrently for service={}",
                    serviceId
            );
        }
    }
}
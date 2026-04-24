package vn.xime.trust.application.port.out;

import java.time.Instant;

public interface CertGenerator {

    GeneratedCertificate generate(
            String serviceId,
            KeyPair keyPair,
            Instant expiresAt
    );

    record GeneratedCertificate(
            String publicCertPem,
            String privateKeyPem
    ) {}
}
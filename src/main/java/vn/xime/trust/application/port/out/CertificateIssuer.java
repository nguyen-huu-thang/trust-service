package vn.xime.trust.application.port.out;

import java.time.Instant;

/**
 * Port cấp chứng chỉ X.509 hoàn chỉnh.
 *
 * Application layer chỉ biết:
 *  - đưa input (service, public key, validity...)
 *  - nhận về certificate (encoded String)
 *
 * KHÔNG biết:
 *  - build TBS như thế nào
 *  - ký ra sao
 *  - dùng thư viện gì
 */
public interface CertificateIssuer {

    IssuedCertificate issue(IssueCommand command);

    /**
     * Input để cấp certificate
     */
    record IssueCommand(
            String serviceId,
            String spiffeId,
            String publicKey,
            Instant notBefore,
            Instant notAfter
    ) {}

    /**
     * Output của quá trình cấp cert
     */
    record IssuedCertificate(
            String certificate,   // Base64 hoặc PEM (tùy implementation)
            String serialNumber   // optional nhưng nên có cho audit/debug
    ) {}
}
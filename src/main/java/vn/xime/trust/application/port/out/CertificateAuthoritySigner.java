package vn.xime.trust.application.port.out;

import java.security.cert.X509Certificate;

/**
 * Abstraction cho CA signer.
 *
 * Application layer chỉ biết:
 *  - có thể ký dữ liệu
 *  - có thể lấy CA certificate (issuer)
 *
 * KHÔNG biết:
 *  - private key nằm ở đâu
 *  - có tồn tại private key hay không
 */
public interface CertificateAuthoritySigner {

    /**
     * Ký dữ liệu (thường là DER của TBSCertificate)
     *
     * @param data dữ liệu cần ký
     * @return chữ ký
     */
    byte[] sign(byte[] data);

    /**
     * Trả về CA certificate (issuer certificate)
     *
     * @return X509 CA certificate
     */
    X509Certificate getCaCertificate();
}
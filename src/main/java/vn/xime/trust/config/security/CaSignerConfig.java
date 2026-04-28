package vn.xime.trust.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.xime.trust.application.port.out.CertificateAuthoritySigner;
import vn.xime.trust.infrastructure.security.FileSystemCaSigner;
import vn.xime.trust.infrastructure.security.PemLoader;
import vn.xime.trust.infrastructure.security.SignatureService;


/**
 * Cấu hình CA Signer.
 *
 * Hiện tại:
 *  - DEV mode: load key từ file system (./dev-keys/)
 *
 * Tương lai:
 *  - Có thể thay bằng HsmCaSigner mà không ảnh hưởng application layer
 */
@Configuration
public class CaSignerConfig {

    @Bean
    public PemLoader pemLoader() {
        return new PemLoader();
    }

    @Bean
    public SignatureService signatureService() {
        return new SignatureService();
    }

    @Bean
    public CertificateAuthoritySigner caSigner(
            PemLoader pemLoader,
            SignatureService signatureService
    ) {
        // DEV: dùng file system
        return new FileSystemCaSigner(pemLoader, signatureService);

        // FUTURE:
        // return new HsmCaSigner(...);
    }
}
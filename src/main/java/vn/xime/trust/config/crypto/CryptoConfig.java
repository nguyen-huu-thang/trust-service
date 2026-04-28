package vn.xime.trust.config.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.xime.trust.application.port.out.KeyEncryptionService;
import vn.xime.trust.application.port.out.KeyGenerator;
import vn.xime.trust.application.port.out.CertificateIssuer;
import vn.xime.trust.application.port.out.CertificateAuthoritySigner;
import vn.xime.trust.application.port.out.TokenCodec;

import vn.xime.trust.infrastructure.crypto.AesKeyEncryptionService;
import vn.xime.trust.infrastructure.crypto.DefaultKeyGenerator;
import vn.xime.trust.infrastructure.crypto.HmacTokenCodec;
import vn.xime.trust.infrastructure.crypto.DefaultCertificateIssuer;
import vn.xime.trust.infrastructure.crypto.X509CertificateAssembler;
import vn.xime.trust.infrastructure.crypto.X509CertificateBuilder;
import vn.xime.trust.infrastructure.crypto.X509ExtensionsFactory;
import vn.xime.trust.infrastructure.crypto.X509NameFactory;



@Configuration
public class CryptoConfig {

    // =========================
    // Key Encryption
    // =========================

    @Bean
    public KeyEncryptionService keyEncryptionService(
            @Value("${key.encryption.secret}") String secret
    ) {
        return new AesKeyEncryptionService(secret);
    }

    // =========================
    // Key Generator
    // =========================

    @Bean
    public KeyGenerator keyGenerator() {
        return new DefaultKeyGenerator();
    }

    // =========================
    // Token
    // =========================

    @Bean
    public TokenCodec tokenCodec(@Value("${key.encryption.secret}") String secret) {
        return new HmacTokenCodec(secret);
    }

    // =========================
    // Certificate pipeline
    // =========================

    @Bean
    public X509NameFactory x509NameFactory() {
        return new X509NameFactory();
    }

    @Bean
    public X509ExtensionsFactory x509ExtensionsFactory() {
        return new X509ExtensionsFactory();
    }

    @Bean
    public X509CertificateBuilder x509CertificateBuilder(
            X509ExtensionsFactory extensionsFactory,
            X509NameFactory nameFactory
    ) {
        return new X509CertificateBuilder(
                extensionsFactory,
                nameFactory
        );
    }

    @Bean
    public X509CertificateAssembler x509CertificateAssembler() {
        return new X509CertificateAssembler();
    }

    // =========================
    // Certificate Issuer (MAIN)
    // =========================

    @Bean
    public CertificateIssuer certificateIssuer(
            CertificateAuthoritySigner caSigner,
            X509CertificateBuilder builder,
            X509CertificateAssembler assembler
    ) {
        return new DefaultCertificateIssuer(
                caSigner,
                builder,
                assembler
        );
    }
}
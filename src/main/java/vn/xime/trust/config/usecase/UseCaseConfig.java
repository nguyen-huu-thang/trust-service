package vn.xime.trust.config.usecase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.xime.trust.domain.factory.ServiceFactory;
import vn.xime.trust.domain.service.KeyLifecycleDomainService;
import vn.xime.trust.domain.factory.KeyFactory;
import vn.xime.trust.domain.factory.KeyPolicyFactory;
import vn.xime.trust.domain.factory.ShardFactory;
import vn.xime.trust.domain.factory.CertificateFactory;
import vn.xime.trust.domain.factory.CertRefreshTokenFactory;
import vn.xime.trust.domain.service.KeyValidationDomainService;
import vn.xime.trust.domain.service.KeyPolicyDomainService;
import vn.xime.trust.domain.service.CertificateSelectionService;
import vn.xime.trust.domain.service.CertificateValidationService;
import vn.xime.trust.domain.service.CertRefreshTokenDomainService;
import vn.xime.trust.domain.service.CertificateLifecycleService;
import vn.xime.trust.domain.policy.CertificateIssuancePolicy;



@Configuration
public class UseCaseConfig {

    @Bean
    public ServiceFactory serviceFactory() {
        return new ServiceFactory();
    }

    @Bean
    public KeyLifecycleDomainService keyLifecycleDomainService() {
        return new KeyLifecycleDomainService();
    }

    @Bean
    public KeyFactory keyFactory() {
        return new KeyFactory();
    }

    @Bean
    public KeyPolicyFactory keyPolicyFactory() {
        return new KeyPolicyFactory();
    }

    @Bean
    public ShardFactory shardFactory() {
        return new ShardFactory();
    }

    @Bean
    public KeyValidationDomainService keyValidationDomainService() {
        return new KeyValidationDomainService();
    }

    @Bean
    public KeyPolicyDomainService keyPolicyDomainService() {
        return new KeyPolicyDomainService();
    }

    @Bean
    public CertificateFactory certificateFactory() {
        return new CertificateFactory();
    }

    @Bean
    public CertRefreshTokenFactory certRefreshTokenFactory() {
        return new CertRefreshTokenFactory();
    }

    @Bean
    public CertificateSelectionService certificateSelectionService() {
        return new CertificateSelectionService();
    }

    @Bean
    public CertificateValidationService certificateValidationService() {
        return new CertificateValidationService();
    }

    @Bean
    public CertRefreshTokenDomainService certRefreshTokenDomainService() {
        return new CertRefreshTokenDomainService();
    }

    @Bean
    public CertificateLifecycleService certificateLifecycleService() {
        return new CertificateLifecycleService();
    }

    @Bean
    public CertificateIssuancePolicy certificateIssuancePolicy() {
        return new CertificateIssuancePolicy();
    }
}
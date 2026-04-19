package vn.xime.trust.config.usecase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.xime.trust.domain.factory.ServiceFactory;
import vn.xime.trust.domain.service.KeyLifecycleDomainService;
import vn.xime.trust.domain.factory.KeyFactory;
import vn.xime.trust.domain.factory.KeyPolicyFactory;
import vn.xime.trust.domain.factory.ShardFactory;
import vn.xime.trust.domain.service.KeyValidationDomainService;


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
}
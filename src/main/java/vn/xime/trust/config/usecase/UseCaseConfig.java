package vn.xime.trust.config.usecase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.xime.trust.domain.factory.ServiceFactory;

@Configuration
public class UseCaseConfig {

    @Bean
    public ServiceFactory serviceFactory() {
        return new ServiceFactory();
    }
}
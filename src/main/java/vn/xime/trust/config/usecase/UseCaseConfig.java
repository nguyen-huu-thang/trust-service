package vn.xime.trust.config.usecase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.xime.trust.application.port.out.KeyEncryptionService;
import vn.xime.trust.application.port.out.KeyGenerator;
import vn.xime.trust.application.usecase.key.GenerateKeyUseCase;
import vn.xime.trust.application.usecase.key.GetKeyUseCase;
import vn.xime.trust.domain.factory.ServiceFactory;
import vn.xime.trust.domain.repository.KeyRepository;

@Configuration
public class UseCaseConfig {

    @Bean
    public GenerateKeyUseCase generateKeyUseCase(
        KeyRepository keyRepository,
        KeyGenerator keyGenerator,
        KeyEncryptionService encryptionService
    ) {
        return new GenerateKeyUseCase(
            keyRepository,
            keyGenerator,
            encryptionService
        );
    }

    @Bean
    public GetKeyUseCase getKeyUseCase(
        KeyRepository keyRepository,
        KeyEncryptionService encryptionService
    ) {
        return new GetKeyUseCase(
        keyRepository,
        encryptionService
        );
    }

    @Bean
    public ServiceFactory serviceFactory() {
        return new ServiceFactory();
    }
}
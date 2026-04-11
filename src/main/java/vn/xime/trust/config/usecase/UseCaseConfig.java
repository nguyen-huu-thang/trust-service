package vn.xime.trust.config.usecase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.xime.trust.application.port.KeyEncryptionService;
import vn.xime.trust.application.port.KeyGenerator;
import vn.xime.trust.application.usecase.GenerateKeyUseCase;
import vn.xime.trust.application.usecase.GetKeyUseCase;
import vn.xime.trust.domain.key.KeyRepository;

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
}
package vn.xime.key.config.usecase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.xime.key.application.port.KeyEncryptionService;
import vn.xime.key.application.port.KeyGenerator;
import vn.xime.key.application.usecase.GenerateKeyUseCase;
import vn.xime.key.application.usecase.GetKeyUseCase;
import vn.xime.key.domain.key.KeyRepository;

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
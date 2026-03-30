package vn.xime.key.config.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.xime.key.application.port.KeyEncryptionService;
import vn.xime.key.application.port.KeyGenerator;
import vn.xime.key.infrastructure.crypto.AesKeyEncryptionService;
import vn.xime.key.infrastructure.crypto.RsaKeyGenerator;

@Configuration
public class CryptoConfig {

    @Bean
    public KeyEncryptionService keyEncryptionService(
            @Value("${key.encryption.secret}") String secret
    ) {
        return new AesKeyEncryptionService(secret);
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new RsaKeyGenerator();
    }
}
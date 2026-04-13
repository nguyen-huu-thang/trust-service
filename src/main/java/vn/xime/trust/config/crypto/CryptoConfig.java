package vn.xime.trust.config.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.xime.trust.application.port.out.KeyEncryptionService;
import vn.xime.trust.application.port.out.KeyGenerator;
import vn.xime.trust.infrastructure.crypto.AesKeyEncryptionService;
import vn.xime.trust.infrastructure.crypto.RsaKeyGenerator;

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
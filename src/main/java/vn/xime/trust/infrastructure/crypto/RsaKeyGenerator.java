package vn.xime.trust.infrastructure.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import vn.xime.trust.application.port.out.KeyGenerator;

public class RsaKeyGenerator implements KeyGenerator {

    @Override
    public GeneratedKeyPair generate(String algorithm, int keySize) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm);
            generator.initialize(keySize);

            KeyPair keyPair = generator.generateKeyPair();

            // Convert to Base64 String
            String publicKey = Base64.getEncoder()
                    .encodeToString(keyPair.getPublic().getEncoded());

            String privateKey = Base64.getEncoder()
                    .encodeToString(keyPair.getPrivate().getEncoded());

            return new GeneratedKeyPair(publicKey, privateKey);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate " + algorithm + " key", e);
        }
    }
}
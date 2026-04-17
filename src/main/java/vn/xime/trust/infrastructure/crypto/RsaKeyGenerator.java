package vn.xime.trust.infrastructure.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import vn.xime.trust.application.port.out.KeyGenerator;

public class RsaKeyGenerator implements KeyGenerator {

    @Override
    public KeyPair generate(String algorithm, int keySize) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm);
            generator.initialize(keySize);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate " + algorithm + " key", e);
        }
    }
}
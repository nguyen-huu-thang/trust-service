package vn.xime.trust.infrastructure.crypto;

import vn.xime.trust.application.port.out.KeyGenerator;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.Objects;

/**
 * Key generator hỗ trợ:
 *  - RSA
 *  - EC (ECDSA)
 *
 * Trả về Base64 encoded key (PKCS#8 / X.509)
 */
public class DefaultKeyGenerator implements KeyGenerator {

    @Override
    public GeneratedKeyPair generate(String algorithm, int keySize) {
        Objects.requireNonNull(algorithm, "Algorithm must not be null");

        try {
            KeyPair keyPair = switch (algorithm) {
                case "RSA" -> generateRsa(keySize);
                case "EC"  -> generateEc(keySize);
                case "ECDSA"  -> generateEc(keySize);
                default -> throw new IllegalArgumentException(
                        "Unsupported algorithm: " + algorithm
                );
            };

            return toBase64(keyPair);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to generate key. algorithm=" + algorithm + ", keySize=" + keySize,
                    e
            );
        }
    }

    // =========================
    // RSA
    // =========================

    private KeyPair generateRsa(int keySize) throws Exception {
        if (keySize < 2048) {
            throw new IllegalArgumentException("RSA key size must be >= 2048");
        }

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(keySize, new SecureRandom());

        return generator.generateKeyPair();
    }

    // =========================
    // EC (ECDSA)
    // =========================

    private KeyPair generateEc(int keySize) throws Exception {
        String curve = mapCurve(keySize);

        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec(curve), new SecureRandom());

        return generator.generateKeyPair();
    }

    private String mapCurve(int keySize) {
        return switch (keySize) {
            case 256 -> "secp256r1"; // P-256
            case 384 -> "secp384r1"; // P-384
            case 521 -> "secp521r1"; // optional
            default -> throw new IllegalArgumentException(
                    "Unsupported EC key size: " + keySize +
                    " (supported: 256, 384, 521)"
            );
        };
    }

    // =========================
    // Convert
    // =========================

    private GeneratedKeyPair toBase64(KeyPair keyPair) {
        String publicKey = Base64.getEncoder()
                .encodeToString(keyPair.getPublic().getEncoded());

        String privateKey = Base64.getEncoder()
                .encodeToString(keyPair.getPrivate().getEncoded());

        return new GeneratedKeyPair(publicKey, privateKey);
    }
}
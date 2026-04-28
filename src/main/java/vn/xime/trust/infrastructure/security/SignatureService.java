package vn.xime.trust.infrastructure.security;

import java.security.PrivateKey;
import java.security.Signature;
import java.util.Objects;

/**
 * Service thực hiện ký dữ liệu.
 *
 * Trách nhiệm:
 *  - Thực hiện crypto signing
 *  - Resolve thuật toán phù hợp với loại key
 *
 * KHÔNG:
 *  - load key
 *  - biết key đến từ đâu (file, HSM, KMS...)
 */
public class SignatureService {

    /**
     * Ký dữ liệu với private key
     *
     * @param data dữ liệu cần ký (thường là DER của tbsCertificate)
     * @param privateKey private key dùng để ký
     * @return chữ ký
     */
    public byte[] sign(byte[] data, PrivateKey privateKey) {
        Objects.requireNonNull(data, "Data must not be null");
        Objects.requireNonNull(privateKey, "PrivateKey must not be null");

        if (data.length == 0) {
            throw new IllegalArgumentException("Data to sign must not be empty");
        }

        String algorithm = resolveAlgorithm(privateKey);

        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Signing failed with algorithm: " + algorithm,
                    e
            );
        }
    }

    /**
     * Xác định thuật toán ký dựa trên loại key
     */
    private String resolveAlgorithm(PrivateKey privateKey) {
        String keyAlgorithm = privateKey.getAlgorithm();

        return switch (keyAlgorithm) {
            case "RSA" -> "SHA256withRSA";
            case "EC"  -> "SHA256withECDSA";
            default -> throw new IllegalStateException(
                    "Unsupported key algorithm: " + keyAlgorithm
            );
        };
    }
}
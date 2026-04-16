package vn.xime.trust.application.port.out;

import java.security.KeyPair;

public interface KeyGenerator {
    KeyPair generate(int keySize);
}

// package vn.xime.trust.application.port.out;

// public interface KeyGenerator {

//     GeneratedKeyPair generate(String algorithm, int keySize);

//     class GeneratedKeyPair {
//         private final String publicKey;
//         private final String privateKey;

//         public GeneratedKeyPair(String publicKey, String privateKey) {
//             this.publicKey = publicKey;
//             this.privateKey = privateKey;
//         }

//         public String getPublicKey() {
//             return publicKey;
//         }

//         public String getPrivateKey() {
//             return privateKey;
//         }
//     }
// }
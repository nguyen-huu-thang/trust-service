package vn.xime.trust.application.port.out;

import java.security.KeyPair;

public interface KeyGenerator {
    KeyPair generate(int keySize);
}
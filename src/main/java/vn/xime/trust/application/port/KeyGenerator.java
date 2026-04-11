package vn.xime.trust.application.port;

import java.security.KeyPair;

public interface KeyGenerator {
    KeyPair generate(int keySize);
}
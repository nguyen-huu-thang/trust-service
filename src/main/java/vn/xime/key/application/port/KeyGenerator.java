package vn.xime.key.application.port;

import java.security.KeyPair;

public interface KeyGenerator {
    KeyPair generate(int keySize);
}
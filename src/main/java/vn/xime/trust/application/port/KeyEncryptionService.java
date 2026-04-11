package vn.xime.trust.application.port;

public interface KeyEncryptionService {

    String encrypt(String privateKey);

    String decrypt(String encrypted);
}
package vn.xime.key.application.port;

public interface KeyEncryptionService {

    String encrypt(String privateKey);

    String decrypt(String encrypted);
}
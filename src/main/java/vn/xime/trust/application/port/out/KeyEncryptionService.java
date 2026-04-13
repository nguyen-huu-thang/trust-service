package vn.xime.trust.application.port.out;

public interface KeyEncryptionService {

    String encrypt(String privateKey);

    String decrypt(String encrypted);
}
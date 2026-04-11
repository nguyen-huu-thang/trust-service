package vn.xime.trust.application.dto.response;

import java.util.List;

public class GetKeysResponseDto {

    /**
     * Nếu includePrivate = false → dùng publicKeys
     */
    private final List<PublicKeyDto> publicKeys;

    /**
     * Nếu includePrivate = true → dùng privateKeys
     */
    private final List<PrivateKeyDto> privateKeys;

    public GetKeysResponseDto(
            List<PublicKeyDto> publicKeys,
            List<PrivateKeyDto> privateKeys
    ) {
        this.publicKeys = publicKeys;
        this.privateKeys = privateKeys;
    }

    public List<PublicKeyDto> getPublicKeys() {
        return publicKeys;
    }

    public List<PrivateKeyDto> getPrivateKeys() {
        return privateKeys;
    }
}
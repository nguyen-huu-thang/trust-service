package vn.xime.trust.application.usecase.cert;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;
import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.repository.CertRefreshTokenRepository;
import vn.xime.trust.domain.service.CertRefreshTokenDomainService;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.application.mapper.CertificateMapper;
import vn.xime.trust.application.dto.response.CertRefreshTokenDto;



@Component
public class GetCertRefreshTokensUseCase {

    private final CertRefreshTokenRepository repository;
    private final CertRefreshTokenDomainService domainService;
    private final CertificateMapper mapper;

    public GetCertRefreshTokensUseCase(
            CertRefreshTokenRepository repository,
            CertRefreshTokenDomainService domainService,
            CertificateMapper mapper
    ) {
        this.repository = repository;
        this.domainService = domainService;
        this.mapper = mapper;
    }

    // ==================================================
    // GET BY ID (ADMIN)
    // ==================================================

    public CertRefreshTokenDto getById(String certId) {

        Id id = IdService.fromString(certId);

        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }

        CertRefreshToken certToken = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Token not found"));
        
        return mapper.toCertTokenDto(certToken);
    }

    // ==================================================
    // GET BY TOKEN HASH (ADMIN / DEBUG)
    // ==================================================

    public CertRefreshTokenDto getByTokenHash(String tokenHash) {
        if (tokenHash == null || tokenHash.isBlank()) {
            throw new IllegalArgumentException("tokenHash is required");
        }

        CertRefreshToken certToken = repository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalStateException("Token not found"));
        
        return mapper.toCertTokenDto(certToken);
    }

    // ==================================================
    // LIST ACTIVE TOKENS (ADMIN)
    // ==================================================

    public List<CertRefreshTokenDto> listActiveTokens() {

        List<CertRefreshToken> certTokens = repository.findAllNotDeleted();

        return certTokens.stream().map(mapper::toCertTokenDto).toList();
    }

    // ==================================================
    // GET USABLE TOKEN (RUNTIME)
    // ==================================================

    // hàm này hiện tại không cần lắm.
    // vì logic bây giờ chỉ trả token quan bootstrap hoặc rotate để đảm bảo vấn đề bảo mật.

    public CertRefreshToken getUsableToken(String tokenHash) {
        if (tokenHash == null || tokenHash.isBlank()) {
            throw new IllegalArgumentException("tokenHash is required");
        }

        Instant now = Instant.now();

        // =========================
        // LOAD
        // =========================

        CertRefreshToken token = repository.findUsableToken(tokenHash, now)
                .orElseThrow(() -> new IllegalStateException("Token not usable"));

        // =========================
        // DOMAIN VALIDATION
        // =========================

        if (!domainService.isUsable(token, now)) {
            throw new IllegalStateException("Token not usable");
        }

        return token;
    }
}
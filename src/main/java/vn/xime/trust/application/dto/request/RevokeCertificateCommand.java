package vn.xime.trust.application.dto.request;

import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor

public class RevokeCertificateCommand {
    private final String certId;
    private final String reason;
}

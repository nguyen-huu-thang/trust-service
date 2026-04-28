package vn.xime.trust.infrastructure.crypto;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;

/**
 * Factory tạo X500Name cho:
 *  - Subject (service)
 *  - Issuer (CA)
 *
 * Giữ logic naming tách biệt khỏi builder
 */
public class X509NameFactory {

    /**
     * Tạo Subject cho service certificate
     *
     * Lưu ý:
     *  - CN chỉ mang tính hiển thị
     *  - Identity thật nằm ở SAN (SPIFFE)
     */
    public X500Name createSubject(String serviceId) {
        return new X500NameBuilderWrapper()
                .add(BCStyle.CN, serviceId)
                .add(BCStyle.O, "Platform")
                .add(BCStyle.OU, "Service")
                .build();
    }

    /**
     * Tạo Issuer cho CA certificate (DEV mode)
     *
     * Trong thực tế:
     *  - Nên lấy từ CA certificate (không hardcode)
     */
    public X500Name createIssuer(String caName) {
        return new X500NameBuilderWrapper()
                .add(BCStyle.CN, caName)
                .add(BCStyle.O, "Platform")
                .add(BCStyle.OU, "Trust Service")
                .build();
    }

    /**
     * Wrapper để code builder gọn hơn
     */
    private static class X500NameBuilderWrapper {

        private final org.bouncycastle.asn1.x500.X500NameBuilder builder =
                new org.bouncycastle.asn1.x500.X500NameBuilder(BCStyle.INSTANCE);

        public X500NameBuilderWrapper add(
                org.bouncycastle.asn1.ASN1ObjectIdentifier oid,
                String value
        ) {
            if (value != null && !value.isBlank()) {
                builder.addRDN(oid, value);
            }
            return this;
        }

        public X500Name build() {
            return builder.build();
        }
    }
}
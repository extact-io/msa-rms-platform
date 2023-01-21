package io.extact.msa.rms.platform.core.jwt;

import org.eclipse.microprofile.config.Config;

public class JwtConfig {

    public static final String CONFIG_PREFIX = "jwt.";
    public static final String BEARER_MARK = "Bearer";
    private static final long ISSUED_AT_NOW = -1;

    private Config config;

    public static JwtConfig of(Config config) {
        return new JwtConfig(config);
    }

    JwtConfig(Config config) {
        this.config = config;
    }

    public String getIssuer() {
        return config.getValue(CONFIG_PREFIX + "claim.issuer", String.class);
    }
    public boolean isIssuedAtToNow() {
        return getIssuedAt() == ISSUED_AT_NOW;
    }
    public long getIssuedAt() {
        return config.getValue(CONFIG_PREFIX + "claim.issuedAt", Long.class);
    }
    public int getExpirationTime() {
        return config.getValue(CONFIG_PREFIX + "claim.exp", Integer.class);
    }
    public String getPrivateKeyPath() {
        return config.getValue(CONFIG_PREFIX + "privatekey.path", String.class);
    }
}

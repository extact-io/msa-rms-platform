package io.extact.msa.rms.platform.core.jwt.validate;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.jwt.JwtClaims;

public class Jose4jJsonWebToken implements JsonWebToken {

    private JwtClaims jwt;

    public Jose4jJsonWebToken(JwtClaims claims) {
        this.jwt = claims;
    }

    @Override
    public String getName() {
        return getClaim(Claims.upn.name());
    }

    @Override
    public Set<String> getGroups() {
        // MP-JWTはSetを要求するため変換
        return new HashSet<>(getClaim(Claims.groups.name()));
    }

    @Override
    public Set<String> getClaimNames() {
        return new HashSet<>(jwt.getClaimNames());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getClaim(String claimName) {
        return (T) jwt.getClaimValue(claimName);
    }

}

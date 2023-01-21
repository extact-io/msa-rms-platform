package io.extact.msa.rms.platform.core.jwt.validate;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.auth0.jwt.interfaces.DecodedJWT;

public class Auth0JsonWebToken implements JsonWebToken {

    private DecodedJWT jwt;

    public Auth0JsonWebToken(DecodedJWT jwt) {
        this.jwt = jwt;
    }

    @Override
    public String getName() {
        return jwt.getClaim(Claims.upn.name()).asString();
    }

    @Override
    public Set<String> getGroups() {
        return new HashSet<>(jwt.getClaim(Claims.groups.name()).asList(String.class));
    }

    @Override
    public Set<String> getClaimNames() {
        return new HashSet<>(jwt.getClaims().keySet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getClaim(Claims claim) {
        if (claim.getType().equals(String.class)) {
            return (T) jwt.getClaim(claim.name()).asString();
        }
        if (claim.getType().equals(Long.class)) {
            return (T) jwt.getClaim(claim.name()).asLong();
        }
        if (claim.getType().equals(Set.class)) {
            var claimValue = jwt.getClaim(claim.name());
            return !claimValue.isMissing() && !claimValue.isNull()
                    ? (T) new HashSet<>(jwt.getClaim(claim.name()).asList(String.class))
                    : null;
        }
        return (T) jwt.getClaim(claim.name()).toString();
    }

    @Override
    public <T> T getClaim(String claimName) {
        throw new UnsupportedOperationException();
    }
}

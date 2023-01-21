package io.extact.msa.rms.platform.core.jwt.provider.impl;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.jwt.Claims;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;

import io.extact.msa.rms.platform.core.extension.ConfiguableScoped;
import io.extact.msa.rms.platform.core.jwt.JwtConfig;
import io.extact.msa.rms.platform.core.jwt.provider.JsonWebTokenGenerator;
import io.extact.msa.rms.platform.core.jwt.provider.SecretKeyFile;
import io.extact.msa.rms.platform.core.jwt.provider.UserClaims;

@ConfiguableScoped
public class Auth0RsaJwtGenerator implements JsonWebTokenGenerator {

    private JwtConfig jwtConfig;

    @Inject
    public  Auth0RsaJwtGenerator(Config config) {
        this.jwtConfig = JwtConfig.of(config);
    }

    @Override
    public String generateToken(UserClaims userClaims) {
        Algorithm alg = Algorithm.RSA256(createPrivateKey());
        return buildClaims(userClaims).sign(alg);
    }

    private RSAPrivateKey createPrivateKey() {
        SecretKeyFile keyFile = new SecretKeyFile(jwtConfig.getPrivateKeyPath());
        return keyFile.generateKey(SecretKeyFile.PRIVATE);
    }

    private Builder buildClaims(UserClaims userClaims) {
        // MicroProfile-JWTで必須とされている項目のみ設定
        return JWT.create()
                .withIssuer(jwtConfig.getIssuer())
                .withSubject(userClaims.getUserId())
                .withExpiresAt(OffsetDateTime.now()
                        .plusMinutes(jwtConfig.getExpirationTime())
                        .toInstant())
                .withIssuedAt(resoleveIssuedAt(jwtConfig.getIssuedAt()))
                .withJWTId(UUID.randomUUID().toString())
                .withClaim(Claims.upn.name(), userClaims.getUserPrincipalName())
                .withClaim(Claims.groups.name(), new ArrayList<>(userClaims.getGroups()));
    }

    private Instant resoleveIssuedAt(long secondsFromEpoch) {
        return jwtConfig.isIssuedAtToNow()
                ? OffsetDateTime.now().toInstant()
                : Instant.ofEpochSecond(secondsFromEpoch);
    }
}

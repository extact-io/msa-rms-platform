package io.extact.msa.rms.platform.core.jwt.provider.impl;

import static org.jose4j.jws.AlgorithmIdentifiers.*;

import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.lang.JoseException;

import io.extact.msa.rms.platform.core.extension.ConfiguableScoped;
import io.extact.msa.rms.platform.core.jwt.JwtConfig;
import io.extact.msa.rms.platform.core.jwt.provider.JsonWebTokenGenerator;
import io.extact.msa.rms.platform.core.jwt.provider.SecretKeyFile;
import io.extact.msa.rms.platform.core.jwt.provider.UserClaims;

@ConfiguableScoped
public class Jose4jRsaJwtGenerator implements JsonWebTokenGenerator {

    private JwtConfig jwtConfig;

    @Inject
    public  Jose4jRsaJwtGenerator(Config config) {
        this.jwtConfig = JwtConfig.of(config);
    }

    @Override
    public String generateToken(UserClaims userClaims) {

        var jws = new JsonWebSignature(); // 署名オブジェクト
        var privateKey = createPrivateKey(); // RSA秘密鍵(p8フォーマット)

        JwtClaims claims = createClaims(userClaims);
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(RSA_USING_SHA256);
        jws.setKey(privateKey);
        jws.setDoKeyValidation(false);

        try {
            // ClaimsのJSONを秘密鍵で署名
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            throw new IllegalStateException(e);
        }
    }

    private RSAPrivateKey createPrivateKey() {
        SecretKeyFile keyFile = new SecretKeyFile(jwtConfig.getPrivateKeyPath());
        return keyFile.generateKey(SecretKeyFile.PRIVATE);
    }

    private JwtClaims createClaims(UserClaims userClaims) {

        // MicroProfile-JWTで必須とされている項目のみ設定
        var claims = new JwtClaims();

        // 発行者
        claims.setIssuer(jwtConfig.getIssuer());
        // ユーザ識別子
        claims.setSubject(userClaims.getUserId());
        // 有効期限(exp)
        claims.setExpirationTimeMinutesInTheFuture(jwtConfig.getExpirationTime());
        // 発行日時(iat)
        if (jwtConfig.isIssuedAtToNow()) {
            claims.setIssuedAtToNow();
        } else {
            claims.setIssuedAt(NumericDate.fromSeconds(jwtConfig.getIssuedAt()));
        }
        // tokenId(jti)
        claims.setGeneratedJwtId();
        // ユーザ名(MicroProfile-JWTのカスタムClaim)
        claims.setStringClaim("upn", userClaims.getUserPrincipalName());
        // グループ名(MicroProfile-JWTのカスタムClaim)
        claims.setStringListClaim("groups", new ArrayList<>(userClaims.getGroups()));

        return claims;
    }
}

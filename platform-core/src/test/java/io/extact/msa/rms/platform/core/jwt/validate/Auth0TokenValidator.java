package io.extact.msa.rms.platform.core.jwt.validate;

import java.security.interfaces.RSAPublicKey;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;

import io.extact.msa.rms.platform.core.jwt.JwtConfig;
import io.extact.msa.rms.platform.core.jwt.JwtValidateException;
import io.extact.msa.rms.platform.core.jwt.provider.SecretKeyFile;

public class Auth0TokenValidator implements JsonWebTokenValidator {

    private JwtConfig jwtConfig;

    public Auth0TokenValidator(JwtConfig jwtConfig) {
        super();
        this.jwtConfig = jwtConfig;
    }

    @Override
    public JsonWebToken validate(String token) throws JwtValidateException {
        Algorithm alg = Algorithm.RSA256(createPublicKey());
        JWTVerifier verifier = JWT.require(alg)
                .acceptExpiresAt(30)                    // 有効期限の時間ズレ許容秒数
                .withClaimPresence(Claims.sub.name())   // サブジェクトは必須
                .withClaimPresence(Claims.jti.name())   // JwtIdは必須
                .withIssuer(jwtConfig.getIssuer())      // 発行者は自分自身であること
                .build();
        return new Auth0JsonWebToken(verifier.verify(token));
    }

    private RSAPublicKey createPublicKey() {
        SecretKeyFile keyFile = new SecretKeyFile(TEST_PUBLIC_KEY_PATH);
        return keyFile.generateKey(SecretKeyFile.PUBLIC);
    }
}

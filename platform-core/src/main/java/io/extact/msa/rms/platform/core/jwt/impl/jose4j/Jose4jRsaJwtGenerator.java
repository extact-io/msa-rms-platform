package io.extact.msa.rms.platform.core.jwt.impl.jose4j;

import static org.jose4j.jws.AlgorithmIdentifiers.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.jose4j.base64url.SimplePEMEncoder;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.lang.JoseException;

import io.extact.msa.rms.platform.core.extension.ConfiguableScoped;
import io.extact.msa.rms.platform.core.jwt.JwtConfig;
import io.extact.msa.rms.platform.core.jwt.provider.JsonWebTokenGenerator;
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
        var privateKey = createPrivateKeyFromJson();
        //var privateKey = createPrivateKeyFromPem();

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

    private PrivateKey createPrivateKeyFromJson() {
        String path = "/jwtKey.json";
        try (InputStream is = this.getClass().getResourceAsStream(path);
                BufferedReader buff = new BufferedReader(new InputStreamReader(is))) {
            var json = new StringBuilder();
            String line;
            while ((line = buff.readLine()) != null) {
                json.append(line);
            }
            var jwk = (RsaJsonWebKey) JsonWebKey.Factory.newJwk(json.toString());
            return jwk.getPrivateKey();
        } catch (IOException | JoseException e) {
            throw new IllegalStateException(e);
        }
    }

    private RSAPrivateKey createPrivateKeyFromPem() {
        String path = "/jwtKey.p8";
        try (InputStream is = this.getClass().getResourceAsStream(path);
                BufferedReader buff = new BufferedReader(new InputStreamReader(is))) {
            var pem = new StringBuilder();
            String line;
            while ((line = buff.readLine()) != null) {
                pem.append(line);
            }

            String privateKeyPem = pem.toString()
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PRIVATE KEY-----", "");

                  byte[] encoded = SimplePEMEncoder.decode(privateKeyPem);

                  KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                  PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
                  return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
    }

    // ----------------------------------------------------- inner classes

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

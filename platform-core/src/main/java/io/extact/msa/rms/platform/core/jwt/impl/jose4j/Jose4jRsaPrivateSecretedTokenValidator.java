package io.extact.msa.rms.platform.core.jwt.impl.jose4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.base64url.SimplePEMEncoder;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import io.extact.msa.rms.platform.core.extension.ConfiguableScoped;
import io.extact.msa.rms.platform.core.jwt.JwtConfig;
import io.extact.msa.rms.platform.core.jwt.JwtValidateException;
import io.extact.msa.rms.platform.core.jwt.consumer.JsonWebTokenValidator;

/**
 * 秘密鍵で署名されたTokenの検証クラス。
 */
@ConfiguableScoped
public class Jose4jRsaPrivateSecretedTokenValidator implements JsonWebTokenValidator {

    private JwtConfig jwtConfig;

    @Inject
    public Jose4jRsaPrivateSecretedTokenValidator(Config config) {
        this.jwtConfig = JwtConfig.of(config);
    }

    @Override
    public JsonWebToken validate(String token) throws JwtValidateException {

        // 検証条件の設定
        JwtConsumer consumer = new JwtConsumerBuilder()
                // 有効期限をチェックする
                .setRequireExpirationTime()
                // 有効期限の時間ズレ許容秒数
                .setAllowedClockSkewInSeconds(jwtConfig.getAllowedClockSeconds())
                // サブジェクトは必須
                .setRequireSubject()
                // JwtIdは必須
                .setRequireJwtId()
                // 発行者は自分自身であること
                .setExpectedIssuer(jwtConfig.getIssuer())
                // 受信者のチェックはしない
                .setSkipDefaultAudienceValidation()
                // トークンの署名を検査するキー（＝署名に使ったキー）
                .setVerificationKey(createPublicKeyFromPem())
                // 復号キーの形式チェックはしない
                .setRelaxVerificationKeyValidation()
                .build();

        // tokenを検証しClaimsを取り出す
        JwtClaims claims;
        String userPrincipalName;
        try {
            claims = consumer.processToClaims(token);
            userPrincipalName = claims.getStringClaimValue(Claims.upn.name());
            Objects.requireNonNull(userPrincipalName); // 必須項目
        } catch (InvalidJwtException | MalformedClaimException e) {
            throw new JwtValidateException(e);
        }

        claims.setClaim(Claims.raw_token.name(), token);
        return new Jose4jCallerPrincipal(userPrincipalName, claims);
    }

    private RSAPublicKey createPublicKeyFromPem() {
        String path = "/jwtKeyPub.pem";
        try (InputStream is = this.getClass().getResourceAsStream(path);
                BufferedReader buff = new BufferedReader(new InputStreamReader(is))) {
            var pem = new StringBuilder();
            String line;
            while ((line = buff.readLine()) != null) {
                pem.append(line);
            }

            String publicKeyPem = pem.toString()
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PUBLIC KEY-----", "");

                  byte[] encoded = SimplePEMEncoder.decode(publicKeyPem);

                  KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                  X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
                  return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
    }
}

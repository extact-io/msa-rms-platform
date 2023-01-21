package io.extact.msa.rms.platform.core.jwt.validate;

import java.security.interfaces.RSAPublicKey;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import io.extact.msa.rms.platform.core.jwt.JwtConfig;
import io.extact.msa.rms.platform.core.jwt.JwtValidateException;
import io.extact.msa.rms.platform.core.jwt.provider.SecretKeyFile;

public class Jose4jTokenValidator implements JsonWebTokenValidator {

    private JwtConfig jwtConfig;

    public Jose4jTokenValidator(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public JsonWebToken validate(String token) throws JwtValidateException {
        JwtConsumer consumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()                 // 有効期限をチェックする
                .setAllowedClockSkewInSeconds(30)           // 有効期限の時間ズレ許容秒数
                .setRequireSubject()                        // サブジェクトは必須
                .setRequireJwtId()                          // JwtIdは必須
                .setExpectedIssuer(jwtConfig.getIssuer())   // 発行者は自分自身であること
                .setSkipDefaultAudienceValidation()         // 受信者のチェックはしない
                .setVerificationKey(createPublicKey())      // トークンの署名を検査するキー（＝署名に使ったキー）
                .setRelaxVerificationKeyValidation()        // 復号キーの形式チェックはしない
                .build();
        try {
            return new Jose4jJsonWebToken(consumer.processToClaims(token));
        } catch (InvalidJwtException e) {
            throw new JwtValidateException(e);
        }
    }

    private RSAPublicKey createPublicKey() {
        SecretKeyFile keyFile = new SecretKeyFile(TEST_PUBLIC_KEY_PATH);
        return keyFile.generateKey(SecretKeyFile.PUBLIC);
    }
}

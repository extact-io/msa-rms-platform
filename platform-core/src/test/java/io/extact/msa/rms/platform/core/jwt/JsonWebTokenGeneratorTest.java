package io.extact.msa.rms.platform.core.jwt;

import static io.extact.msa.rms.platform.core.jwt.JsonWebTokenGeneratorTest.ImplType.*;
import static io.extact.msa.rms.platform.core.jwt.JsonWebTokenGeneratorTest.JsonWebTokenGeneratorFactory.*;
import static io.extact.msa.rms.platform.core.jwt.JsonWebTokenGeneratorTest.JsonWebTokenValidatorFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.extact.msa.rms.platform.core.jwt.provider.JsonWebTokenGenerator;
import io.extact.msa.rms.platform.core.jwt.provider.UserClaims;
import io.extact.msa.rms.platform.core.jwt.provider.impl.Auth0RsaJwtGenerator;
import io.extact.msa.rms.platform.core.jwt.provider.impl.Jose4jRsaJwtGenerator;
import io.extact.msa.rms.platform.core.jwt.validate.Auth0TokenValidator;
import io.extact.msa.rms.platform.core.jwt.validate.Jose4jTokenValidator;
import io.extact.msa.rms.platform.core.jwt.validate.JsonWebTokenValidator;

class JsonWebTokenGeneratorTest {

    private MapConfig mapConfig;

    @BeforeEach
    void setup() {
        // デフォルト値の設定
        MapConfig config = new MapConfig();
        config.setPrivateKeyPath("/jwt.key");
        config.setIssuer("testApplication");
        config.setIssuedAt(-1L);
        config.setExpirationTime(60);
        this.mapConfig = config;
    }

    @ParameterizedTest
    @MethodSource("generatorAndValidatorFactoryProvider")
    void testGenerateTokenAndValidate(
            JsonWebTokenGeneratorFactory generatorFactoyr,
            JsonWebTokenValidatorFactory validatorFactory) throws JwtValidateException {

        // テストし易いように発行日時と有効期限を固定
        long now = System.currentTimeMillis() / 1000L; // 秒で表した現在日時
        mapConfig.setIssuedAt(now); // 発行日時を固定で設定
        mapConfig.setExpirationTime(0);

        // Tokenの生成
        JsonWebTokenGenerator testGenerator = generatorFactoyr.create(mapConfig);
        UserClaims userClaims = new SimpleUserClaims();
        String token = testGenerator.generateToken(userClaims);

        // 生成したTokenをJOSE4JとAuth0とで検査
        JwtConfig jwtConfig = JwtConfig.of(mapConfig);
        JsonWebTokenValidator validator = validatorFactory.create(jwtConfig);
        JsonWebToken jwt = validator.validate(token);

        // 復元したJSONが元通りか確認
        assertThat(jwt.getName()).isEqualTo(userClaims.getUserPrincipalName());
        assertThat(jwt.getIssuer()).isEqualTo(jwtConfig.getIssuer());
        assertThat(jwt.getAudience()).isNull();
        assertThat(jwt.getSubject()).isEqualTo(userClaims.getUserId());
        assertThat(jwt.getTokenID()).isNotNull();
        assertThat(jwt.getIssuedAtTime()).isEqualTo(now);
        assertThat(jwt.getExpirationTime()).isBetween(now, now + 5L); // JwtClaims内部でnowをするため+5msecまでは誤差として許容
        assertThat(jwt.getGroups()).hasSize(1);
        assertThat(jwt.getGroups()).containsAll(userClaims.getGroups());
    }

    static Stream<Arguments> generatorAndValidatorFactoryProvider() {
        return Stream.of(
            arguments(JOSE4J_GENERATOR, JOSE4J_VALIDATOR),
            arguments(JOSE4J_GENERATOR, AUTH0_VALIDATOR),
            arguments(AUTH0_GENERATOR, JOSE4J_VALIDATOR),
            arguments(AUTH0_GENERATOR, AUTH0_VALIDATOR)
        );
    }

    static class SimpleUserClaims implements UserClaims {
        @Override
        public String getUserId() {
            return "soramame";
        }
        @Override
        public String getUserPrincipalName() {
            return "soramame@rms.com";
        }
        @Override
        public Set<String> getGroups() {
            return Set.of("1");
        }
    }

    static class JsonWebTokenGeneratorFactory {
        static final JsonWebTokenGeneratorFactory JOSE4J_GENERATOR = new JsonWebTokenGeneratorFactory(JOSE4J);
        static final JsonWebTokenGeneratorFactory AUTH0_GENERATOR = new JsonWebTokenGeneratorFactory(AUTH0);
        ImplType implType;
        JsonWebTokenGeneratorFactory(ImplType implType) {
            this.implType = implType;
        }
        JsonWebTokenGenerator create(Config config) {
            return switch (implType) {
                case JOSE4J -> new Jose4jRsaJwtGenerator(config);
                case AUTH0 -> new Auth0RsaJwtGenerator(config);
            };
        }

    }

    static class JsonWebTokenValidatorFactory {
        static final JsonWebTokenValidatorFactory JOSE4J_VALIDATOR = new JsonWebTokenValidatorFactory(JOSE4J);
        static final JsonWebTokenValidatorFactory AUTH0_VALIDATOR = new JsonWebTokenValidatorFactory(AUTH0);
        ImplType implType;
        JsonWebTokenValidatorFactory(ImplType implType) {
            this.implType = implType;
        }
        JsonWebTokenValidator create(JwtConfig jwtConfig) {
            return switch (implType) {
                case JOSE4J -> new Jose4jTokenValidator(jwtConfig);
                case AUTH0 -> new Auth0TokenValidator(jwtConfig);
            };
        }
    }

    enum ImplType {
        JOSE4J,
        AUTH0
    }
}

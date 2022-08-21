package io.extact.msa.rms.platform.fw.it;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.core.jwt.impl.jose4j.Jose4jJwtGenerator;
import io.extact.msa.rms.platform.core.jwt.impl.jose4j.Jose4jPrivateSecretedTokenValidator;
import io.extact.msa.rms.platform.core.jwt.login.LoginUserUtils;
import io.extact.msa.rms.platform.fw.it.AuthIntegratinTest.Server1AssertTest;
import io.extact.msa.rms.platform.fw.it.AuthIntegratinTest.Server2AssertTest;
import io.extact.msa.rms.platform.fw.stub.auth.client.ClientApi;
import io.extact.msa.rms.platform.fw.stub.auth.client.Server1ApiRestBridge;
import io.extact.msa.rms.platform.fw.stub.auth.client.Server1ApiRestClient;
import io.extact.msa.rms.platform.fw.stub.auth.server1.Server1Application;
import io.extact.msa.rms.platform.fw.stub.auth.server1.Server1Assert;
import io.extact.msa.rms.platform.fw.stub.auth.server1.Server1Resource;
import io.extact.msa.rms.platform.fw.stub.auth.server1.Server2ApiRestClient;
import io.extact.msa.rms.platform.fw.stub.auth.server2.Server2Application;
import io.extact.msa.rms.platform.fw.stub.auth.server2.Server2Assert;
import io.extact.msa.rms.platform.fw.stub.auth.server2.Server2Resource;
import io.extact.msa.rms.platform.fw.webapi.SecurityConstraintException;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

/**
 * スタブのPersonアプリを使ってplatform.fwクラスをテストする。
 * <pre>
 * ・スタブアプリ：CID Bean(PersonApi)
 * ・スタブアプリ：CID Bean(PersaonApiRestBridge)
 * ・スタブアプリ：RestClient(PersonApiRestClient)
 *     ↓ HTTP
 * ・スタブアプリ：RestResource(PersonApplication)
 * ・スタブアプリ：CID Bean(PersonService)
 * ・スタブアプリ：CID Bean(PersonJpaRepository) or (PersonFileRepository)
 * ※ JPAかFileかどちらの実装を使うかはこのクラスのサブクラスで決定
 * </pre>
 */
@HelidonTest
// for RESTResrouce Beans
@AddBean(Server1Resource.class)
@AddBean(Server1Application.class)
@AddBean(Jose4jJwtGenerator.class)
@AddBean(value = Server1AssertTest.class, scope = Dependent.class)
@AddBean(Server2Resource.class)
@AddBean(Server2Application.class)
@AddBean(Server2ApiRestClient.class)
@AddBean(value = Server2AssertTest.class, scope = Dependent.class)
@AddConfig(key = "jwt.filter.enable", value = "true") // 認証認可ON
@AddConfig(key = "server.port", value = "7001") // for PersonResource Server port
//for RESTClient Beans
@AddBean(Server1ApiRestBridge.class)
@AddBean(Server1ApiRestClient.class)
@AddBean(Jose4jPrivateSecretedTokenValidator.class)
@AddConfig(key = "configuredCdi.register.0.class", value = "io.extact.msa.rms.platform.core.jwt.client.JwtPropagateClientHeadersFactory")
@AddConfig(key = "web-api", value = "http://localhost:7001") // for REST Client
@ExtendWith(JulToSLF4DelegateExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class AuthIntegratinTest {

    @Inject
    private ClientApi api;

    @Test
    @Order(1)
    void testNotLogin() {
        // yet not login
        var actual = api.guestApi();
        assertThat(actual).isTrue();

        var thrown = catchThrowable(() -> api.memeberApi());
        assertThat(thrown).isInstanceOf(SecurityConstraintException.class);

        thrown = catchThrowable(() -> api.adminApi());
        assertThat(thrown).isInstanceOf(SecurityConstraintException.class);
    }

    @Test
    @Order(2)
    void testMemberLogin() {
        var actual = api.authenticate("1", "member");
        assertThat(actual.getUserId()).isEqualTo("1");
        assertThat(actual.getGroups()).isEqualTo(Set.of("member"));

        var result = api.guestApiWithLogin();
        assertThat(result).isTrue();

        result = api.memeberApi();
        assertThat(result).isTrue();

        var thrown = catchThrowable(() -> api.adminApi());
        assertThat(thrown).isInstanceOf(SecurityConstraintException.class);
    }


    @Test
    @Order(3)
    void testAdmin() {
        var actual = api.authenticate("2", "admin");
        assertThat(actual.getUserId()).isEqualTo("2");
        assertThat(actual.getGroups()).isEqualTo(Set.of("admin"));

        var result = api.guestApiWithLogin();
        assertThat(result).isTrue();

        result = api.adminApi();
        assertThat(result).isTrue();

        var thrown = catchThrowable(() -> api.memeberApi());
        assertThat(thrown).isInstanceOf(SecurityConstraintException.class);
    }

    static class Server1AssertTest implements Server1Assert {

        @Override
        public void doBeforeLoginAssert() {
            var actual = LoginUserUtils.get();
            assertThat(actual.isUnknownUser()).isTrue();
        }

        @Override
        public void doMemberApiAssert() {
            var actual = LoginUserUtils.get();
            assertThat(actual.isUnknownUser()).isFalse();
            assertThat(actual.getUserId()).isEqualTo(1);
        }

        @Override
        public void doAdminApiAssert() {
            var actual = LoginUserUtils.get();
            assertThat(actual.isUnknownUser()).isFalse();
            assertThat(actual.getUserId()).isEqualTo(2);
        }

        @Override
        public void doGuestApiAssert() {
            var actual = LoginUserUtils.get();
            assertThat(actual.isUnknownUser()).isTrue();
        }

        @Override
        public void doGuestApiWithAssert() {
            var actual = LoginUserUtils.get();
            assertThat(actual.isUnknownUser()).isTrue();
        }
    }

    static class Server2AssertTest implements Server2Assert {

        @Override
        public void doNotLoginApiAssert() {
            var actual = LoginUserUtils.get();
            assertThat(actual.isUnknownUser()).isTrue();
        }

        @Override
        public void doMemberLoginApiAssert() {
            var actual = LoginUserUtils.get();
            assertThat(actual.isUnknownUser()).isFalse();
            assertThat(actual.getUserId()).isEqualTo(1);
        }

        @Override
        public void doAdminLoginApi() {
            var actual = LoginUserUtils.get();
            assertThat(actual.isUnknownUser()).isFalse();
            assertThat(actual.getUserId()).isEqualTo(2);
        }

    }
}

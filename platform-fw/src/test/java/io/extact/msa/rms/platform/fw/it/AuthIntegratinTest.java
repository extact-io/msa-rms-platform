package io.extact.msa.rms.platform.fw.it;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.spi.CDI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.core.jwt.provider.impl.Auth0RsaJwtGenerator;
import io.extact.msa.rms.platform.fw.exception.webapi.SecurityConstraintException;
import io.extact.msa.rms.platform.fw.it.AuthIntegratinTest.Server1AssertTest;
import io.extact.msa.rms.platform.fw.it.AuthIntegratinTest.Server2AssertTest;
import io.extact.msa.rms.platform.fw.login.LoginUserUtils;
import io.extact.msa.rms.platform.fw.stub.auth.client.ClientApi;
import io.extact.msa.rms.platform.fw.stub.auth.client.Server1ApiProxy;
import io.extact.msa.rms.platform.fw.stub.auth.client.Server1ApiRestClient;
import io.extact.msa.rms.platform.fw.stub.auth.server1.Server1Application;
import io.extact.msa.rms.platform.fw.stub.auth.server1.Server1Assert;
import io.extact.msa.rms.platform.fw.stub.auth.server1.Server1Resource;
import io.extact.msa.rms.platform.fw.stub.auth.server1.Server2ApiRestClient;
import io.extact.msa.rms.platform.fw.stub.auth.server2.Server2Application;
import io.extact.msa.rms.platform.fw.stub.auth.server2.Server2Assert;
import io.extact.msa.rms.platform.fw.stub.auth.server2.Server2Resource;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

/**
 * スタブのPersonアプリを使ってplatform.fwクラスをテストする。
 * <pre>
 * ・スタブアプリ：CID Bean(PersonApi)
 * ・スタブアプリ：CID Bean(PersaonApiProxy)
 * ・スタブアプリ：RestClient(PersonApiRestClient)
 *     ↓ HTTP
 * ・スタブアプリ：RestResource(PersonApplication)
 * ・スタブアプリ：CID Bean(PersonService)
 * ・スタブアプリ：CID Bean(PersonJpaRepository) or (PersonFileRepository)
 * ※ JPAかFileかどちらの実装を使うかはこのクラスのサブクラスで決定
 * </pre>
 */
// for JWT Auth
@HelidonTest(resetPerTest = true)
@AddConfig(key = "jwt.privatekey.path", value = "/jwt.key")
@AddConfig(key = "mp.jwt.verify.publickey.location", value = "/jwt.pub.key")
@AddConfig(key = "security.jersey.enabled", value = "true") // 認証認可ON
// for RESTResrouce Beans
@AddBean(Server1Resource.class)
@AddBean(Server1Application.class)
@AddBean(Auth0RsaJwtGenerator.class)
@AddBean(value = Server1AssertTest.class, scope = Dependent.class)
@AddBean(Server2Resource.class)
@AddBean(Server2Application.class)
@AddBean(Server2ApiRestClient.class)
@AddBean(value = Server2AssertTest.class, scope = Dependent.class)
@AddConfig(key = "server.port", value = "7001") // for PersonResource Server port
// for RESTClient Beans
@AddBean(Server1ApiProxy.class)
@AddBean(Server1ApiRestClient.class)
@AddConfig(key = "configuredCdi.register.0.class", value = "io.extact.msa.rms.platform.fw.external.PropagateJwtClientHeadersFactory")
@AddConfig(key = "web-api/mp-rest/url", value = "http://localhost:7001") // for REST Client
@ExtendWith(JulToSLF4DelegateExtension.class)
public class AuthIntegratinTest {

    private ClientApi api;

    @BeforeEach
    void setup() {
        this.api = CDI.current().select(ClientApi.class).get();
    }

    @Test
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
        public void doGuestApiWithLoginAssert() {
            var actual = LoginUserUtils.get();
            assertThat(actual.isUnknownUser()).isFalse();
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

package io.extact.msa.rms.platform.core.jwt;

import static io.extact.msa.rms.platform.core.jwt.JsonWebTokenIntegrationTest.TestRestClientInterface.*;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.auth.LoginConfig;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.core.debug.ServerHeaderDumpFilter;
import io.extact.msa.rms.platform.core.jwt.JsonWebTokenIntegrationTest.TestLoginApplication;
import io.extact.msa.rms.platform.core.jwt.JsonWebTokenIntegrationTest.TestStubApplication;
import io.extact.msa.rms.platform.core.jwt.provider.GenerateToken;
import io.extact.msa.rms.platform.core.jwt.provider.JwtProvideResponseFilter;
import io.extact.msa.rms.platform.core.jwt.provider.UserClaims;
import io.extact.msa.rms.platform.core.jwt.provider.impl.Auth0RsaJwtGenerator;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest(resetPerTest = true)
@AddConfig(key = "server.port", value = "7001")
@AddConfig(key = "rms.jwt.privatekey.path", value = "/jwt.key")
@AddConfig(key = "mp.jwt.verify.publickey.location", value = "/jwt.pub.key")
@AddBean(TestLoginApplication.class)
@AddBean(TestStubApplication.class)
@AddBean(Auth0RsaJwtGenerator.class)
@ExtendWith(JulToSLF4DelegateExtension.class)
public class JsonWebTokenIntegrationTest {

    private TestRestClientInterface endPoint;
    private static String bearerToken;

    // ----------------------------------------------------- lifecycle methods

    @BeforeEach
    void setup() throws Exception {
        this.endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(TestRestClientInterface.class);
    }

    @AfterEach
    void tearDown() {
        bearerToken = null;
    }

    // ----------------------------------------------------- test methods

    @Test
    void testJwtAuthSuccessSequence() {
        // 認証除外でレスポンスヘッダにJWTが設定される
        endPoint.login(SUCCESS);
        // 受け取ったJWTを認証ヘッダに設定して認証配下のパスにアクセスできること
        endPoint.roleA(SUCCESS);
        // 受け取ったJWTを認証ヘッダに設定して認証除外のパスにもアクセスできること
        endPoint.noRole(SUCCESS);
    }

    @Test
    void testJwtAuthComplexSequence() {

        // ログインが失敗してJWTが設定されない
        WebApplicationException actual = catchThrowableOfType(() ->
            endPoint.login(ERROR),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());

        // JWTがないので認証エラーになる
        actual = catchThrowableOfType(() ->
            endPoint.roleA(SUCCESS),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(UNAUTHORIZED.getStatusCode());

        // JWTがないのでロールなしAPIも認証エラーになる
        actual = catchThrowableOfType(() ->
            endPoint.noRole(SUCCESS),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(UNAUTHORIZED.getStatusCode());

        // 今度はログインが成功してJWTが設定される
        endPoint.login(SUCCESS);
        // 今度はJWTがあるのでエラーにならない
        endPoint.roleA(SUCCESS);    // roleA
        endPoint.noRole(SUCCESS);   // roleなし

        // JWTはあるがroleBは持ってないので認可エラーになる
        actual = catchThrowableOfType(() ->
            endPoint.roleB(SUCCESS),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());

        // エラーがあったアクセスの後もJWTがあれば問題なく成功する
        actual = catchThrowableOfType(() ->
            endPoint.login(ERROR),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());
        endPoint.roleA(SUCCESS);
    }

    @Test
    @AddConfig(key = "security.jersey.enabled", value = "false") // JWT Authの無効化
    void testFilterOff() {
        endPoint.roleA(SUCCESS);
    }


    // ----------------------------------------------------- client side mock classes

    @RegisterProvider(CatchPublishedTokenResponseFilter.class)
    public interface TestRestClientInterface {

        static final String SUCCESS = "success";
        static final String ERROR = "error";

        @GET
        @Path("/unsecure/login")
        @Produces(MediaType.APPLICATION_JSON)
        TestUserClaims login(@QueryParam("pttn") String pttn);

        @GET
        @Path("/secure/stub/roleA")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{bearerToken}")
        @Produces(MediaType.TEXT_PLAIN)
        String roleA(@QueryParam("pttn") String pttn);

        @GET
        @Path("/secure/stub/roleB")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{bearerToken}")
        @Produces(MediaType.TEXT_PLAIN)
        String roleB(@QueryParam("pttn") String pttn);

        @GET
        @Path("/secure/stub/permitAll")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{bearerToken}")
        @Produces(MediaType.TEXT_PLAIN)
        String noRole(@QueryParam("pttn") String pttn);

        default String bearerToken() {
            return bearerToken;
        }
    }


    // ----------------------------------------------------- server side mock classes

    // Register by TestLoginApplication
    @Path("/login")
    public static class TestLoginResource {
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        @GenerateToken
        public TestUserClaims login(@QueryParam("pttn") String pttn) {
            if (pttn.equals(ERROR)) {
                throw new RuntimeException();
            }
            return new TestUserClaims();
        }
    }

    // Register by TestStubApplication
    @Path("/stub")
    public static class TestStubResource {
        @GET
        @Path("/roleA")
        @Produces(MediaType.TEXT_PLAIN)
        @RolesAllowed("roleA")
        public String roleA(@QueryParam("pttn") String pttn) {
            if (pttn.equals(ERROR)) {
                throw new RuntimeException();
            }
            return "success";
        }
        @GET
        @Path("/roleB")
        @Produces(MediaType.TEXT_PLAIN)
        @RolesAllowed("roleB")
        public String roleB(@QueryParam("pttn") String pttn) {
            if (pttn.equals(ERROR)) {
                throw new RuntimeException();
            }
            return "success";
        }
        @GET
        @Path("/permitAll")
        @Produces(MediaType.TEXT_PLAIN)
        public String noRole(@QueryParam("pttn") String pttn) {
            if (pttn.equals(ERROR)) {
                throw new RuntimeException();
            }
            return "success";
        }
    }

    // Register by @AddBenan
    @ApplicationPath("unsecure")
    public static class TestLoginApplication extends Application {
        // RequestFilterやResponseFilterなどのProviderは@AddBeanでは登録されないため
        // Application#getClassesを経由で登録している。またApplication#getClassesを
        // オーバーライドするとResourceクラスの自動登録も行われなくなるためResourceクラスも
        // 併せて登録する必要がある
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(
                        TestLoginResource.class,
                        ServerHeaderDumpFilter.class,
                        JwtProvideResponseFilter.class
                    );
        }
    }

    // Register by @AddBenan
    @ApplicationPath("secure")
    @LoginConfig(authMethod = "MP-JWT")
    public static class TestStubApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(
                    TestStubResource.class,
                    ServerHeaderDumpFilter.class
                );
        }
    }

    // Register by @RegisterProvider
    public static class CatchPublishedTokenResponseFilter implements ClientResponseFilter {
        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            if (!responseContext.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                System.out.println("Authorizationなし");
                return;
            }
            JsonWebTokenIntegrationTest.bearerToken = responseContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        }
    }

    // POJO
    public static class TestUserClaims implements UserClaims {
        @Override
        public String getUserId() {
            return "test";
        }
        @Override
        public String getUserPrincipalName() {
            return "test@test";
        }
        @Override
        public Set<String> getGroups() {
            return Set.of("roleA");
        }
    }
}

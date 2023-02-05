package io.extact.msa.rms.platform.core.health;

import static io.extact.msa.rms.test.assertj.ToStringAssert.*;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.List;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.ws.rs.WebApplicationException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse.Status;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.glassfish.jersey.ext.cdi1x.internal.CdiComponentProvider;
import org.glassfish.jersey.microprofile.restclient.RestClientExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.core.health.DbReadinessCheckTest.DbReadinessCheckTestWrapper;
import io.extact.msa.rms.platform.core.health.client.GenericCheckResponse;
import io.extact.msa.rms.platform.core.health.client.ReadnessCheckRestClient;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.extact.msa.rms.test.utils.TestUtils;
import io.helidon.microprofile.config.ConfigCdiExtension;
import io.helidon.microprofile.health.HealthCdiExtension;
import io.helidon.microprofile.server.JaxRsCdiExtension;
import io.helidon.microprofile.server.ServerCdiExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.AddExtension;
import io.helidon.microprofile.tests.junit5.DisableDiscovery;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest(resetPerTest = true)
@DisableDiscovery
@AddExtension(ServerCdiExtension.class)
@AddExtension(JaxRsCdiExtension.class)
@AddExtension(RestClientExtension.class)
@AddExtension(ConfigCdiExtension.class)
@AddExtension(CdiComponentProvider.class)
@ExtendWith(JulToSLF4DelegateExtension.class)
@AddConfig(key = "server.port", value = "7001")
// ---- following specific parts
@AddExtension(HealthCdiExtension.class)
@AddBean(DbReadinessCheckTestWrapper.class)
@AddConfig(key = "health.timeout-millis", value = "3600000")
class DbReadinessCheckTest {

    private ReadnessCheckRestClient client;

    @BeforeEach
    void setup() throws Exception {
        this.client = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(ReadnessCheckRestClient.class);
    }

    @Test
    @AddConfig(key = "rms.healthCheck.dbReadnessCheck.pingSql", value = "SELECT 1")
    void testProbeReadnessOk() {
        var expectedOfCheck = new GenericCheckResponse.Check();
        expectedOfCheck.setStatus(Status.UP.name());
        expectedOfCheck.setName(DbReadinessCheck.class.getSimpleName());

        var expected = new GenericCheckResponse();
        expected.setStatus(Status.UP.name());
        expected.setChecks(List.of(expectedOfCheck));

        var actual = client.probeReadness();
        assertThatToString(actual).isEqualTo(expected);
        System.out.println(actual);
    }

    @Test
    @AddConfig(key = "rms.healthCheck.dbReadnessCheck.pingSql", value = "SQL ERROR")
    void testProbeReadnessNg() {

        var thrown = catchThrowableOfType(() -> client.probeReadness(), WebApplicationException.class);
        assertThat(thrown).isNotNull();
        assertThat(thrown.getResponse().getStatus()).isEqualTo(SERVICE_UNAVAILABLE.getStatusCode());

        // check body data.
        var expectedOfCheck = new GenericCheckResponse.Check();
        expectedOfCheck.setStatus(Status.DOWN.name());
        expectedOfCheck.setName(DbReadinessCheck.class.getSimpleName());

        var expected = new GenericCheckResponse();
        expected.setStatus(Status.DOWN.name());
        expected.setChecks(List.of(expectedOfCheck));

        var actual = thrown.getResponse().readEntity(GenericCheckResponse.class);
        assertThatToString(actual).isEqualTo(expected);
    }

    // ----------------------------------------------------- stub inner classes.

    // @PersistenceContextは@Producerでは差し込めないのWrapperを被せて差し込んでいる
    static class DbReadinessCheckTestWrapper {
        private DbReadinessCheck subject;

        @Inject
        public DbReadinessCheckTestWrapper(
                @ConfigProperty(name = "rms.healthCheck.dbReadnessCheck.pingSql") String pingSql) {
            var temp = new DbReadinessCheck(pingSql);
            EntityManager emProxy = (EntityManager) Proxy.newProxyInstance(
                    this.getClass().getClassLoader(),
                    new Class[] { EntityManager.class },
                    new NopInvocationHandler());
            TestUtils.setFieldValue(temp, "em", emProxy);
            this.subject = temp;
        }

        @Produces
        @Readiness
        public HealthCheck checkReadiness() {
            return subject.checkReadiness();
        }
    }

    static class NopInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("createNativeQuery")) {
                if (((String) args[0]).toLowerCase().equals("select 1")) {
                    return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                            new Class[] { Query.class }, this);
                }
                throw new PersistenceException("sql:" + args[0]);
            }
            return null;
        }
    }
}

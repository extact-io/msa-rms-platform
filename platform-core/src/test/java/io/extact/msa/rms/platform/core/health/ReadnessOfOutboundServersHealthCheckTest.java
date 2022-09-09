package io.extact.msa.rms.platform.core.health;

import static io.extact.msa.rms.test.assertj.ToStringAssert.*;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.eclipse.microprofile.health.HealthCheckResponse.Status;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.glassfish.jersey.ext.cdi1x.internal.CdiComponentProvider;
import org.glassfish.jersey.microprofile.restclient.RestClientExtension;
import org.jboss.weld.exceptions.UnsupportedOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.core.health.ReadnessOfOutboundServersHealthCheckTest.ReadnessCheckRestClientFactoryStub;
import io.extact.msa.rms.platform.core.health.client.GenericCheckResponse;
import io.extact.msa.rms.platform.core.health.client.ReadnessCheckRestClient;
import io.extact.msa.rms.platform.core.health.client.ReadnessCheckRestClientFactory;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.config.ConfigCdiExtension;
import io.helidon.microprofile.health.HealthCdiExtension;
import io.helidon.microprofile.server.JaxRsCdiExtension;
import io.helidon.microprofile.server.ServerCdiExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.AddExtension;
import io.helidon.microprofile.tests.junit5.DisableDiscovery;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.WebApplicationException;

/**
 * ネットワーク越しにApplicaitonResouceをテストするテストケース。
 * <pre>
 * ・テストドライバ：RestClient(ReadnessCheckRestClient)
 *     ↓ HTTP(7001)
 * ・実物：MicroProfile Health(ReadnessOfOutboundServersHealthCheck)
 * ・スタブ：RestResource(ReadnessCheckRestClientFactoryStub)
 * </pre>
 */
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
@AddBean(ReadnessOfOutboundServersHealthCheck.class)
@AddBean(value = ReadnessCheckRestClientFactoryStub.class, scope = Dependent.class)
class ReadnessOfOutboundServersHealthCheckTest {

    private ReadnessCheckRestClient client;

    @BeforeEach
    void setup() throws Exception {
        this.client =  RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(ReadnessCheckRestClient.class);
    }

    @Test
    @AddConfig(key = "healthCheck.readnessOfOutboundServersHealthCheck.probe.url.0", value = "http://localhost:8001")
    @AddConfig(key = "healthCheck.readnessOfOutboundServersHealthCheck.probe.url.1", value = "http://localhost:8002")
    void testProbeReadnessOk() {
        var expectedOfCheck = new GenericCheckResponse.Check();
        expectedOfCheck.setStatus(Status.UP.name());
        expectedOfCheck.setName(ReadnessOfOutboundServersHealthCheck.class.getSimpleName());
        Map<String, Object> data = new TreeMap<>(Map.of( // assertしやすいように並びを固定化
                "http://localhost:8001", Status.UP.name(),
                "http://localhost:8002", Status.UP.name()));
        expectedOfCheck.setData(data);

        var expected = new GenericCheckResponse();
        expected.setStatus(Status.UP.name());
        expected.setChecks(List.of(expectedOfCheck));

        var actual = client.probeReadnessAsync().toCompletableFuture().join();
        actual.getChecks().forEach(check -> {
           check.setData(new TreeMap<>(check.getData())); // assertしやすいように並びを固定化
        });

        assertThatToString(actual).isEqualTo(expected);
    }

    @Test
    @AddConfig(key = "healthCheck.readnessOfOutboundServersHealthCheck.probe.url.0", value = "http://localhost:8001")
    @AddConfig(key = "healthCheck.readnessOfOutboundServersHealthCheck.probe.url.1", value = "http://localhost:9999") // NG server
    void testProbeReadnessNg() {

        var thrown = catchThrowableOfType(
                () -> client.probeReadnessAsync().toCompletableFuture().join(),
                CompletionException.class);
        assertThat(thrown).isNotNull();
        var cause = thrown.getCause();
        assertThat(thrown.getCause()).isNotNull().isInstanceOf(WebApplicationException.class);
        assertThat(((WebApplicationException) cause).getResponse().getStatus())
                .isEqualTo(SERVICE_UNAVAILABLE.getStatusCode());

        // check body data.
        var expectedOfCheck = new GenericCheckResponse.Check();
        expectedOfCheck.setStatus(Status.DOWN.name());
        expectedOfCheck.setName(ReadnessOfOutboundServersHealthCheck.class.getSimpleName());
        Map<String, Object> data = new TreeMap<>(Map.of( // assertしやすいように並びを固定化
                "http://localhost:8001", Status.UP.name(),
                "http://localhost:9999", Status.DOWN.name()));
        expectedOfCheck.setData(data);

        var expected = new GenericCheckResponse();
        expected.setStatus(Status.DOWN.name());
        expected.setChecks(List.of(expectedOfCheck));

        var actual = ((WebApplicationException)cause).getResponse().readEntity(GenericCheckResponse.class);
        actual.getChecks().forEach(check -> {
            check.setData(new TreeMap<>(check.getData())); // assertしやすいように並びを固定化
         });
        assertThatToString(actual).isEqualTo(expected);
    }

    // ----------------------------------------------------- stub inner classes.

    static class ReadnessCheckRestClientFactoryStub implements ReadnessCheckRestClientFactory {
        @Override
        public ReadnessCheckRestClient create(String baseUrl) {
            return new ReadnessCheckRestClientStub(baseUrl.endsWith("9999"));
        }
    }

    static class ReadnessCheckRestClientStub implements ReadnessCheckRestClient {
        private boolean error;

        public ReadnessCheckRestClientStub(boolean error) {
            this.error = error;
        }

        @Override
        public CompletableFuture<GenericCheckResponse> probeReadnessAsync() {
            return CompletableFuture
                    .supplyAsync(() -> {
                        var check = new GenericCheckResponse.Check();
                        check.setStatus(error ? Status.DOWN.name() : Status.UP.name());
                        check.setName(SimpleReadnessCheck.class.getSimpleName());
                        var res = new GenericCheckResponse();
                        res.setStatus(check.getStatus());
                        res.setChecks(List.of(check));
                        return res;
                    });
        }

        @Override
        public GenericCheckResponse probeReadness() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() throws Exception {
            // nop
        }
    }
}

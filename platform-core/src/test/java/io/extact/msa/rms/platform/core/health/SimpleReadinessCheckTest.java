package io.extact.msa.rms.platform.core.health;

import static org.assertj.core.api.Assertions.*;

import org.eclipse.microprofile.health.HealthCheckResponse.Status;
import org.glassfish.jersey.ext.cdi1x.internal.CdiComponentProvider;
import org.glassfish.jersey.microprofile.restclient.RestClientExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.core.health.client.ReadnessCheckRestClient;
import io.extact.msa.rms.platform.core.health.client.ReadnessCheckRestClientFactory;
import io.extact.msa.rms.platform.core.health.client.ReadnessCheckRestClientFactoryImpl;
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
import jakarta.inject.Inject;

@HelidonTest
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
@AddBean(SimpleReadinessCheck.class)
@AddBean(ReadnessCheckRestClientFactoryImpl.class)
class SimpleReadinessCheckTest {

    @Inject
    private ReadnessCheckRestClientFactory factory;
    private ReadnessCheckRestClient client;

    @BeforeEach
    void setup() throws Exception {
        this.client = factory.create("http://localhost:7001");
    }

    @Test
    void testProbeReadness() {
        var checkResponse = client.probeReadness();
        assertThat(checkResponse.getStatus()).isEqualTo(Status.UP.name());
    }
}

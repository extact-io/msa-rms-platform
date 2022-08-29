package io.extact.msa.rms.platform.core.health;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponse.Status;
import org.eclipse.microprofile.health.Readiness;

import io.extact.msa.rms.platform.core.health.client.GenericCheckResponse;
import io.extact.msa.rms.platform.core.health.client.ReadnessCheckRestClientFactory;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

//Bean registered by HealthCheckRegisterExtensions
@Slf4j
public class RestServerReadnessCheck {

    private static final String PROBE_URL_CONFIG_KEY = "healthCheck.restServerReadnessCheck.probe.url";
    private ReadnessCheckRestClientFactory factory;
    private List<String> probeUrls;

    @Inject
    public RestServerReadnessCheck(
            ReadnessCheckRestClientFactory factory,
            @ConfigProperty(name = PROBE_URL_CONFIG_KEY) List<String> probeUrls) {
        this.factory = factory;
        this.probeUrls = probeUrls;
    }

    @Produces
    @Readiness
    public HealthCheck checkReadiness() {
        return () -> {

            List<CheckResult> results = probeUrls.stream()
                    .map(this::probeReadness)
                    .toList();

            boolean hasError = results.stream().anyMatch(CheckResult::isDown);

            var response = HealthCheckResponse.named(this.getClass().getSimpleName()).status(!hasError);
            results.forEach(result -> {
                response.withData(result.probeUrl(), result.checkResponse().getStatus());
            });
            return response.build();
        };
    }

    private CheckResult probeReadness(String probeUrl) {
        var client = factory.create(probeUrl);
        GenericCheckResponse checkResponse;
        try {
            checkResponse = client.probeReadness();
        } catch (Exception e) {
            log.warn("occur exception. probe.url=[" + probeUrl + "]", e);
            checkResponse = new GenericCheckResponse();
            checkResponse.setStatus(Status.DOWN.name());
        }
        return new CheckResult(probeUrl, checkResponse);
    }

    private record CheckResult(String probeUrl, GenericCheckResponse checkResponse) {
        boolean isDown() {
            return this.checkResponse.isDown();
        }
    }
}

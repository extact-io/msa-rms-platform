package io.extact.msa.rms.platform.core.health;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponse.Status;
import org.eclipse.microprofile.health.Readiness;

import io.extact.msa.rms.platform.core.health.client.GenericCheckResponse;
import io.extact.msa.rms.platform.core.health.client.ReadnessCheckRestClient;
import io.extact.msa.rms.platform.core.health.client.ReadnessCheckRestClientFactory;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

//Bean registered by HealthCheckRegisterExtensions
@Slf4j
public class ReadnessOfOutboundServersHealthCheck {

    private static final String PROBE_URL_CONFIG_KEY = "healthCheck.readnessOfOutboundServersHealthCheck.probe.url";
    private ReadnessCheckRestClientFactory factory;
    private List<String> probeUrls;

    @Inject
    public ReadnessOfOutboundServersHealthCheck(
            ReadnessCheckRestClientFactory factory,
            @ConfigProperty(name = PROBE_URL_CONFIG_KEY) List<String> probeUrls) {
        this.factory = factory;
        this.probeUrls = probeUrls;
    }

    @Produces
    @Readiness
    public HealthCheck checkReadiness() {
        return () -> {
            List<CheckTask> tasks = null;
            try {
                // createTask and execute async job
                tasks = probeUrls.stream()
                        .map(url -> {
                            var client = factory.create(url);
                            return new CheckTask(url, client); // execute async
                        })
                        .toList();

                // convert to future array from task list
                @SuppressWarnings({ "unchecked" })
                CompletableFuture<GenericCheckResponse>[] futureArray = tasks.stream()
                        .map(CheckTask::getFuture)
                        .toArray(CompletableFuture[]::new);

                // join thread
                final var finalTasks = tasks; // for lambda ref.
                CompletableFuture<Void> promise = CompletableFuture.allOf(futureArray);
                List<CheckResult> results = promise.thenApply(dummy -> {
                    return finalTasks.stream()
                            .map(CheckTask::getResult)
                            .toList();
                }).join();

                // build response
                boolean hasError = results.stream().anyMatch(result -> result.checkResponse().isDown());
                var responseBuilder = HealthCheckResponse.named(this.getClass().getSimpleName()).status(!hasError);
                results.forEach(result -> {
                    responseBuilder.withData(result.probeUrl(), result.checkResponse().getStatus());
                });
                return responseBuilder.build();
            } finally {
                if (tasks != null) {
                    tasks.forEach(CheckTask::close);
                }
            }
        };
    }


    // -------------------------------------------------------------- inner classes.

    @Getter
    static class CheckTask {

        String probeUrl;
        ReadnessCheckRestClient client;
        CompletableFuture<GenericCheckResponse> future;

        CheckTask(String probeUrl, ReadnessCheckRestClient client) {
            this.probeUrl = probeUrl;
            this.client = client;
            this.future = client.probeReadnessAsync().toCompletableFuture();
        }

        CheckResult getResult() {
            try {
                var response = future.join();
                return new CheckResult(probeUrl, response);
            } catch (Exception e) {
                log.warn("occur exception. probe.url=[" + probeUrl + "]", e);
                var response = new GenericCheckResponse();
                response.setStatus(Status.DOWN.name());
                return new CheckResult(probeUrl, response);
            }
        }

        void close() {
            try {
                client.close();
            } catch (Exception e) {
                log.warn("occur execption", e);
            }
        }
    }

    private record CheckResult(String probeUrl, GenericCheckResponse checkResponse) {
    }
}

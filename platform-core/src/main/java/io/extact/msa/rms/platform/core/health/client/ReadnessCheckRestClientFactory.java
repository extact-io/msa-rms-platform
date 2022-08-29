package io.extact.msa.rms.platform.core.health.client;

public interface ReadnessCheckRestClientFactory {
    ReadnessCheckRestClient create(String baseUrl);
}

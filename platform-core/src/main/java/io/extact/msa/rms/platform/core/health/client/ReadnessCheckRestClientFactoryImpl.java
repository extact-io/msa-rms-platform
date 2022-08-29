package io.extact.msa.rms.platform.core.health.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

import jakarta.enterprise.context.Dependent;

@Dependent
public class ReadnessCheckRestClientFactoryImpl implements ReadnessCheckRestClientFactory {

    @Override
    public ReadnessCheckRestClient create(String baseUrl) {
        try {
            return RestClientBuilder.newBuilder()
                    .baseUri(new URI(baseUrl))
                    .build(ReadnessCheckRestClient.class);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}

package io.extact.msa.rms.platform.core.health.client;

import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/health/ready")
public interface ReadnessCheckRestClient extends AutoCloseable {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<GenericCheckResponse> probeReadnessAsync();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    GenericCheckResponse probeReadness();
}

package io.extact.msa.rms.platform.core.health.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/health/ready")
public interface ReadnessCheckRestClient {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    GenericCheckResponse probeReadness();
}

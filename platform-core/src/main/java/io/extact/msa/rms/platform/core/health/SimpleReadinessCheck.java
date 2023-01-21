package io.extact.msa.rms.platform.core.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import jakarta.enterprise.inject.Produces;

//Bean registered by HealthCheckRegisterExtensions
public class SimpleReadinessCheck {
    @Produces
    @Readiness
    public HealthCheck checkReadiness() {
        return () -> HealthCheckResponse.named(this.getClass().getSimpleName()).up().build();
    }
}

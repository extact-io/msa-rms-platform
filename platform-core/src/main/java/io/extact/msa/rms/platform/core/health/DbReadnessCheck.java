package io.extact.msa.rms.platform.core.health;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

//Bean registered by HealthCheckRegisterExtensions
@Slf4j
public class DbReadnessCheck {

    @PersistenceContext
    private EntityManager em;
    private String pingSql;

    @Inject
    public DbReadnessCheck(@ConfigProperty(name = "healthCheck.dbReadnessCheck.pingSql") String pingSql) {
        this.pingSql = pingSql;
    }

    @Produces
    @Readiness
    public HealthCheck checkReadiness() {
        return () -> {
            var responseBuilder = HealthCheckResponse.named(this.getClass().getSimpleName());
            try {
                em.createNativeQuery(pingSql).getSingleResult();
                return responseBuilder.up().build();
            } catch (Exception e) {
                log.warn("DbReadnessCheck failed", e);
                return responseBuilder.down().build();
            }
        };
    }
}

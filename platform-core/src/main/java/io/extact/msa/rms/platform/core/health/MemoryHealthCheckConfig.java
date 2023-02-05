package io.extact.msa.rms.platform.core.health;

import java.util.Optional;

import jakarta.enterprise.context.Dependent;

import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "rms.healthCheck.memoryHealth")
@Dependent
public class MemoryHealthCheckConfig {
    private Optional<Boolean> enable;
    @ConfigProperty(name = "memoryLiveness.name")
    private Optional<String> livenessName;
    @ConfigProperty(name = "memoryLiveness.method")
    private Optional<String> livenessMethod;
    @ConfigProperty(name = "memoryLiveness.threshold")
    private Optional<Long> livenessThreshold;
    @ConfigProperty(name = "memoryReadiness.name")
    private Optional<String> readnessName;

    public boolean enable() {
        return enable.orElse(false);
    }

    public String getLivenessName() {
        return livenessName.orElseThrow(IllegalStateException::new);
    }

    public String getLivenessMethod() {
        return livenessMethod.orElseThrow(IllegalStateException::new);
    }

    public long getLivenessThreshold() {
        return livenessThreshold.orElseThrow(IllegalStateException::new);
    }

    public String getReadnessName() {
        return readnessName.orElseThrow(IllegalStateException::new);
    }

}

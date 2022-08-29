package io.extact.msa.rms.platform.core.health;

import java.util.Collections;

import org.eclipse.microprofile.config.ConfigProvider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.configurator.AnnotatedTypeConfigurator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HealthCheckRegisterExtension implements Extension {

    void registerHealthCheckBeans(@Observes BeforeBeanDiscovery event) {
        var config = ConfigProvider.getConfig();

        // for memoryHealth config
        var enableMemoryHealth = config.getOptionalValue("healthCheck.memoryHealth.enable", boolean.class)
                .orElse(false);
        if (enableMemoryHealth) {
            // register MemoryHealthCheckBean
            AnnotatedTypeConfigurator<?> configurator = event.addAnnotatedType(MemoryHealthCheck.class, MemoryHealthCheck.class.getSimpleName());
            configurator.add(ApplicationScoped.Literal.INSTANCE);
            // register MemoryEvaluateResource
            configurator = event.addAnnotatedType(MemoryEvaluateResource.class, MemoryEvaluateResource.class.getSimpleName());
            configurator.add(ApplicationScoped.Literal.INSTANCE);
            log.info("MemoryHealthCheckをCDIBean登録しました");
        }

        // for otherChecks config
        var otherChecks = config.getOptionalValues("healthCheck.otherChecks", Class.class).orElse(Collections.emptyList());
        otherChecks.forEach(otherCheck -> {
            @SuppressWarnings("unchecked")
            AnnotatedTypeConfigurator<?> configurator = event.addAnnotatedType(otherCheck, otherCheck.getSimpleName());
            configurator.add(ApplicationScoped.Literal.INSTANCE);
            log.info("healthCheck.otherChecks設定により[{}]をCDIBean登録しました", otherCheck.getName());
        });
    }
}

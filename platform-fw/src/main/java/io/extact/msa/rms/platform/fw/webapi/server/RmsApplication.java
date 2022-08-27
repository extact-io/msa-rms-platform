package io.extact.msa.rms.platform.fw.webapi.server;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.core.Application;

import io.extact.msa.rms.platform.core.debug.ServerHeaderDumpFilter;
import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;

public abstract class RmsApplication extends Application implements CommonOpenApiDefinition {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new LinkedHashSet<>();
        // common providers
        classes.add(RmsTypeParameterFeature.class);
        classes.add(ServerExceptionMapperFeature.class);
        classes.add(ServerHeaderDumpFilter.class);
        // application resource and providers
        getWebApiClasses().forEach(classes::add);
        return classes;
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new LinkedHashMap<>();
        // common properties
        properties.put(
                // The following keys are defined in `ServerProperties.BV_SEND_ERROR_IN_RESPONSE`
                "jersey.config.beanValidation.disable.server", true  // jerseyのJAX-RSのBeanValidationサポートをOFFにする
                );
        // application properties
        getWebApiPrpperties().forEach(properties::put);
        return properties;
    }

    protected abstract Set<Class<?>> getWebApiClasses();

    protected Map<String, Object> getWebApiPrpperties() {
        return Map.of();
    }
}

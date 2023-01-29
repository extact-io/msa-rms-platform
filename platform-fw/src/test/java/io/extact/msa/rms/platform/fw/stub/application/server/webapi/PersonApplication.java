package io.extact.msa.rms.platform.fw.stub.application.server.webapi;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Application;

import io.extact.msa.rms.platform.fw.login.LoginUserFromHttpHeadersRequestFilter;
import io.extact.msa.rms.platform.fw.webapi.RmsBaseApplications;

@ApplicationScoped
public class PersonApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new LinkedHashSet<>();
        classes.addAll(RmsBaseApplications.CLASSES);
        classes.addAll(getWebApiClasses());
        return classes;
    }

    @Override
    public Map<String, Object> getProperties() {
        return RmsBaseApplications.PROPERTIES;
    }

    private Set<Class<?>> getWebApiClasses() {
        return Set.of(
                    LoginUserFromHttpHeadersRequestFilter.class,
                    PersonResource.class
                );
    }
}

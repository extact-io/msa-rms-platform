package io.extact.msa.rms.platform.fw.stub.auth.server1;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Application;

import org.eclipse.microprofile.auth.LoginConfig;

import io.extact.msa.rms.platform.core.jwt.provider.JwtProvideResponseFilter;
import io.extact.msa.rms.platform.fw.login.LoginUserFromJwtRequestFilter;
import io.extact.msa.rms.platform.fw.webapi.RmsBaseApplications;

@ApplicationScoped
@LoginConfig(authMethod = "MP-JWT")
public class Server1Application extends Application {

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
                JwtProvideResponseFilter.class,
                LoginUserFromJwtRequestFilter.class,
                Server1Resource.class);
    }
}

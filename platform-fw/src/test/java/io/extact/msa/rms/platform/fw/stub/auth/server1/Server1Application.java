package io.extact.msa.rms.platform.fw.stub.auth.server1;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.auth.LoginConfig;

import io.extact.msa.rms.platform.core.jwt.provider.JwtProvideResponseFilter;
import io.extact.msa.rms.platform.fw.login.LoginUserFromJwtRequestFilter;
import io.extact.msa.rms.platform.fw.webapi.RmsApplication;

@ApplicationScoped
@LoginConfig(authMethod = "MP-JWT")
public class Server1Application extends RmsApplication {

    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                JwtProvideResponseFilter.class,
                LoginUserFromJwtRequestFilter.class,
                Server1Resource.class);
    }
}

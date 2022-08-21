package io.extact.msa.rms.platform.fw.stub.auth.server1;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import io.extact.msa.rms.platform.core.jwt.JwtSecurityFilterFeature;
import io.extact.msa.rms.platform.core.jwt.login.LoginUserRequestFilter;
import io.extact.msa.rms.platform.core.role.RoleSecurityDynamicFeature;
import io.extact.msa.rms.platform.fw.webapi.server.RmsApplication;

@ApplicationScoped
public class Server1Application extends RmsApplication {

    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                JwtSecurityFilterFeature.class,
                RoleSecurityDynamicFeature.class,
                LoginUserRequestFilter.class,
                Server1Resource.class);
    }
}

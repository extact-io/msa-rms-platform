package io.extact.msa.rms.platform.fw.stub.auth.server2;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;

import io.extact.msa.rms.platform.fw.login.LoginUserFromHttpHeadersRequestFilter;
import io.extact.msa.rms.platform.fw.webapi.RmsApplication;

@ApplicationScoped
public class Server2Application extends RmsApplication {

    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                LoginUserFromHttpHeadersRequestFilter.class,
                Server2Resource.class);
    }
}

package io.extact.msa.rms.platform.fw.stub.application.server.webapi;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;

import io.extact.msa.rms.platform.fw.login.LoginUserFromHttpHeadersRequestFilter;
import io.extact.msa.rms.platform.fw.webapi.RmsApplication;

@ApplicationScoped
public class PersonApplication extends RmsApplication {

    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                    LoginUserFromHttpHeadersRequestFilter.class,
                    PersonResource.class
                );
    }
}

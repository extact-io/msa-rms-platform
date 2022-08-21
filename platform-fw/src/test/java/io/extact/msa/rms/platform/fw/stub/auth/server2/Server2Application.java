package io.extact.msa.rms.platform.fw.stub.auth.server2;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import io.extact.msa.rms.platform.core.jwt.login.PropagatedLoginHeaderRequestFilter;
import io.extact.msa.rms.platform.fw.webapi.server.RmsApplication;

@ApplicationScoped
public class Server2Application extends RmsApplication {

    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                PropagatedLoginHeaderRequestFilter.class,
                Server2Resource.class);
    }
}

package io.extact.msa.rms.platform.fw.exception.interceptor;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.eclipse.microprofile.config.Config;

import io.extact.msa.rms.platform.fw.exception.RmsNetworkConnectionException;
import lombok.extern.slf4j.Slf4j;

@Interceptor
@Priority(Interceptor.Priority.PLATFORM_AFTER + 1000)
@NetworkConnectionErrorAware
@Slf4j
public class NetworkConnectionErrorInterceptor {
    
    private Config config;
    
    @Inject
    public NetworkConnectionErrorInterceptor(Config config) {
        this.config = config;
    }

    @AroundInvoke
    public Object obj(InvocationContext ic) throws Exception {
        try {
            return ic.proceed();
        } catch (Exception original) {
            Throwable test = original;
            while (!(test.getClass().getPackage().getName().equals("java.net"))) {
                test = test.getCause();
                if (test == null) {
                    throw original;
                }
            }
            var message = makeErrorInfoMessage(ic);
            log.warn(message, original);
            throw new RmsNetworkConnectionException(message, original);
        }
    }

    private String makeErrorInfoMessage(InvocationContext ic) {
        var sourceAppName = config.getValue("rms.app.name", String.class);
        var destinationClass = ic.getTarget().getClass().getSimpleName();
        return String.format("Network error on call from %s to %s", sourceAppName, destinationClass);
    }
}

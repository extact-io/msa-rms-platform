package io.extact.msa.rms.platform.core.debug;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InterceptorBinding;
import jakarta.interceptor.InvocationContext;

import org.eclipse.microprofile.config.Config;

import io.extact.msa.rms.platform.core.debug.DebugSleepInterceptor.DebugSleep;
import lombok.extern.slf4j.Slf4j;

@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@DebugSleep
@Slf4j
public class DebugSleepInterceptor {

    private boolean sleepEnable;
    private int sleepTime;

    @Inject
    public DebugSleepInterceptor(Config config) {
        this.sleepEnable = config.getOptionalValue("rms.debug.sleep.enable", boolean.class).orElse(false);
        this.sleepTime = config.getOptionalValue("rms.debug.sleep.time", int.class).orElse(0);
    }

    @AroundInvoke
    public Object obj(InvocationContext ic) throws Exception {
        if (sleepEnable) {
            log.info("start debug sleep[{}msec]......", this.sleepTime);
            Thread.sleep(sleepTime);
            log.info("end debug sleep.");
        }
        return ic.proceed();
    }

    @Inherited
    @InterceptorBinding
    @Retention(RUNTIME)
    @Target({ METHOD, TYPE })
    public @interface DebugSleep {

    }
}

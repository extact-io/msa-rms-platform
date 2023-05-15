package io.extact.msa.rms.platform.fw.exception.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletionException;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@ExceptionUnwrapAware
public class ExceptionUnwrapInterceptor {
    @AroundInvoke
    public Object obj(InvocationContext ic) throws Throwable {
        try {
            return ic.proceed();
        } catch (CompletionException e) {
            throw e.getCause();
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}

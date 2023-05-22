package io.extact.msa.rms.platform.fw.webapi;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

import io.extact.msa.rms.platform.core.jaxrs.mapper.PageNotFoundExceptionMapper;
import io.extact.msa.rms.platform.core.jaxrs.mapper.UnhandledExceptionMapper;
import io.extact.msa.rms.platform.fw.webapi.ServerExceptionMappers.BusinessFlowExceptionMapper;
import io.extact.msa.rms.platform.fw.webapi.ServerExceptionMappers.CircuitBreakerOpenExceptionMapper;
import io.extact.msa.rms.platform.fw.webapi.ServerExceptionMappers.ConstraintExceptionMapper;
import io.extact.msa.rms.platform.fw.webapi.ServerExceptionMappers.RmsServiceUnavailableExceptionMapper;
import io.extact.msa.rms.platform.fw.webapi.ServerExceptionMappers.RmsSystemExceptionMapper;
import io.extact.msa.rms.platform.fw.webapi.ServerExceptionMappers.SecurityConstraintExceptionMapper;

@ConstrainedTo(RuntimeType.SERVER)
public class ServerExceptionMapperFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(BusinessFlowExceptionMapper.class);
        context.register(RmsServiceUnavailableExceptionMapper.class);
        context.register(CircuitBreakerOpenExceptionMapper.class);
        context.register(RmsSystemExceptionMapper.class);
        context.register(ConstraintExceptionMapper.class);
        context.register(SecurityConstraintExceptionMapper.class);
        context.register(PageNotFoundExceptionMapper.class);
        context.register(UnhandledExceptionMapper.class);
        return true;
    }
}

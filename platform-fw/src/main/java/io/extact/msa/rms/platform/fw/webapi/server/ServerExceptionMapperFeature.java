package io.extact.msa.rms.platform.fw.webapi.server;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

import io.extact.msa.rms.platform.core.jaxrs.mapper.PageNotFoundExceptionMapper;
import io.extact.msa.rms.platform.core.jaxrs.mapper.UnhandledExceptionMapper;
import io.extact.msa.rms.platform.fw.webapi.server.ServerExceptionMappers.BusinessFlowExceptionMapper;
import io.extact.msa.rms.platform.fw.webapi.server.ServerExceptionMappers.ConstraintExceptionMapper;
import io.extact.msa.rms.platform.fw.webapi.server.ServerExceptionMappers.RmsSystemExceptionMapper;
import io.extact.msa.rms.platform.fw.webapi.server.ServerExceptionMappers.SecurityConstraintExceptionMapper;

@ConstrainedTo(RuntimeType.SERVER)
public class ServerExceptionMapperFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(BusinessFlowExceptionMapper.class);
        context.register(RmsSystemExceptionMapper.class);
        context.register(ConstraintExceptionMapper.class);
        context.register(SecurityConstraintExceptionMapper.class);
        context.register(PageNotFoundExceptionMapper.class);
        context.register(UnhandledExceptionMapper.class);
        return true;
    }
}

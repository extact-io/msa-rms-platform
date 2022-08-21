package io.extact.msa.rms.platform.fw.webapi.server;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import io.extact.msa.rms.platform.core.jaxrs.mapper.PageNotFoundExceptionMapper;
import io.extact.msa.rms.platform.core.jaxrs.mapper.UnhandledExceptionMapper;
import io.extact.msa.rms.platform.fw.webapi.server.ServerExceptionMappers.BusinessFlowExceptionMapper;
import io.extact.msa.rms.platform.fw.webapi.server.ServerExceptionMappers.ConstraintExceptionMapper;
import io.extact.msa.rms.platform.fw.webapi.server.ServerExceptionMappers.RmsSystemExceptionMapper;

@ConstrainedTo(RuntimeType.SERVER)
public class ServerExceptionMapperFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(BusinessFlowExceptionMapper.class);
        context.register(RmsSystemExceptionMapper.class);
        context.register(ConstraintExceptionMapper.class);
        context.register(PageNotFoundExceptionMapper.class);
        context.register(UnhandledExceptionMapper.class);
        return true;
    }
}

package io.extact.msa.rms.platform.core.jwt.login;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Priorities;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link PropagatedLoginHeaderRequestFilter}と対になるサーバ側のファイルター
 */
@Priority(Priorities.AUTHENTICATION)
@ConstrainedTo(RuntimeType.SERVER)
@Slf4j
public class PropagatedLoginHeaderRequestFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Context
    private HttpHeaders headers;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!headers.getRequestHeaders().containsKey("rms-userId")) {
            LoginUserUtils.set(ServiceLoginUser.UNKNOWN_USER);
            return;
        }
        var userId = headers.getRequestHeaders().getFirst("rms-userId");
        var roles = headers.getRequestHeaders().getFirst("rms-roles").transform(values -> values.split(","));
        var loginUser = ServiceLoginUser.of(Integer.valueOf(userId), Stream.of(roles).collect(Collectors.toSet()));
        LoginUserUtils.set(loginUser);
        log.debug("set loginUser to ThradLocal");
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        log.debug("remove loginUser from ThradLocal");
        LoginUserUtils.remove();
    }
}

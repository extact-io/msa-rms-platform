package io.extact.msa.rms.platform.fw.login;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

import io.extact.msa.rms.platform.fw.external.PropagateLoginUserClientHeadersFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link PropagateLoginUserClientHeadersFactory}と対になるサーバ側のファイルター
 */
@Priority(Priorities.AUTHENTICATION)
@ConstrainedTo(RuntimeType.SERVER)
@Slf4j
public class LoginUserFromHttpHeadersRequestFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Context
    private HttpHeaders headers;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!headers.getRequestHeaders().containsKey("rms-userId")) {
            LoginUserUtils.set(LoginUserImpl.UNKNOWN_USER);
            return;
        }
        var userId = headers.getRequestHeaders().getFirst("rms-userId");
        var roles = headers.getRequestHeaders().getFirst("rms-roles").transform(values -> values.split(","));
        var loginUser = LoginUserImpl.of(Integer.valueOf(userId), Stream.of(roles).collect(Collectors.toSet()));
        LoginUserUtils.set(loginUser);
        log.debug("set loginUser to ThradLocal");
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        log.debug("remove loginUser from ThradLocal");
        LoginUserUtils.remove();
    }
}

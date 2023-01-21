package io.extact.msa.rms.platform.fw.login;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

import org.eclipse.microprofile.jwt.JsonWebToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 検証済み{@link JsonWebToken}から{@link LoginUserImpl}を生成し<code>ThreadLocal</code>
 * に設定するフィルタークラス。
 * このフィルターは前段にMP-JWTによる認証が実行されていることを前提にしている。
 */
@Priority(Priorities.AUTHENTICATION + 10) // MP-JWTの後
@ConstrainedTo(RuntimeType.SERVER)
@Slf4j
public class LoginUserFromJwtRequestFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    private JsonWebToken jwt;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LoginUser loginUser = LoginUser.UNKNOWN_USER;
        if (jwt.getName() != null) {
            loginUser = LoginUserImpl.of(Integer.parseInt(jwt.getSubject()), jwt.getGroups());
        }
        log.debug("set loginUser to ThradLocal");
        LoginUserUtils.set(loginUser);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        log.debug("remove loginUser from ThradLocal");
        LoginUserUtils.remove();
    }
}

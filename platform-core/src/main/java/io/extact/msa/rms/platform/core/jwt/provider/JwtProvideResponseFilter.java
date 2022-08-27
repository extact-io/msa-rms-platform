package io.extact.msa.rms.platform.core.jwt.provider;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.HttpHeaders;

import org.apache.commons.lang3.Range;

import io.extact.msa.rms.platform.core.jwt.JwtConfig;
import lombok.extern.slf4j.Slf4j;

@GenerateToken
@ConstrainedTo(RuntimeType.SERVER)
@Slf4j
public class JwtProvideResponseFilter implements ContainerResponseFilter {

    private static final Range<Integer> SUCCESS_STATUS = Range.between(200, 299);
    private JsonWebTokenGenerator tokenGenerator;

    @Inject
    public JwtProvideResponseFilter(JsonWebTokenGenerator generator) {
        this.tokenGenerator = generator;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {

        // 例外が発生した場合はExecptionMapperでレスポンスが設定されているので
        // まずはHTTPステータスを見て成功か失敗かを判定
        if (!SUCCESS_STATUS.contains(responseContext.getStatus())) {
            return;
        }

        if (!responseContext.hasEntity()) {
            log.warn("Reponse body is not set.");
            return;
        }

        Object entity = responseContext.getEntity();
        if (!(entity instanceof UserClaims)) {
            log.warn("The instance of the body isn't UserClaims. [class={}]", entity.getClass().getName());
            return;
        }

        // JwtTokenの生成
        String jwtToken = tokenGenerator.generateToken((UserClaims) entity);
        log.info("Generated JWT-Token=>[{}]", jwtToken); // ホントはログに書いちゃダメだけどネ

        var headers = responseContext.getHeaders();
        headers.add("Access-Control-Expose-Headers", HttpHeaders.AUTHORIZATION);
        headers.add(HttpHeaders.AUTHORIZATION, JwtConfig.BEARER_MARK + " " + jwtToken);
    }
}

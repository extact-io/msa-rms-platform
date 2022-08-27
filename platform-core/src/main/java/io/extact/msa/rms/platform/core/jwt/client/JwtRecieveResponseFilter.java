package io.extact.msa.rms.platform.core.jwt.client;

import java.io.IOException;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.HttpHeaders;

import org.apache.commons.lang3.tuple.Pair;

/**
 * レスポンスヘッダからJsonWebTokenを取得し通知を行うクラス
 */
@ConstrainedTo(RuntimeType.CLIENT)
public class JwtRecieveResponseFilter implements ClientResponseFilter {

    @Inject
    private Event<JwtRecieveEvent> notificator;

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        if (responseContext.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            var value = responseContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            notificator.fire(new JwtRecieveEvent(Pair.of(HttpHeaders.AUTHORIZATION, value)));
        }
    }
}

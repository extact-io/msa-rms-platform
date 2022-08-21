package io.extact.msa.rms.platform.core.jwt.client;

import java.io.IOException;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.HttpHeaders;

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

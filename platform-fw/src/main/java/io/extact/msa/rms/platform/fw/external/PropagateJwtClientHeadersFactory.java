package io.extact.msa.rms.platform.fw.external;

import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.extact.msa.rms.platform.core.extension.ConfiguableScoped;
import io.extact.msa.rms.platform.fw.external.jwt.JwtRecieveEvent;

/**
 * サーバから発行されたJsonWebTokenをリクエストヘッダに付加するクラス
 */
@ConfiguableScoped
@ConstrainedTo(RuntimeType.CLIENT)
public class PropagateJwtClientHeadersFactory implements ClientHeadersFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PropagateJwtClientHeadersFactory.class);
    private Pair<String, String> jwtHeader;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
            MultivaluedMap<String, String> clientOutgoingHeaders) {

        if (jwtHeader == null) {
            return clientOutgoingHeaders;
        }

        var newHeadersMap = new MultivaluedHashMap<String, String>(clientOutgoingHeaders);
        newHeadersMap.add(jwtHeader.getKey(), jwtHeader.getValue());
        return newHeadersMap;
    }

    void onEvent(@Observes JwtRecieveEvent event) {
        LOG.info("ヘッダに追加するJWTを受信しました");
        this.jwtHeader = event.getJwtHeader();
    }
}

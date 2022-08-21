package io.extact.msa.rms.platform.core.jwt.login;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

@ApplicationScoped
@ConstrainedTo(RuntimeType.CLIENT)
public class PropagateLoginClientHeadersFactory implements ClientHeadersFactory {

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
            MultivaluedMap<String, String> clientOutgoingHeaders) {

        var loginUser = LoginUserUtils.get();
        if (loginUser.isUnknownUser()) {
            return clientOutgoingHeaders;
        }

        var newHeadersMap = new MultivaluedHashMap<String, String>(clientOutgoingHeaders);
        newHeadersMap.add("rms-userId", String.valueOf(loginUser.getUserId()));
        newHeadersMap.add("rms-roles", loginUser.getGroupsByStringValue());
        return newHeadersMap;
    }
}

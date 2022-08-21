package io.extact.msa.rms.platform.fw.stub.auth.client;

import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;
import io.extact.msa.rms.platform.core.jwt.client.JwtPropagateClientHeadersFactory;
import io.extact.msa.rms.platform.core.jwt.client.JwtRecieveResponseFilter;
import io.extact.msa.rms.platform.fw.stub.auth.client_sever1.ClientServer1Api;
import io.extact.msa.rms.platform.fw.webapi.client.ExceptionPropagateClientMapper;

@RegisterRestClient(configKey = "web-api")
@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(ExceptionPropagateClientMapper.class)
@RegisterProvider(JwtRecieveResponseFilter.class)
@RegisterClientHeaders(JwtPropagateClientHeadersFactory.class)
@Path("/server1")
public interface ClientApiRestClient extends ClientServer1Api {
}

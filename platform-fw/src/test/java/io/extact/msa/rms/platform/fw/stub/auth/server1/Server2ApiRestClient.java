package io.extact.msa.rms.platform.fw.stub.auth.server1;

import jakarta.ws.rs.Path;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;
import io.extact.msa.rms.platform.fw.external.PropagateResponseExceptionMapper;
import io.extact.msa.rms.platform.fw.external.PropagateLoginUserClientHeadersFactory;
import io.extact.msa.rms.platform.fw.stub.auth.server1_server2.ClientServer2Api;

//test-classesは自動でRestClientインタフェースが検出されないので@AddBeanでインタフェースを登録すること
@RegisterRestClient(configKey = "web-api")
@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(PropagateResponseExceptionMapper.class)
@RegisterClientHeaders(PropagateLoginUserClientHeadersFactory.class)
@Path("/server2")
public interface Server2ApiRestClient extends ClientServer2Api {
}

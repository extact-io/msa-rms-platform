package io.extact.msa.rms.platform.fw.stub.auth.server2;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

import io.extact.msa.rms.platform.fw.stub.auth.server1_server2.ClientServer2Api;

@Path("/server2")
@ApplicationScoped
public class Server2Resource implements ClientServer2Api {

    private Server2Assert server2Assert;

    @Inject
    public Server2Resource(Server2Assert server2Assert) {
        this.server2Assert = server2Assert;
    }

    @Override
    public boolean notLoginApi() {
        server2Assert.doNotLoginApiAssert();
        return true;
    }

    @Override
    public boolean memberLoginApi() {
        server2Assert.doMemberLoginApiAssert();
        return true;
    }

    @Override
    public boolean adminLoginApi() {
        server2Assert.doAdminLoginApi();
        return true;
    }
}

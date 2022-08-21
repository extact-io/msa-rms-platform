package io.extact.msa.rms.platform.fw.stub.auth.server1;

import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import io.extact.msa.rms.platform.core.jwt.consumer.Authenticated;
import io.extact.msa.rms.platform.core.jwt.provider.GenerateToken;
import io.extact.msa.rms.platform.fw.stub.auth.client_sever1.AuthData;
import io.extact.msa.rms.platform.fw.stub.auth.client_sever1.ClientServer1Api;

@Path("/server1")
@ApplicationScoped
public class Server1Resource implements ClientServer1Api {

    private Server1Assert server1Assert;

    @Inject
    public Server1Resource(Server1Assert server1Assert) {
        this.server1Assert = server1Assert;
    }

    @GenerateToken
    @Override
    public AuthData authenticate(String loginId, String password) {
        server1Assert.doBeforeLoginAssert();
        return new AuthData(loginId, Set.of(password));
    }

    @Authenticated
    @RolesAllowed("member")
    @Override
    public boolean memeberApi() {
        server1Assert.doMemberApiAssert();
        return true;
    }

    @Authenticated
    @RolesAllowed("admin")
    @Override
    public boolean adminApi() {
        server1Assert.doAdminApiAssert();
        return true;
    }

    @Override
    public boolean guestApi() {
        server1Assert.doGuestApiAssert();
        return true;
    }

    @Override
    public boolean guestApiWithLogin() {
        server1Assert.doGuestApiWithAssert();
        return true;
    }
}

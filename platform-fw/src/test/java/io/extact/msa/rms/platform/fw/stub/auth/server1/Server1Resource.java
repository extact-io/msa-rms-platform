package io.extact.msa.rms.platform.fw.stub.auth.server1;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.platform.core.jwt.provider.GenerateToken;
import io.extact.msa.rms.platform.fw.stub.auth.client_sever1.AuthData;
import io.extact.msa.rms.platform.fw.stub.auth.client_sever1.ClientServer1Api;

@Path("/server1")
@ApplicationScoped
public class Server1Resource implements ClientServer1Api {

    private Server1Assert server1Assert;
    private Server2ApiRestClient server2Client;

    @Inject
    public Server1Resource(Server1Assert server1Assert, @RestClient Server2ApiRestClient server2Client) {
        this.server1Assert = server1Assert;
        this.server2Client = server2Client;
    }

    @GenerateToken
    @Override
    public AuthData authenticate(String loginId, String password) {
        server1Assert.doBeforeLoginAssert();
        return new AuthData(loginId, Set.of(password));
    }

    @Override
    public boolean memeberApi() {
        server1Assert.doMemberApiAssert();
        server2Client.memberLoginApi(); // Server2へのREST呼び出し
        server1Assert.doMemberApiAssert();
        return true;
    }

    @Override
    public boolean adminApi() {
        server1Assert.doAdminApiAssert();
        server2Client.adminLoginApi(); // Server2へのREST呼び出し
        server1Assert.doAdminApiAssert();
        return true;
    }

    @Override
    public boolean guestApi() {
        server1Assert.doGuestApiAssert();
        server2Client.notLoginApi();
        return true;
    }

    @Override
    public boolean guestApiWithLogin() {
        server1Assert.doGuestApiWithLoginAssert();
        return true;
    }
}

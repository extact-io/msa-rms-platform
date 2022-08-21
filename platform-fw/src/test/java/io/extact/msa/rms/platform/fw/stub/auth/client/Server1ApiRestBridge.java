package io.extact.msa.rms.platform.fw.stub.auth.client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.platform.core.jwt.provider.UserClaims;

@ApplicationScoped
public class Server1ApiRestBridge implements ClientApi {

    private Server1ApiRestClient client;

    @Inject
    public Server1ApiRestBridge(@RestClient Server1ApiRestClient client) {
        this.client = client;
    }

    @Override
    public UserClaims authenticate(String loginId, String password) {
        return client.authenticate(loginId, password);
    }

    @Override
    public boolean memeberApi() {
        return client.memeberApi();
    }

    @Override
    public boolean adminApi() {
        return client.adminApi();
    }

    @Override
    public boolean guestApi() {
        return client.guestApi();
    }

    @Override
    public boolean guestApiWithLogin() {
        return client.guestApiWithLogin();
    }
}

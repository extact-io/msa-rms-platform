package io.extact.msa.rms.platform.fw.stub.auth.client;

import io.extact.msa.rms.platform.core.jwt.provider.UserClaims;

public interface ClientApi {
    UserClaims authenticate(String loginId, String password);

    boolean memeberApi();

    boolean adminApi();

    boolean guestApi();

    boolean guestApiWithLogin();
}

package io.extact.msa.rms.platform.fw.stub.auth.client_sever1;

import java.util.Set;

import io.extact.msa.rms.platform.core.jwt.provider.UserClaims;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class AuthData implements UserClaims {

    private String userId;
    private Set<String> groups;

    @Override
    public String getUserPrincipalName() {
        return userId + "@msa-rms";
    }
}

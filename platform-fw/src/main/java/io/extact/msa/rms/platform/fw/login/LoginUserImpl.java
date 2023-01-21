package io.extact.msa.rms.platform.fw.login;

import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(staticName = "of")
@Getter @ToString
class LoginUserImpl implements LoginUser {
    private final int userId;
    private final Set<String> groups;
}

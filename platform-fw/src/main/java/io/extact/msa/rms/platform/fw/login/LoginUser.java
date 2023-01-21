package io.extact.msa.rms.platform.fw.login;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public interface LoginUser {
    public static final LoginUser UNKNOWN_USER = LoginUserImpl.of(-1, Collections.emptySet());
    int getUserId();
    Set<String> getGroups();
    default boolean isUnknownUser() {
        return this == UNKNOWN_USER;
    }
    default String getGroupsByStringValue() {
        return getGroups().stream()
                .collect(Collectors.joining(","));
    }
}
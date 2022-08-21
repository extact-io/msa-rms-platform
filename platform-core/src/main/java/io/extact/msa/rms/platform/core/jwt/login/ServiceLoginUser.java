package io.extact.msa.rms.platform.core.jwt.login;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(staticName = "of")
@Getter @ToString
public class ServiceLoginUser {
    public static final ServiceLoginUser UNKNOWN_USER = new ServiceLoginUser(-1, Collections.emptySet());
    private final int userId;
    private final Set<String> groups;
    public boolean isUnknownUser() {
        return this == UNKNOWN_USER;
    }
    public String getGroupsByStringValue() {
        return groups.stream()
                .collect(Collectors.joining(","));
    }
}

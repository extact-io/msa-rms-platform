package io.extact.msa.rms.platform.fw.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserTypeTest {
    @Test
    void testIsAmdin() {
        assertThat(UserType.ADMIN.isAdmin()).isTrue();
        assertThat(UserType.MEMBER.isAdmin()).isFalse();
    }
    @Test
    void testIsValidUserType() {
        assertThat(UserType.isValidUserType("ADMIN")).isTrue();
        assertThat(UserType.isValidUserType("MEMBER")).isTrue();
        assertThat(UserType.isValidUserType("admin")).isFalse();
        assertThat(UserType.isValidUserType("member")).isFalse();
    }
}

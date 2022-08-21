package io.extact.msa.rms.platform.core.jwt.login;

public class LoginUserUtils {
    private static final InheritableThreadLocal<ServiceLoginUser> LOGIN_USER = new InheritableThreadLocal<ServiceLoginUser>() {
        @Override
        protected ServiceLoginUser initialValue() {
            return ServiceLoginUser.UNKNOWN_USER;
        }
    };

    // only login package can be used
    static void set(ServiceLoginUser loginUser) {
        LOGIN_USER.set(loginUser);
    }

    public static ServiceLoginUser get() {
        return LOGIN_USER.get();
    }

    public static void remove() {
        LOGIN_USER.remove();
    }
}

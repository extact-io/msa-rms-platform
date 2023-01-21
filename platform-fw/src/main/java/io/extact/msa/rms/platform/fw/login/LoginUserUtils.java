package io.extact.msa.rms.platform.fw.login;

public class LoginUserUtils {

    private static final InheritableThreadLocal<LoginUser> LOGIN_USER = new InheritableThreadLocal<LoginUser>() {
        @Override
        protected LoginUser initialValue() {
            return LoginUser.UNKNOWN_USER;
        }
    };

    // only login package can be used
    static void set(LoginUser loginUser) {
        LOGIN_USER.set(loginUser);
    }

    public static LoginUser get() {
        return LOGIN_USER.get();
    }

    public static void remove() {
        LOGIN_USER.remove();
    }
}

package io.extact.msa.rms.platform.fw.domain.vo;

public enum UserType {

    ADMIN(true), MEMBER(false);

    boolean admin;

    private UserType(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public static boolean isValidUserType(String userTypeName) {
        try {
            UserType.valueOf(userTypeName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

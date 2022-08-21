package io.extact.msa.rms.platform.core.jwt;

public class JwtValidateException extends Exception {
    public JwtValidateException(Exception e) {
        super(e);
    }
}

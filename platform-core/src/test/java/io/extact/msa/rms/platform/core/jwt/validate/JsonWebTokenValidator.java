package io.extact.msa.rms.platform.core.jwt.validate;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.extact.msa.rms.platform.core.jwt.JwtValidateException;

public interface JsonWebTokenValidator {
    static final String TEST_PUBLIC_KEY_PATH = "/jwt.pub.key";
    JsonWebToken validate(String token) throws JwtValidateException;
}

package io.extact.msa.rms.platform.core.jwt.consumer;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.extact.msa.rms.platform.core.jwt.JwtValidateException;

public interface JsonWebTokenValidator {
    JsonWebToken validate(String token) throws JwtValidateException;
}

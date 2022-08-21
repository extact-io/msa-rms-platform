package io.extact.msa.rms.platform.core.jwt.provider;

public interface JsonWebTokenGenerator {
    String generateToken(UserClaims userClaims);
}
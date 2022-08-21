package io.extact.msa.rms.platform.core.jwt.provider;

import java.util.Set;

/**
 * JWTの元ネタを表すインタフェース。
 * @see GenerateToken
 */
public interface UserClaims {
    String getUserId();
    String getUserPrincipalName();
    Set<String> getGroups();
}

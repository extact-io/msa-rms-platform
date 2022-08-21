package io.extact.msa.rms.platform.core.jwt.provider;

import java.util.Set;

/**
 * JWTの元ネタを表すインタフェース。
 * <code>@GenerateToken</code>を付けてRESTリソースのメソッドの戻り値には
 * このインタフェースを実装すること。
 * @see GenerateToken
 */
public interface UserClaims {
    String getUserId();
    String getUserPrincipalName();
    Set<String> getGroups();
}

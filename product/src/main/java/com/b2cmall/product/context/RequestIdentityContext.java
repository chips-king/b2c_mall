package com.b2cmall.product.context;

import com.b2cmall.common.auth.AuthenticatedEmployee;
import com.b2cmall.common.exception.BusinessException;

/** Servlet线程会复用；身份只在一次请求内有效，必须由过滤器在finally中清理。 */
public final class RequestIdentityContext {
    private static final ThreadLocal<AuthenticatedEmployee> CURRENT = new ThreadLocal<>();
    private RequestIdentityContext() { }
    public static void set(AuthenticatedEmployee identity) { CURRENT.set(identity); }
    public static AuthenticatedEmployee require() {
        AuthenticatedEmployee identity = CURRENT.get();
        if (identity == null) { throw new BusinessException(401, "请先登录"); }
        return identity;
    }
    public static void clear() { CURRENT.remove(); }
}

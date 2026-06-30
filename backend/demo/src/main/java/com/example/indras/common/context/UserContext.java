package com.example.indras.common.context;

import com.example.indras.common.exception.BizException;

public final class UserContext {

    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_ROLE = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setUserId(Long userId) {
        CURRENT_USER.set(userId);
    }

    public static void setRole(String role) {
        CURRENT_ROLE.set(role);
    }

    public static void set(Long userId, String role) {
        CURRENT_USER.set(userId);
        CURRENT_ROLE.set(role);
    }

    public static Long getUserId() {
        return CURRENT_USER.get();
    }

    public static String getRole() {
        return CURRENT_ROLE.get();
    }

    public static Long requireUserId() {
        Long userId = CURRENT_USER.get();
        if (userId == null) {
            throw BizException.unauthorized("未登录或 Token 失效");
        }
        return userId;
    }

    public static void requireAdmin() {
        requireUserId();
        if (!"ADMIN".equals(CURRENT_ROLE.get())) {
            throw BizException.forbidden("权限不足");
        }
    }

    public static void clear() {
        CURRENT_USER.remove();
        CURRENT_ROLE.remove();
    }
}

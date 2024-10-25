package com.github.frank.common.exception;

import lombok.Getter;

/**
 * @author Frank An
 */
@Getter
public enum ErrorCode {
    // 认证相关错误 (1000-1999)
    USERNAME_ALREADY_EXISTS("1000", "Username already exists"),
    EMAIL_ALREADY_EXISTS("1001", "Email already exists"),
    INVALID_CREDENTIALS("1002", "Invalid username or password"),
    TOKEN_EXPIRED("1003", "Token has expired"),
    INVALID_TOKEN("1004", "Invalid token"),

    // 用户相关错误 (2000-2999)
    USER_NOT_FOUND("2000", "User not found"),
    INVALID_USER_STATUS("2001", "Invalid user status"),

    // 角色相关错误 (3000-3999)
    ROLE_NOT_FOUND("3000", "Role not found"),
    ROLE_ALREADY_EXISTS("3001", "Role already exists"),

    // 系统错误 (9000-9999)
    SYSTEM_ERROR("9000", "System error"),
    INVALID_REQUEST("9001", "Invalid request"),
    DATABASE_ERROR("9002", "Database error");;

    private final String message;
    private final String code;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}

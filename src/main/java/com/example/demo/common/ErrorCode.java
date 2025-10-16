package com.example.demo.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // =========== 一级宏观错误码 业务相关 (1xxx) ===========
    USER_NOT_FOUND(1001, "指定用户不存在", HttpStatus.NOT_FOUND),

    // =========== 一级宏观错误码 系统相关 (2xxx) ===========
    VALIDATION_ERROR(2001, "参数校验失败", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(5000, "服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
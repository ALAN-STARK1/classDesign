package com.example.indras.common.exception;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private final int code;
    private final Object data;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
        this.data = null;
    }

    public BizException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public static BizException badRequest(String message) {
        return new BizException(40001, message);
    }

    public static BizException unauthorized(String message) {
        return new BizException(40100, message);
    }

    public static BizException forbidden(String message) {
        return new BizException(40300, message);
    }

    public static BizException notFound(String message) {
        return new BizException(40400, message);
    }

    public static BizException conflict(String message) {
        return new BizException(40900, message);
    }

    public static BizException businessRule(String message) {
        return new BizException(42200, message);
    }

    public static BizException aiUnavailable(String message) {
        return new BizException(50200, message);
    }
}

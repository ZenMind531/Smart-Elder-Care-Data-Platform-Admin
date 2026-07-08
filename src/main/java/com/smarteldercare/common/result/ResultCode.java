package com.smarteldercare.common.result;

public enum ResultCode {
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或token失效"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "数据不存在"),
    INTERNAL_ERROR(500, "服务器内部错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

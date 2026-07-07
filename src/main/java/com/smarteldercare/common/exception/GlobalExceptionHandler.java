package com.smarteldercare.common.exception;

import com.smarteldercare.common.result.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 兜底：任何没被处理的异常都到这里
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        e.printStackTrace(); // 控制台打印错误，方便调试
        return ApiResponse.error(500, "服务器内部错误：" + e.getMessage());
    }

    // 参数校验失败
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<?> handleIllegalArgument(IllegalArgumentException e) {
        return ApiResponse.error(400, e.getMessage());
    }
}
package com.smarteldercare.common.exception;

import com.smarteldercare.common.result.ApiResponse;
import com.smarteldercare.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException exception) {
        return ApiResponse.fail(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgument(IllegalArgumentException exception) {
        return ApiResponse.fail(ResultCode.BAD_REQUEST.getCode(), exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));
        return ApiResponse.fail(ResultCode.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(BindException.class)
    public ApiResponse<Void> handleBindException(BindException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));
        return ApiResponse.fail(ResultCode.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleBadJson(HttpMessageNotReadableException exception) {
        return ApiResponse.fail(ResultCode.BAD_REQUEST.getCode(), "请求体格式错误");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> handleAccessDenied(AccessDeniedException exception) {
        return ApiResponse.fail(403, "无权限");
    }

    /** 兜底：所有未捕获异常，详细堆栈写日志，对外不暴露细节 */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception, HttpServletRequest request) {
        log.error("系统异常 [{} {}]", request.getMethod(), request.getRequestURI(), exception);
        return ApiResponse.fail(ResultCode.INTERNAL_ERROR);
    }
}

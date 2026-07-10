package com.smarteldercare.common.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Integer code;
    private String message;
    private T data;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return of(ResultCode.SUCCESS, data);
    }

    public static <T> ApiResponse<T> fail(ResultCode resultCode) {
        return of(resultCode, null);
    }

    public static <T> ApiResponse<T> fail(Integer code, String message) {
        return new ApiResponse<>(code, message, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return fail(code, message);
    }

    public static <T> ApiResponse<T> of(ResultCode resultCode, T data) {
        return new ApiResponse<>(resultCode.getCode(), resultCode.getMessage(), data, LocalDateTime.now());
    }
}

package com.smarteldercare.common.exception;

import com.smarteldercare.common.result.ResultCode;

public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public Integer getCode() {
        return code;
    }
}

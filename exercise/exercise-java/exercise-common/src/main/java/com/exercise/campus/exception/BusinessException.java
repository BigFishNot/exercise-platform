package com.exercise.campus.exception;

import com.exercise.campus.enums.ResponseCodeEnum;
import lombok.Getter;

/**
 * 业务异常
 * 由全局异常处理统一转 ResponseVO 返回
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResponseCodeEnum.BUSINESS_ERROR.getCode();
    }

    public BusinessException(ResponseCodeEnum codeEnum) {
        super(codeEnum.getInfo());
        this.code = codeEnum.getCode();
    }

    public BusinessException(ResponseCodeEnum codeEnum, String message) {
        super(message);
        this.code = codeEnum.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
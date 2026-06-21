package com.exercise.campus.vo;

import com.exercise.campus.enums.ResponseCodeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应包装
 * {
 *   "status": "success",
 *   "code": 200,
 *   "info": "成功",
 *   "data": T
 * }
 */
@Data
public class ResponseVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private Integer code;
    private String info;
    private T data;

    public ResponseVO() {
    }

    public ResponseVO(String status, Integer code, String info, T data) {
        this.status = status;
        this.code = code;
        this.info = info;
        this.data = data;
    }

    public static <T> ResponseVO<T> ok() {
        return new ResponseVO<>(ResponseCodeEnum.SUCCESS.getStatus(),
                ResponseCodeEnum.SUCCESS.getCode(),
                ResponseCodeEnum.SUCCESS.getInfo(),
                null);
    }

    public static <T> ResponseVO<T> ok(T data) {
        return new ResponseVO<>(ResponseCodeEnum.SUCCESS.getStatus(),
                ResponseCodeEnum.SUCCESS.getCode(),
                ResponseCodeEnum.SUCCESS.getInfo(),
                data);
    }

    public static <T> ResponseVO<T> ok(T data, String info) {
        return new ResponseVO<>(ResponseCodeEnum.SUCCESS.getStatus(),
                ResponseCodeEnum.SUCCESS.getCode(),
                info,
                data);
    }

    public static <T> ResponseVO<T> fail(ResponseCodeEnum codeEnum) {
        return new ResponseVO<>(codeEnum.getStatus(), codeEnum.getCode(), codeEnum.getInfo(), null);
    }

    public static <T> ResponseVO<T> fail(ResponseCodeEnum codeEnum, String info) {
        return new ResponseVO<>(codeEnum.getStatus(), codeEnum.getCode(), info, null);
    }

    public static <T> ResponseVO<T> fail(Integer code, String info) {
        return new ResponseVO<>(ResponseCodeEnum.BUSINESS_ERROR.getStatus(), code, info, null);
    }
}
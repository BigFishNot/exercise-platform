package com.exercise.campus.config;

import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.exception.BusinessException;
import com.exercise.campus.vo.ResponseVO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理
 * 所有 Controller 抛出的异常都转 ResponseVO 返回
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseVO<Void> handleBusiness(BusinessException e) {
        log.warn("[BusinessException] code={} message={}", e.getCode(), e.getMessage());
        return ResponseVO.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseVO<Void> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("[ParamValid] {}", msg);
        return ResponseVO.fail(ResponseCodeEnum.PARAM_ERROR, msg);
    }

    @ExceptionHandler(BindException.class)
    public ResponseVO<Void> handleBind(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("[ParamBind] {}", msg);
        return ResponseVO.fail(ResponseCodeEnum.PARAM_ERROR, msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseVO<Void> handleConstraint(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("[ConstraintViolation] {}", msg);
        return ResponseVO.fail(ResponseCodeEnum.PARAM_ERROR, msg);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class})
    public ResponseVO<Void> handleParam(Exception e) {
        log.warn("[ParamError] {}", e.getMessage());
        return ResponseVO.fail(ResponseCodeEnum.PARAM_ERROR, "参数错误");
    }

    /** 静态资源不存在（典型：接口路径写错，被 ResourceHttpRequestHandler 接管） */
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseVO<Void> handleNoResource(org.springframework.web.servlet.resource.NoResourceFoundException e) {
        log.warn("[NoResourceFound] {}", e.getMessage());
        return ResponseVO.fail(404, "接口不存在");
    }

    @ExceptionHandler(Exception.class)
    public ResponseVO<Void> handleUnknown(Exception e) {
        log.error("[SystemError]", e);
        return ResponseVO.fail(ResponseCodeEnum.SYSTEM_ERROR);
    }
}
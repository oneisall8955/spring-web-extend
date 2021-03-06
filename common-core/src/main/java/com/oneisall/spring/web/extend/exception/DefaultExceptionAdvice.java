package com.oneisall.spring.web.extend.exception;

import com.oneisall.spring.web.extend.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一结果返回
 *
 * @author liuzhicong
 */
@Slf4j
@RestControllerAdvice
public class DefaultExceptionAdvice {

    /**
     * BusinessException 业务异常处理
     * 返回状态码:200
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleException(BusinessException e) {
        log.error("发生业务异常", e);
        return Result.failed(e.getCodeMessageEnum());
    }


    /**
     * 所有异常统一处理
     * 返回状态码:500
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("发生系统异常", e);
        return Result.failed(SystemExceptionEnum.SYSTEM_ERROR);
    }


}

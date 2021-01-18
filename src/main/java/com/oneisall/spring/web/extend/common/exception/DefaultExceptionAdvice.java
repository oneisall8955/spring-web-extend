package com.oneisall.spring.web.extend.common.exception;

import com.oneisall.spring.web.extend.common.Result;
import com.oneisall.spring.web.extend.common.enums.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
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
        return Result.failed(e.getExceptionEnum());
    }


    /**
     * 所有异常统一处理
     * 返回状态码:500
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("发生系统异常", e);
        return Result.failed(ExceptionEnum.SYSTEM_ERROR);
    }


}

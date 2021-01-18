package com.oneisall.spring.web.extend.common.exception;

import com.oneisall.spring.web.extend.common.enums.ExceptionEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 业务异常
 *
 * @author liuzhicong
 **/
@Setter
@Getter
public class BusinessException extends RuntimeException {
    private Integer code;
    private String msg;
    private ExceptionEnum exceptionEnum;

    public BusinessException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMsg());
        this.code = exceptionEnum.getCode();
        this.msg = exceptionEnum.getMsg();
        this.exceptionEnum = exceptionEnum;
    }
}

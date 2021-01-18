package com.oneisall.spring.web.extend.exception;

import com.oneisall.spring.web.extend.i18n.MessageException;
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
    private MessageException messageException;

    public BusinessException(MessageException messageException) {
        super(messageException.getMsg());
        this.code = messageException.getCode();
        this.msg = messageException.getMsg();
        this.messageException = messageException;
    }
}

package com.oneisall.spring.web.extend.exception;

import com.oneisall.spring.web.extend.i18n.CodeMessageEnum;
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
    private CodeMessageEnum codeMessageEnum;

    public BusinessException(CodeMessageEnum codeMessageEnum) {
        super(codeMessageEnum.getMsg());
        this.code = codeMessageEnum.getCode();
        this.msg = codeMessageEnum.getMsg();
        this.codeMessageEnum = codeMessageEnum;
    }
}

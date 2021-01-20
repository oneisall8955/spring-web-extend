package com.oneisall.spring.web.extend.exception;

import com.oneisall.spring.web.extend.i18n.CodeMessageEnum;
import com.oneisall.spring.web.extend.i18n.I18nMessage;
import lombok.AllArgsConstructor;

/**
 * @author liuzhicong
 **/
@AllArgsConstructor
public enum SystemExceptionEnum implements CodeMessageEnum {

    // system error
    SYSTEM_ERROR(500, "system error.(500)"),
    READ_BODY_ERROR(501, "system error.(501)"),
    ;

    private final int code;
    private final String defaultMsg;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return I18nMessage.getMessage("ERROR_" + this.code, this.defaultMsg);
    }
}

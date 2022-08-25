package com.oneisall.spring.web.extend.concurrent;

import com.oneisall.spring.web.extend.i18n.CodeMessageEnum;
import com.oneisall.spring.web.extend.i18n.I18nMessage;
import lombok.AllArgsConstructor;

/**
 * @author liuzhicong
 **/
@AllArgsConstructor
public enum ConAssistantExceptionEnum implements CodeMessageEnum {


    /** 类型转换异常 */
    TYPE_CAST_ERROR(20001, "class type cast failed.(10001)"),

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

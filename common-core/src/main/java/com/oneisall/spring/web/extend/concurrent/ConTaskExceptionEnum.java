package com.oneisall.spring.web.extend.concurrent;

import com.oneisall.spring.web.extend.i18n.CodeMessageEnum;
import lombok.AllArgsConstructor;

/**
 * @author liuzhicong
 **/
@AllArgsConstructor
public enum ConTaskExceptionEnum implements CodeMessageEnum {


    /** 类型转换异常 */
    TYPE_CAST_ERROR(10001, "class type cast failed.(10001)"),

    ;

    private final int code;
    private final String defaultMsg;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return defaultMsg;
    }

}

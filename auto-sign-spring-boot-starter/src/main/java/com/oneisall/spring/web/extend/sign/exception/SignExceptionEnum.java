package com.oneisall.spring.web.extend.sign.exception;

import com.oneisall.spring.web.extend.i18n.CodeMessageEnum;
import com.oneisall.spring.web.extend.i18n.I18nMessage;
import lombok.AllArgsConstructor;

/**
 * @author liuzhicong
 **/
@AllArgsConstructor
public enum SignExceptionEnum implements CodeMessageEnum {

    /** 签名校验失败 */
    SIGN_VERIFY_FAILED(10001, "Signature verification failed.(10001)"),
    /** 签名配置异常 */
    SIGN_SETTING_ERROR(10002, "Signature verification failed.(10002)"),

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

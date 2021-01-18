package com.oneisall.spring.web.extend.sign.exception;

import com.oneisall.spring.web.extend.i18n.I18nMessage;
import com.oneisall.spring.web.extend.i18n.MessageException;
import lombok.AllArgsConstructor;

/**
 * @author liuzhicong
 **/
@AllArgsConstructor
public enum SignExceptionEnum implements MessageException {

    /** 签名校验失败 */
    SIGN_VERIFY_FAILED(10001, "Signature verification failed."),
    /** 签名配置异常 */
    SIGN_SYSTEM_ERROR(10002, "Signature verification failed(setting error)."),
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

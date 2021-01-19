package com.oneisall.spring.web.extend.i18n;

/**
 * 可以获取消息及code得异常接口
 *
 * @author liuzhicong
 **/
public interface MessageException {

    /**
     * 获取异常code
     *
     * @return 异常code
     */
    int getCode();

    /**
     * 获取异常信息
     *
     * @return 异常信息
     */
    String getMsg();
}

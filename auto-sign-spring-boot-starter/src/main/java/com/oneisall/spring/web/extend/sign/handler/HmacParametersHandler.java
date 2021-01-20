package com.oneisall.spring.web.extend.sign.handler;

/**
 * Hmac算法，从Parameter获取签名，校验Parameter
 *
 * @author : oneisall
 * @version : v1 2021/1/20 00:07
 */
public class HmacParametersHandler extends AbstractSignHandler {

    @Override
    public boolean verify(RequestSignInfo requestSignInfo) {
        return false;
    }

    @Override
    public boolean match(RequestSignInfo requestSignInfo) {
        return false;
    }

    @Override
    public int order() {
        return 50;
    }
}

package com.oneisall.spring.web.extend.sign.handler;

/**
 * Hmac算法，从Head获取签名，校验request body
 *
 * @author : oneisall
 * @version : v1 2021/1/20 00:07
 */
public class HmacHeaderBodyHandler extends AbstractSignHandler {

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
        return 10;
    }
}

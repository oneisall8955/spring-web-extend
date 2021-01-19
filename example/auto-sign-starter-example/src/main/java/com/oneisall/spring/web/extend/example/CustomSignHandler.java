package com.oneisall.spring.web.extend.example;

import com.oneisall.spring.web.extend.sign.handler.AbstractSignHandler;
import com.oneisall.spring.web.extend.sign.handler.RequestSignInfo;
import org.springframework.stereotype.Component;

/**
 * TODO :please describe it in one sentence
 *
 * @author : oneisall
 * @version : v1 2021/1/20 01:28
 */
@Component
public class CustomSignHandler extends AbstractSignHandler {

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
        return 999;
    }
}

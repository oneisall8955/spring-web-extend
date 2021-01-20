package com.oneisall.spring.web.extend.example;

import com.oneisall.spring.web.extend.model.Result;
import com.oneisall.spring.web.extend.sign.annotation.SignRequired;
import com.oneisall.spring.web.extend.sign.exception.SignExceptionEnum;
import com.oneisall.spring.web.extend.sign.handler.AbstractSignHandler;
import com.oneisall.spring.web.extend.sign.handler.RequestSignInfo;

/**
 * 自定义验签处理器，没有加入容器处理，后面被指定
 *
 * @author : oneisall
 * @version : v1 2021/1/20 01:28
 */
public class AfterAppendCustomSignHandler extends AbstractSignHandler {

    @Override
    public Result<?> verify(RequestSignInfo requestSignInfo) {
        SignRequired signAnnotation = requestSignInfo.getSignAnnotation();
        if ("after".equalsIgnoreCase(requestSignInfo.getHeaders().get(signAnnotation.signKey()))) {
            return Result.succeed();
        }
        return Result.failedLog(SignExceptionEnum.SIGN_VERIFY_FAILED, signAnnotation.signKey() + "不等于after");
    }

    @Override
    public boolean match(RequestSignInfo requestSignInfo) {
        return false;
    }

    @Override
    public int order() {
        return 998;
    }
}

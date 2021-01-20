package com.oneisall.spring.web.extend.example;

import com.oneisall.spring.web.extend.exception.SystemExceptionEnum;
import com.oneisall.spring.web.extend.model.Result;
import com.oneisall.spring.web.extend.sign.handler.AbstractSignHandler;
import com.oneisall.spring.web.extend.sign.handler.RequestSignInfo;
import org.springframework.stereotype.Component;

/**
 * 自定义验签处理器
 *
 * @author : oneisall
 * @version : v1 2021/1/20 01:28
 */
@Component
public class CustomSignHandler extends AbstractSignHandler {

    @Override
    public Result<?> verify(RequestSignInfo requestSignInfo) {
        return Result.failed(SystemExceptionEnum.SYSTEM_ERROR);
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

package com.oneisall.spring.web.extend.sign.handler;

import com.oneisall.spring.web.extend.model.Result;
import com.oneisall.spring.web.extend.model.StrategyMatcher;

/**
 * 抽象的验签处理器
 *
 * @author liuzhicong
 **/
public abstract class AbstractSignHandler implements StrategyMatcher<RequestSignInfo> {

    /**
     * 验签
     *
     * @param requestSignInfo 请求信息
     * @return 结果
     */
    public abstract Result<?> verify(RequestSignInfo requestSignInfo);

    @Override
    public String toString() {
        return "{clazz:" + this.getClass().getName() + ",order:" + order() + "}";
    }
}

package com.oneisall.spring.web.extend.sign.handler;

import com.oneisall.spring.web.extend.model.StrategyMatcher;

/**
 * 抽象的验签处理器
 *
 * @author liuzhicong
 **/
public abstract class AbstractSignHandler implements StrategyMatcher<RequestSignInfo> {

    /** 验签 */
    public abstract boolean verify(RequestSignInfo requestSignInfo);
}

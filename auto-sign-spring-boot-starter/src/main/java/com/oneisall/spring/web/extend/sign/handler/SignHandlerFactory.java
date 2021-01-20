package com.oneisall.spring.web.extend.sign.handler;

import com.oneisall.spring.web.extend.model.BaseStrategyFactory;
import com.oneisall.spring.web.extend.model.StrategyFactoryInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * 验签策略工厂
 *
 * @author : oneisall
 * @version : v1 2021/1/20 00:13
 */
@Slf4j
@StrategyFactoryInfo("验签处理器")
public class SignHandlerFactory extends BaseStrategyFactory<AbstractSignHandler,RequestSignInfo> {

}

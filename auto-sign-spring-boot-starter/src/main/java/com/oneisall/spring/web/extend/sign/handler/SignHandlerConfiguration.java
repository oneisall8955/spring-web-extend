package com.oneisall.spring.web.extend.sign.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 签名处理器初始化
 *
 * @author : oneisall
 * @version : v1 2021/1/20 01:11
 */
@Configuration
public class SignHandlerConfiguration {

    @Bean
    public SignHandlerFactory signHandlerFactory() {
        return new SignHandlerFactory();
    }

    @Bean
    public HmacHeaderBodyHandler hmacHeaderBodyHandler() {
        return new HmacHeaderBodyHandler();
    }

    @Bean
    public HmacParametersHandler hmacParametersHandler() {
        return new HmacParametersHandler();
    }
}

package com.oneisall.spring.web.extend.model;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 工厂信息
 *
 * @author : oneisall
 * @version : v1 2021/1/20 00:35
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrategyFactoryInfo {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";
}

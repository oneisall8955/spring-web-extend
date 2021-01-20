package com.oneisall.spring.web.extend.sign.annotation;

import com.oneisall.spring.web.extend.sign.configuration.SignDataFrom;
import com.oneisall.spring.web.extend.sign.configuration.SignKeyFrom;
import com.oneisall.spring.web.extend.sign.configuration.SignMethod;
import com.oneisall.spring.web.extend.sign.handler.AbstractSignHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要签名得接口
 *
 * @author liuzhicong
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SignRequired {

    String title();

    /** 签名密钥 */
    String signedSecret() default "";

    /** 签名密钥从配置文件中获取 */
    boolean usingProperties() default false;

    String uniqueName() default "";

    String signKey() default "sign";

    SignKeyFrom signKeyFrom() default SignKeyFrom.HEADER;

    SignDataFrom signDataFrom() default SignDataFrom.BODY;

    SignMethod signMethod() default SignMethod.HMAC;

    Class<? extends AbstractSignHandler> usingVerifier() default AbstractNone.class;

    /** 仅仅包含的字段 */
    String[] includes() default {};

    abstract class AbstractNone extends AbstractSignHandler {
    }
}

package com.oneisall.spring.web.extend.common.configuration.sign;

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

    /** 请求参数从哪里获取 */
    enum RequestSignFrom {
        /** 请求头 */
        HEADER,
        /** get参数 */
        PARAM
    }

    /** 请求参数从哪里获取 */
    enum SignDataFrom {
        /** body参数 */
        BODY,
        /** get参数 */
        PARAM
    }

    String title();

    /** 签名密钥 */
    String signedSecret() default "";

    /** 签名密钥从配置文件中获取 */
    boolean usingProperties() default false;

    String uniqueName() default "";

    RequestSignFrom requestSignFrom() default RequestSignFrom.HEADER;

    SignDataFrom signDataFrom() default SignDataFrom.BODY;

    String requestSignKey() default "sign";
}

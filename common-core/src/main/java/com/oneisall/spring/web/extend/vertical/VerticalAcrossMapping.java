package com.oneisall.spring.web.extend.vertical;

import com.oneisall.spring.web.extend.vertical.converter.DefaultConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liuzhicong
 **/
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerticalAcrossMapping {

    String mappingSecondaryValue();

    boolean throwFoundManyButMappingOne() default true;

    Class<? extends VerticalConverter> usingConverter() default DefaultConverter.class;

    String pattern() default "";
}

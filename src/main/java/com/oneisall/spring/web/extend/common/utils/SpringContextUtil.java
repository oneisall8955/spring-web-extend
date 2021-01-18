package com.oneisall.spring.web.extend.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;

/**
 * @author liuzhicong
 **/
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context = null;

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static String getActiveProfile() {
        return Optional.ofNullable(context.getEnvironment().getActiveProfiles()[0]).orElse(null);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> clazz) {
        return context.getBeansWithAnnotation(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        context = applicationContext;
    }
}

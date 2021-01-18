package com.oneisall.spring.web.extend.common.configuration;

import com.oneisall.spring.web.extend.common.configuration.sign.ReusableBodyRequest;
import com.oneisall.spring.web.extend.common.configuration.sign.ReusableBodyRequestFilter;
import com.oneisall.spring.web.extend.common.configuration.sign.SignInterceptor;
import com.oneisall.spring.web.extend.common.configuration.sign.SignRequired;
import com.oneisall.spring.web.extend.common.properties.SignProperties;
import com.oneisall.spring.web.extend.common.utils.CollectionMapUtil;
import com.oneisall.spring.web.extend.common.utils.SpringContextUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author liuzhicong
 **/
@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Resource
    private SignProperties signProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // TODO 根据注解获取path从而不用配置 https://my.oschina.net/u/557580/blog/534699
        //  仿造 RequestMappingHandlerMapping && HandlerMethod
        //  不能直接获取RequestMappingHandlerMapping，因为循环依赖问题
        SignInterceptor signInterceptor = signInterceptor();
        registry.addInterceptor(signInterceptor).addPathPatterns(signInterceptor.getPaths());
    }

    @Bean
    public SignInterceptor signInterceptor() {
        SignInterceptor signInterceptor = new SignInterceptor();
        List<String> paths = CollectionMapUtil.null2empty(signProperties.getPaths());
        signInterceptor.setPaths(paths.stream().distinct().collect(Collectors.toList()));
        return signInterceptor;
    }

    @Bean
    public FilterRegistrationBean<ReusableBodyRequestFilter> registerFilter() {
        FilterRegistrationBean<ReusableBodyRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ReusableBodyRequestFilter());
        List<String> paths = signInterceptor().getPaths();
        registration.addUrlPatterns(paths.toArray(new String[]{}));
        registration.setName("ReusableBodyRequestFilter");
        registration.setOrder(1);
        return registration;
    }
}

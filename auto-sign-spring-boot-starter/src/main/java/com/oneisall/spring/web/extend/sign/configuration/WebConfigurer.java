package com.oneisall.spring.web.extend.sign.configuration;

import com.oneisall.spring.web.extend.sign.handler.SignHandlerFactory;
import com.oneisall.spring.web.extend.sign.properties.SignProperties;
import com.oneisall.spring.web.extend.utils.CollectionMapUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuzhicong
 **/
@EnableConfigurationProperties({SignProperties.class})
@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Resource
    private SignProperties signProperties;

    @Resource
    private SignHandlerFactory signHandlerFactory;

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
        signInterceptor.setSignHandlerFactory(signHandlerFactory);
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

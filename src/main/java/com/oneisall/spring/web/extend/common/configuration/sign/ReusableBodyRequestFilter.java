package com.oneisall.spring.web.extend.common.configuration.sign;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 可重复使用request body的过滤器
 * <p>
 * {@link ReusableBodyRequest}
 *
 * @author liuzhicong
 **/
@Slf4j
public class ReusableBodyRequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ReusableBodyRequest reusableBodyRequest = null;
        if (request instanceof HttpServletRequest) {
            reusableBodyRequest = new ReusableBodyRequest((HttpServletRequest) request);
        }
        if (reusableBodyRequest == null) {
            chain.doFilter(request, response);
        } else {
            log.debug("ReusableBodyRequestFilter已生效，path：{}", reusableBodyRequest.getRequestURL());
            chain.doFilter(reusableBodyRequest, response);
        }
    }

    @Override
    public void destroy() {

    }


}

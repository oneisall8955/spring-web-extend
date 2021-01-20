package com.oneisall.spring.web.extend.sign.configuration;

import com.oneisall.spring.web.extend.exception.BusinessException;
import com.oneisall.spring.web.extend.model.Result;
import com.oneisall.spring.web.extend.sign.annotation.SignRequired;
import com.oneisall.spring.web.extend.sign.exception.SignExceptionEnum;
import com.oneisall.spring.web.extend.sign.handler.AbstractSignHandler;
import com.oneisall.spring.web.extend.sign.handler.RequestSignInfo;
import com.oneisall.spring.web.extend.sign.handler.SignHandlerFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * 签名拦截器
 *
 * @author liuzhicong
 **/
@Slf4j
@Setter
@Getter
public class SignInterceptor extends HandlerInterceptorAdapter {

    /** 签名路径 */
    private List<String> paths;

    /** 签名处理工厂 */
    private SignHandlerFactory signHandlerFactory;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        SignRequired signAnnotation = method.getAnnotation(SignRequired.class);
        if (signAnnotation == null) {
            log.debug("不需要验签的接口：{}", method.getName());
            return true;
        }

        RequestSignInfo requestSignInfo = RequestSignInfo.of(request, signAnnotation);
        Optional<AbstractSignHandler> handlerOptional;

        if (signAnnotation.usingVerifier() != SignRequired.AbstractNone.class) {
            handlerOptional = signHandlerFactory.getInstance(signAnnotation.usingVerifier());
        } else {
            handlerOptional = signHandlerFactory.getInstance(requestSignInfo);
        }
        AbstractSignHandler signHandler = handlerOptional.orElseThrow(() ->
                {
                    log.error("【{}】：找不到处理的handler", requestSignInfo.getTitle());
                    return new BusinessException(SignExceptionEnum.SIGN_VERIFY_FAILED);
                }
        );

        Result<?> verifyResult;
        try {
            verifyResult = signHandler.verify(requestSignInfo);
        } catch (Exception e) {
            // TODO 构建错误日志
            log.error("【{}】验签发生异常", signAnnotation.title(), e);
            if (e instanceof BusinessException) {
                throw e;
            } else {
                throw new BusinessException(SignExceptionEnum.SIGN_VERIFY_FAILED);
            }
        }
        if (!verifyResult.isSuccess()) {
            // TODO builder构建错误日志
            log.error("【{}】验签结果为失败，原因：{}", signAnnotation.title(), verifyResult.getMessage());
            if (verifyResult.getCodeMessageEnum() != null) {
                // 已经有指定的则用指定的
                throw new BusinessException(verifyResult.getCodeMessageEnum());
            } else {
                // 统一为签名失败放回给前端
                throw new BusinessException(SignExceptionEnum.SIGN_VERIFY_FAILED);
            }
        }
        return true;
    }
}

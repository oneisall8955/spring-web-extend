package com.oneisall.spring.web.extend.common.configuration.sign;

import com.oneisall.spring.web.extend.common.LoggerWrapper;
import com.oneisall.spring.web.extend.common.enums.ExceptionEnum;
import com.oneisall.spring.web.extend.common.exception.BusinessException;
import com.oneisall.spring.web.extend.common.properties.SignProperties;
import com.oneisall.spring.web.extend.common.utils.CollectionMapUtil;
import com.oneisall.spring.web.extend.common.utils.HmacUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @SuppressWarnings("unchecked")
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
        String signedSecret;
        String uniqueName = signAnnotation.uniqueName();
        String title = signAnnotation.title();

        // TODO 策略模式
        if (signAnnotation.usingProperties() && StringUtils.isBlank(uniqueName)) {
            log.error("【{}】：签名配置异常，签名唯一标识为空", title);
            throw new BusinessException(ExceptionEnum.SIGN_SYSTEM_ERROR);
        } else {
            SignProperties.SignSetting signSetting =
                    Optional.ofNullable(SignProperties.findUniqueName(uniqueName))
                            .orElse(new SignProperties.SignSetting());
            log.debug("【{}】：获取到签名配置为：{}", title, LoggerWrapper.buildJson(signSetting));
            if (StringUtils.isBlank(signSetting.getSignedSecret())) {
                log.error("【{}】：签名配置{}的密钥为空", title, uniqueName);
                throw new BusinessException(ExceptionEnum.SIGN_SYSTEM_ERROR);
            }
            signedSecret = signSetting.getSignedSecret();
        }
        log.debug("【{}】：签名配置{}的密钥为{}", title, uniqueName, signedSecret);

        SignRequired.RequestSignFrom requestSignFrom = signAnnotation.requestSignFrom();
        String requestSignKey = signAnnotation.requestSignKey();

        // TODO 策略模式获取sign
        String signFromRequest;
        if (requestSignFrom == SignRequired.RequestSignFrom.HEADER) {
            // 请求头
            signFromRequest = request.getHeader(requestSignKey);
        } else {
            // get参数
            signFromRequest = request.getParameter(requestSignKey);
        }

        // 对方提供的签名为空
        if (StringUtils.isBlank(signFromRequest)) {
            log.error("【{}】：请求从{}使用key={}中获取的签名为空", title, requestSignFrom, requestSignKey);
            throw new BusinessException(ExceptionEnum.SIGN_VERIFY_FAILED);
        }

        // TODO 策略模式获取签名参数
        SignRequired.SignDataFrom signDataFrom = signAnnotation.signDataFrom();
        Object signData;
        if (signDataFrom == SignRequired.SignDataFrom.BODY) {
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line = reader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                log.error("【{}】：请求获取请求body失败", title, e);
                throw new BusinessException(ExceptionEnum.SIGN_VERIFY_FAILED);
            }
            signData = builder.toString();
        } else {
            Map<String, String[]> parameterMap = request.getParameterMap();
            Map<String, String> parametersData = CollectionMapUtil.initNewHashMap(parameterMap.size());
            for (Map.Entry<String, String[]> parameterEntry : parameterMap.entrySet()) {
                String[] value = parameterEntry.getValue();
                if (value != null && value.length == 1 && !Objects.equals(parameterEntry.getKey(), requestSignKey)) {
                    parametersData.put(parameterEntry.getKey(), value[0]);
                }
            }
            signData = parametersData;
        }
        boolean verifyResult;
        try {
            if (signData instanceof Map) {
                verifyResult = HmacUtil.verifyParameters((Map<String, String>) signData, signedSecret, signFromRequest);
            } else {
                verifyResult = HmacUtil.verifyText(signData.toString(), signedSecret, signFromRequest);
            }
        } catch (Exception e) {
            log.error("【{}】：验签发生异常", title);
            verifyResult = false;
        }
        if (!verifyResult) {
            // TODO 封装结果
            String signFromLocal;
            if (signData instanceof Map) {
                signFromLocal = HmacUtil.generateHmac((Map<String, String>) signData, signedSecret);
            } else {
                signFromLocal = HmacUtil.generateHmac(signData.toString(), signedSecret);
            }
            log.error("【{}】：验签结果对不匹配，key：{}，请求：{}，本地：{},签名的内容:{}", title, signedSecret, signFromRequest, signFromLocal, signData);
            throw new BusinessException(ExceptionEnum.SIGN_VERIFY_FAILED);
        }
        return true;
    }
}

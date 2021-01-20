package com.oneisall.spring.web.extend.sign.handler;

import com.oneisall.spring.web.extend.model.Result;
import com.oneisall.spring.web.extend.sign.annotation.SignRequired;
import com.oneisall.spring.web.extend.sign.configuration.SignDataFrom;
import com.oneisall.spring.web.extend.sign.configuration.SignKeyFrom;
import com.oneisall.spring.web.extend.sign.configuration.SignMethod;
import com.oneisall.spring.web.extend.sign.exception.SignExceptionEnum;
import com.oneisall.spring.web.extend.utils.HmacUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * Hmac算法，从Parameter获取签名，校验Parameter
 *
 * @author : oneisall
 * @version : v1 2021/1/20 00:07
 */
@Slf4j
public class HmacParametersHandler extends AbstractSignHandler {

    @Override
    public Result<?> verify(RequestSignInfo requestSignInfo) {
        SignRequired signAnnotation = requestSignInfo.getSignAnnotation();
        Map<String, String> sourceParameters = requestSignInfo.getParameters();

        String signFromRequest = sourceParameters.get(signAnnotation.signKey());
        String signedSecret = requestSignInfo.getSignedSecret();
        if (StringUtils.isBlank(signedSecret)) {
            log.error("【{}】签名配置错误，签名密钥为空", signAnnotation.title());
            return Result.failed(SignExceptionEnum.SIGN_SETTING_ERROR);
        }

        String signFromLocal = HmacUtil.generateHmac(sourceParameters, signedSecret);
        boolean equals = Objects.equals(signFromLocal, signFromRequest);
        if (equals) {
            return Result.succeed();
        }
        // TODO 构建失败消息
        String log = String.format("【%s】：验签结果对不匹配，key：%s，请求：%s，本地：%s,签名的内容:%s",
                signAnnotation.title(), signedSecret, signFromRequest, signFromLocal, requestSignInfo.getBody());
        return Result.failedLog(SignExceptionEnum.SIGN_VERIFY_FAILED, log);
    }

    @Override
    public boolean match(RequestSignInfo requestSignInfo) {
        SignRequired annotation = requestSignInfo.getSignAnnotation();
        return annotation.signKeyFrom() == SignKeyFrom.PARAM
                && annotation.signDataFrom() == SignDataFrom.PARAM
                && annotation.signMethod() == SignMethod.HMAC;
    }

    @Override
    public int order() {
        return 1;
    }
}

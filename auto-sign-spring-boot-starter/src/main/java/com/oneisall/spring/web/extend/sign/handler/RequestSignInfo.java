package com.oneisall.spring.web.extend.sign.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.oneisall.spring.web.extend.sign.annotation.SignRequired;
import com.oneisall.spring.web.extend.sign.properties.SignProperties;
import com.oneisall.spring.web.extend.utils.HttpWebUtils;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * 请求的签名信息
 *
 * @author liuzhicong
 **/
@Setter
@Getter
public class RequestSignInfo {

    // 原始请求信息

    private HttpServletRequest request;
    private Map<String, String> headers;
    private Map<String, String> parameters;
    private String body;

    // 枚举配置
    /** 注解 */
    private SignRequired signAnnotation;

    /** 注解中获取或从配置中获取 */
    private String signedSecret;

    private SignProperties.SignSetting signSetting;

    /** 排除验签字段，用这个字段快速查找，比注解的数组快 */
    private LinkedHashSet<String> includes;

    /** 接口名称 */
    private String title;

    /** TODO 构造标识日志的生成器，提供默认的 */
    private Object logUniqueBuilder;

    public static RequestSignInfo of(HttpServletRequest request, SignRequired signAnnotation) {

        RequestSignInfo requestSignInfo = new RequestSignInfo();
        requestSignInfo.setRequest(request);
        requestSignInfo.setHeaders(HttpWebUtils.headers(request));
        requestSignInfo.setParameters(HttpWebUtils.parameters(request));
        requestSignInfo.setBody(HttpWebUtils.body(request));

        requestSignInfo.setSignAnnotation(signAnnotation);
        requestSignInfo.setSignedSecret(signAnnotation.signedSecret());
        if (signAnnotation.usingProperties()) {
            SignProperties.SignSetting setting = SignProperties.findUniqueName(signAnnotation.uniqueName());
            if (setting != null) {
                requestSignInfo.setSignSetting(setting);
                requestSignInfo.setSignedSecret(setting.getSignedSecret());
            }
        }
        requestSignInfo.setIncludes(Sets.newLinkedHashSet(Lists.newArrayList(signAnnotation.includes())));
        requestSignInfo.setTitle(signAnnotation.title());
        // TODO 日志构建
        requestSignInfo.setLogUniqueBuilder(null);
        return requestSignInfo;
    }
}

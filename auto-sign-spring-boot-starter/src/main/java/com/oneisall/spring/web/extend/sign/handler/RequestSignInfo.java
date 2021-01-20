package com.oneisall.spring.web.extend.sign.handler;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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

    /** 获取签名的值的字段名称 */
    private String signKeyName;
    /** 获取签名的值从哪里获取 */
    private String signKeyFrom;
    /** 签名的方法 */
    private String signMethod;
    /** 排除验签字段 */
    private List<String> excludes;
    /** TODO 用户指定使用的校验器 */
    private Object usingVerifier;
    /** 接口名称 */
    private String title;
    /** TODO 构造标识日志的生成器，提供默认的 */
    private Object logUniqueBuilder;
}

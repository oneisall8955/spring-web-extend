package com.oneisall.spring.web.extend.utils;

import com.google.common.collect.Maps;
import com.oneisall.spring.web.extend.exception.BusinessException;
import com.oneisall.spring.web.extend.exception.SystemExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author liuzhicong
 **/
@Slf4j
public class HttpWebUtils {

    /** 正则表达式 */
    private static final String URL_REGEX = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";

    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX.trim());

    private static final String UNKNOWN = "unknown";

    /**
     * X-Real-IP：nginx服务代理
     * X-Forwarded-For：Squid 服务代理
     * Proxy-Client-IP：apache 服务代理
     * WL-Proxy-Client-IP：weblogic 服务代理
     * HTTP_CLIENT_IP：有些代理服务器
     *
     * */
    private static final String[] HEADERS = {"X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

    /**
     * 判断字符串是否为URL
     * <p>
     * https://stackoverflow.com/questions/3809401/what-is-a-good-regular-expression-to-match-a-url
     *
     * @param urls 需要判断的String类型url
     * @return true:是URL；false:不是URL
     */
    public static boolean isHttpUrl(String urls) {
        return URL_PATTERN.matcher(urls).matches();
    }

    /** 获取headers */
    public static Map<String, String> headers(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headers = Maps.newLinkedHashMapWithExpectedSize(10);
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            headers.put(name, value);
        }
        return headers;
    }

    public static String body(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            log.error("获取body异常", e);
            throw new BusinessException(SystemExceptionEnum.READ_BODY_ERROR);
        }
        return builder.toString();
    }

    /**
     * 获取get?后的参数
     */
    public static Map<String, String> parameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> parametersData = CollectionMapUtil.newLinkedHashMapWithExpectedSize(parameterMap.size());
        for (Map.Entry<String, String[]> parameterEntry : parameterMap.entrySet()) {
            String[] value = parameterEntry.getValue();
            if (value != null && value.length == 1) {
                parametersData.put(parameterEntry.getKey(), value[0]);
            }
        }
        return parametersData;
    }

    /**
     * 获取ip
     */
    public static String getIp(HttpServletRequest request) {
        String ip = "";
        for (String header : HEADERS) {
            if (!StringUtils.isEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
                break;
            }
            ip = request.getHeader(header);
        }
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        String comma = ",";
        String[] ips = ip.split(comma);
        for (String strIp : ips) {
            if (!(UNKNOWN.equalsIgnoreCase(strIp))) {
                ip = strIp;
                break;
            }
        }
        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ip.contains(comma)) {
            String[] ipArray = ip.split(comma);
            if (ipArray.length > 1) {
                ip = ipArray[0].trim();
            }
        }
        String compatibilityLocalIp = "0.0.0.0.0.0.0.1";
        if (compatibilityLocalIp.equals(ip)) {
            //兼容本地允许的情况
            ip = "127.0.0.1";
        }
        return ip;
    }
}

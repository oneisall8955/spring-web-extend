package com.oneisall.spring.web.extend.utils;

import com.google.common.collect.Maps;
import com.oneisall.spring.web.extend.exception.BusinessException;
import com.oneisall.spring.web.extend.exception.SystemExceptionEnum;
import lombok.extern.slf4j.Slf4j;

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

    public static Map<String, String> parameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> parametersData = CollectionMapUtil.initNewHashMap(parameterMap.size());
        for (Map.Entry<String, String[]> parameterEntry : parameterMap.entrySet()) {
            String[] value = parameterEntry.getValue();
            if (value != null && value.length == 1) {
                parametersData.put(parameterEntry.getKey(), value[0]);
            }
        }
        return parametersData;
    }
}

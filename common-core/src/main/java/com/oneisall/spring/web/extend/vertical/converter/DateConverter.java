package com.oneisall.spring.web.extend.vertical.converter;

import com.oneisall.spring.web.extend.vertical.VerticalAcrossMapping;
import com.oneisall.spring.web.extend.vertical.VerticalConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuzhicong
 **/
public class DateConverter implements VerticalConverter<Date> {

    private final Map<String, FastDateFormat> formatMap = new ConcurrentHashMap<>();

    @Override
    public Date convert(String source, VerticalAcrossMapping verticalAcrossMapping) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        String pattern = StringUtils.trim(verticalAcrossMapping.pattern());
        formatMap.computeIfAbsent(pattern, FastDateFormat::getInstance);
        FastDateFormat fastDateFormat = formatMap.get(pattern);
        String trim = source.trim();
        try {
            return fastDateFormat.parse(trim);
        } catch (ParseException e) {
            throw new IllegalArgumentException("cannot convert " + trim + " to Date (using pattern " + pattern + ")");
        }
    }
}

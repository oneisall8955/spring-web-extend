package com.oneisall.spring.web.extend.vertical.converter;

import com.oneisall.spring.web.extend.vertical.VerticalAcrossMapping;
import com.oneisall.spring.web.extend.vertical.VerticalConverter;

/**
 * @author liuzhicong
 **/
public class StringConverter implements VerticalConverter<String> {
    @Override
    public String convert(String source, VerticalAcrossMapping verticalAcrossMapping) {
        return source;
    }
}

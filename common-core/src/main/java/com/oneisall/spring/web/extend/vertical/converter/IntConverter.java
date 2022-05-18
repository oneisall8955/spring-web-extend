package com.oneisall.spring.web.extend.vertical.converter;

import com.oneisall.spring.web.extend.vertical.VerticalAcrossMapping;
import com.oneisall.spring.web.extend.vertical.VerticalConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liuzhicong
 **/
public class IntConverter implements VerticalConverter<Integer> {
    @Override
    public Integer convert(String source, VerticalAcrossMapping verticalAcrossMapping) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        return Integer.parseInt(source);
    }
}

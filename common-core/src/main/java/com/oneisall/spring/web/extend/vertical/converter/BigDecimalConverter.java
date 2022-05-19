package com.oneisall.spring.web.extend.vertical.converter;

import com.oneisall.spring.web.extend.vertical.VerticalAcrossMapping;
import com.oneisall.spring.web.extend.vertical.VerticalConverter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author liuzhicong
 **/
public class BigDecimalConverter implements VerticalConverter<BigDecimal> {
    @Override
    public BigDecimal convert(String source, VerticalAcrossMapping verticalAcrossMapping) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        return new BigDecimal(source.trim());
    }
}

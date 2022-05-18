package com.oneisall.spring.web.mongodb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.validation.constraints.NotBlank;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Function;

/**
 * @author liuzhicong
 **/
@Setter
@Getter
public class MongodbCondition {

    public static final String OP_IS = "is";
    public static final String OP_EQ = "eq";
    public static final String OP_NE = "ne";

    public static final String OP_LT = "lt";
    public static final String OP_LTE = "lte";

    public static final String OP_GT = "gt";
    public static final String OP_GTE = "gte";

    public static final String OP_IN = "in";
    public static final String OP_NIN = "nin";

    @NotBlank
    private String field;

    @NotBlank
    private String operator;

    private String value;

    private List<String> values;

    @JsonIgnore
    private Object mValue;

    @JsonIgnore
    private List<Object> mValues;

    public void fillCriteria(Criteria criteria, Field declaredField) {
        initValue(declaredField);
        fill(criteria);
    }

    private void initValue(Field declaredField) {

        if (value == null && values == null) {
            throw new IllegalArgumentException("值必须存在");
        }

        Class<?> fieldType = declaredField.getType();

        Function<String, Object> converter = null;

        if (fieldType == int.class || fieldType == Integer.class) {
            converter = Integer::parseInt;
        }

        if (fieldType == double.class || fieldType == Double.class) {
            converter = Double::parseDouble;
        }

        if (fieldType == float.class || fieldType == Float.class) {
            converter = Float::parseFloat;
        }

        if (fieldType == short.class || fieldType == Short.class) {
            converter = Short::parseShort;
        }

        if (fieldType == boolean.class || fieldType == Boolean.class) {
            converter = Boolean::parseBoolean;
        }

        if (fieldType == BigDecimal.class) {
            converter = Boolean::new;
        }

        if (fieldType == Date.class) {
            String pattern = "yyyy-MM-dd HH:mm:ss";
            converter = s -> {
                FastDateFormat instance = FastDateFormat.getInstance(pattern, TimeZone.getTimeZone("Asia/Shanghai"));
                try {
                    return instance.parse(s);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("不能转换日期，请使用格式：" + pattern);
                }
            };
        }

        if (fieldType == String.class) {
            converter = i -> i;
        }

        if (converter == null) {
            throw new IllegalArgumentException("无法转换值");
        }

        if (value != null) {
            mValue = converter.apply(value);
        }
        if (values != null) {
            mValues = new ArrayList<>(values.size());
            for (String v : values) {
                mValues.add(converter.apply(v));
            }
        }
    }

    private void fill(Criteria criteria) {
        switch (operator) {
            case OP_IS:
            case OP_EQ:
                criteria.and(field).is(mValue);
                break;
            case OP_NE:
                criteria.and(field).ne(mValue);
                break;
            case OP_LT:
                criteria.and(field).lt(mValue);
                break;
            case OP_LTE:
                criteria.and(field).lte(mValue);
                break;
            case OP_GT:
                criteria.and(field).gt(mValue);
                break;
            case OP_GTE:
                criteria.and(field).gte(mValue);
                break;
            case OP_IN:
                criteria.and(field).in(mValues.toArray());
                break;
            case OP_NIN:
                criteria.and(field).nin(mValues.toArray());
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作类型=" + operator);
        }
    }
}

package com.oneisall.spring.web.extend.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * json 工具
 *
 * @author liuzhicong
 **/
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ObjectMapper MAPPER_NONNULL = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * 将对象转换成json字符串。
     */
    public static String objectToJson(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.error("Object转Json异常", e);
        }
        return null;
    }

    public static String object2JsonNoNull(Object data) {
        try {
            return MAPPER_NONNULL.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.error("Object转json异常", e);
        }
        return null;
    }

    /**
     * 将json结果集转化为对象
     */
    public static <T> T jsonToObject(String jsonData, Class<T> beanType) {
        try {
            return MAPPER.readValue(jsonData, beanType);
        } catch (Exception e) {
            logger.error("json转对象异常", e);
        }
        return null;
    }

    /**
     * JSON 转 指定类型
     */
    public static <T> T jsonToObject(String json, TypeReference<T> valueTypeRef) {
        try {
            return MAPPER.readValue(json, valueTypeRef);
        } catch (IOException e) {
            logger.error("json转对象异常", e);
        }
        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            return MAPPER.readValue(jsonData, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

package com.oneisall.spring.web.extend.utils;

import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 复制工具
 *
 * @author : oneisall
 * @version : v1 2021/1/20 02:17
 */
public class CopyUtils {

    /**
     * 复制集合
     */
    public static <T, K> List<T> copyList(List<K> sourceList, Class<T> clazz) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Lists.newArrayList();
        }

        ArrayList<T> target = new ArrayList<>();
        sourceList.forEach(k -> target.add(copyObject(k, clazz)));
        return target;
    }

    /**
     * 复制对象
     */
    public static <T, K> T copyObject(K source, Class<T> clazz) {
        if (source == null) {
            return null;
        }

        T t = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(source, t);
        return t;
    }

    /**
     * 复制对象
     */
    public static <T> T copyObject(T source) {
        if (source == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T t = (T) BeanUtils.instantiateClass(source.getClass());
        BeanUtils.copyProperties(source, t);
        return t;
    }
}

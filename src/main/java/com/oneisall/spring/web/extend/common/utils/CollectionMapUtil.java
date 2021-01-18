package com.oneisall.spring.web.extend.common.utils;

import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;

import java.util.*;

/**
 * @author liuzhicong
 **/
public class CollectionMapUtil {

    /**
     * 判定map集合是否为空
     *
     * @param map 需要判断的map
     * @return boolean
     * @author liam
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * 判定map集合是否不为空
     *
     * @param map 需要判断的map
     * @return boolean
     * @author liam
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 判定collection集合是否为空
     *
     * @param collection 需要判断的collection
     * @return boolean
     * @author liam
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * 判定collection集合是否不为空
     *
     * @param collection 需要判断的collection
     * @return boolean
     * @author liam
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static <T> T safetyGetFirst(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        } else {
            if (collection instanceof List) {
                return ((List<T>) collection).get(0);
            }
            return collection.stream().findFirst().orElse(null);
        }
    }

    /** 模仿 ： {@code com.google.common.collect.Maps#newHashMapWithExpectedSize(int)} */
    public static <K, V> HashMap<K, V> initNewHashMap(int expectedSize) {
        return new HashMap<>(capacity(expectedSize));
    }

    /**
     * Returns a capacity that is sufficient to keep the map from being resized as long as it grows no
     * larger than expectedSize and the load factor is ≥ its default (0.75).
     */
    private static int capacity(int expectedSize) {
        // This is the calculation used in JDK8 to resize when a putAll
        // happens; it seems to be the most conservative calculation we
        // can make.  0.75 is the default load factor.
        return (int) ((float) expectedSize / 0.75F + 1.0F);
    }

    public static <T> List<T> null2empty(List<T> list) {
        if (list == null) {
            return Lists.newArrayList();
        }
        return list;
    }

    public static <T> Set<T> null2empty(Set<T> set) {
        if (set == null) {
            return Sets.newHashSet();
        }
        return set;
    }
}

package com.oneisall.spring.web.extend.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuzhicong
 **/
public class CollectionMapUtil {

    private CollectionMapUtil() {

    }

    public static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

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
    public static <K, V> Map<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return new HashMap<>(capacity(expectedSize));
    }

    /** 模仿 ： {@code com.google.common.collect.Maps#newLinkedHashMapWithExpectedSize(int)} */
    public static <K, V> Map<K, V> newLinkedHashMapWithExpectedSize(int expectedSize) {
        return new LinkedHashMap<>(capacity(expectedSize));
    }

    public static <K, V> Map<K, V> newConcurrentHashMapWithExpectedSize(int expectedSize) {
        return new ConcurrentHashMap<>(capacity(expectedSize));
    }


    public static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            checkNonNegative(expectedSize, "expectedSize");
            return expectedSize + 1;
        }
        if (expectedSize < MAX_POWER_OF_TWO) {
            return (int) (expectedSize / 0.75F + 1.0F);
        }
        return Integer.MAX_VALUE;
    }

    static void checkNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
        }
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

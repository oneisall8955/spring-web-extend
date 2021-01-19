package com.oneisall.spring.web.extend.model;

/**
 * 匹配器接口
 *
 * @author : oneisall
 * @version : v1 2021/1/20 00:20
 */
public interface StrategyMatcher<T> {
    /** 检阅是否匹配 */
    boolean match(T t);

    /** 排序,越大排在越前面 */
    int order();
}

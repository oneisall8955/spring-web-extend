package com.oneisall.spring.web.core.exmaple.facotry;

import com.oneisall.spring.web.extend.model.StrategyMatcher;

/**
 * TODO :please describe it in one sentence
 *
 * @author : oneisall
 * @version : v1 2021/1/20 02:00
 */
public interface Car extends StrategyMatcher<String> {
    void run();
}

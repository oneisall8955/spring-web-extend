package com.oneisall.spring.web.extend.concurrent;

import lombok.ToString;

import java.util.Objects;

/**
 * 默认的题目
 *
 * @author liuzhicong
 **/
@ToString
public class DefaultConQuestion<Q> implements ConQuestion {

    private final String key;

    private final Q realQuestion;

    public DefaultConQuestion(Q key) {
        this.key = Objects.isNull(key) ? "" : String.valueOf(key);
        realQuestion = key;
    }

    @Override
    public String questionKey() {
        return key;
    }


    /** 问题的值，默认是取题号，用于List<Integer>，List<String>这种形式的入参 */
    public Q realQuestion() {
        return realQuestion;
    }
}

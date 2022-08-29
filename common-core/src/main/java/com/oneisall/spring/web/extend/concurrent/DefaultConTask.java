package com.oneisall.spring.web.extend.concurrent;

import lombok.ToString;

import java.util.Objects;

/**
 * 默认的任务
 *
 * @author liuzhicong
 **/
@ToString
public class DefaultConTask<Q> implements ConTask {

    private final String taskKey;

    private final Q realTaskValue;

    public DefaultConTask(Q taskKey) {
        this.taskKey = Objects.isNull(taskKey) ? "" : String.valueOf(taskKey);
        realTaskValue = taskKey;
    }

    @Override
    public String taskKey() {
        return taskKey;
    }


    /** 任务的值，默认是取任务编号，用于List<Integer>，List<String>这种形式的入参 */
    public Q realTaskValue() {
        return realTaskValue;
    }
}

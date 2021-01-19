package com.oneisall.spring.web.extend.model;

import com.oneisall.spring.web.extend.utils.JsonUtils;

import java.util.function.Supplier;

/**
 * 日志包装，防止debug大json
 *
 * @author liuzhicong
 **/
public class LoggerWrapper {

    private static final LoggerWrapper NULL_OBJECT_MSG_LOGGER_WRAPPER;

    static {
        NULL_OBJECT_MSG_LOGGER_WRAPPER = new LoggerWrapper(() -> "null");
    }

    public LoggerWrapper() {
    }

    public LoggerWrapper(Supplier<String> msgSupplier) {
        this.msgSupplier = msgSupplier;
    }

    private Supplier<String> msgSupplier;

    public static LoggerWrapper buildJson(Object o) {
        if (o == null) {
            return NULL_OBJECT_MSG_LOGGER_WRAPPER;
        }
        LoggerWrapper loggerWrapper = new LoggerWrapper();
        loggerWrapper.msgSupplier = () -> JsonUtils.object2JsonNoNull(o);
        return loggerWrapper;
    }

    @Override
    public String toString() {
        return msgSupplier.get();
    }
}

package com.oneisall.spring.web.extend.common;

import com.oneisall.spring.web.extend.common.utils.JsonUtils;

import java.util.function.Supplier;

/**
 * @author liuzhicong
 **/
public class LoggerWrapper {

    private static final LoggerWrapper NULL_OBJECT_MSG_LOGGER_WRAPPER;

    static {
        NULL_OBJECT_MSG_LOGGER_WRAPPER = new LoggerWrapper(() -> "");
    }

    public LoggerWrapper() {
    }

    public LoggerWrapper(Supplier<String> msgSupplier) {

    }

    private Supplier<String> msgSupplier;

    public static LoggerWrapper buildJson(Object o) {
        if (o == null) {
            return NULL_OBJECT_MSG_LOGGER_WRAPPER;
        }
        LoggerWrapper loggerWrapper = new LoggerWrapper();
        loggerWrapper.msgSupplier = () -> {
            return JsonUtils.object2JsonNoNull(o);
        };
        return loggerWrapper;
    }

    @Override
    public String toString() {
        return msgSupplier.get();
    }
}

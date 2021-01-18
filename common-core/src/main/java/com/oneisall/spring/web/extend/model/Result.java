package com.oneisall.spring.web.extend.model;

import com.oneisall.spring.web.extend.i18n.MessageException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * 通用结果返回类
 *
 * @author liuzhicong
 */
@Setter
@Getter
public class Result<T> implements Serializable {
    private boolean success;
    private int code;
    private T data;
    private String message;

    public Result(boolean success, int code, T data, String message) {
        this.success = success;
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <T> Result<T> succeed() {
        return of(true, null, HttpStatus.OK.value(), "成功");
    }

    public static <T> Result<T> succeed(String msg) {
        return of(true, null, HttpStatus.OK.value(), msg);
    }

    public static <T> Result<T> succeed(T data, String msg) {
        return of(true, data, HttpStatus.OK.value(), msg);
    }

    public static <T> Result<T> succeed(T data) {
        return of(true, data, HttpStatus.OK.value(), null);
    }

    public static <T> Result<T> of(Boolean success, T data, Integer code, String msg) {
        return new Result<>(success, code, data, msg);
    }

    public static <T> Result<T> failed(MessageException messageException) {
        return of(false, null, messageException.getCode(), messageException.getMsg());
    }
}

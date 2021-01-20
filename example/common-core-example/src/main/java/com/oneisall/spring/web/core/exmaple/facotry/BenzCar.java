package com.oneisall.spring.web.core.exmaple.facotry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * TODO :please describe it in one sentence
 *
 * @author : oneisall
 * @version : v1 2021/1/20 02:05
 */
@Component
@Slf4j
public class BenzCar implements Car {

    @Override
    public void run() {
        log.info("奔驰快跑！{}", this);
    }

    @Override
    public boolean match(String s) {
        return "benz".equalsIgnoreCase(s);
    }
}

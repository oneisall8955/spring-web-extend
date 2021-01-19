package com.oneisall.spring.web.core.exmaple.facotry;

import com.oneisall.spring.web.extend.model.BaseStrategyFactory;
import com.oneisall.spring.web.extend.model.StrategyFactoryInfo;
import org.springframework.stereotype.Component;

/**
 * TODO :please describe it in one sentence
 *
 * @author : oneisall
 * @version : v1 2021/1/20 01:58
 */
@Component
@StrategyFactoryInfo("汽车")
public class CarFactory extends BaseStrategyFactory<Car, String> {
    @Override
    public boolean instanceNew() {
        return false;
    }

}

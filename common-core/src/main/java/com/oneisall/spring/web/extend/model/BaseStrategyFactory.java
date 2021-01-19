package com.oneisall.spring.web.extend.model;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * 抽象策略基础工厂
 *
 * @author : oneisall
 * @version : v1 2021/1/20 00:16
 */
@Slf4j
public class BaseStrategyFactory<S extends StrategyMatcher<T>, T> implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    protected List<S> STRATEGIES;

    protected String factoryName;

    public BaseStrategyFactory() {
        StrategyFactoryInfo factoryInfo = this.getClass().getAnnotation(StrategyFactoryInfo.class);
        factoryName = Optional.ofNullable(factoryInfo).map(item -> StringUtils.defaultString(item.value(), item.name())).orElse("ANONYMOUS");
    }

    public Optional<S> getInstance(T t) {
        for (S strategy : STRATEGIES) {
            if (strategy.match(t)) {
                return Optional.of(strategy);
            }
        }
        log.info("【{}】工厂根据{}找不到对应处理策略", factoryName, t);
        return Optional.empty();
    }

    public List<S> getInstances(T t) {
        List<S> instances = new LinkedList<>();
        for (S strategy : STRATEGIES) {
            if (strategy.match(t)) {
                instances.add(strategy);
            }
        }
        return instances;
    }

    public List<S> getInstances() {
        return Lists.newArrayList(STRATEGIES);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings({"unchecked"})
    @PostConstruct
    public void init() {
        log.debug("【{}】工厂PostConstruct", factoryName);
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<S> sClass = (Class<S>) parameterizedType.getActualTypeArguments()[0];
        Map<String, S> beans = applicationContext.getBeansOfType(sClass);
        STRATEGIES = Lists.newArrayListWithCapacity(beans.size());
        STRATEGIES.addAll(beans.values());
        STRATEGIES.sort(Comparator.comparingInt(S::order).reversed());
        log.info("【{}】工厂根据找到{}对应处理策略", factoryName, STRATEGIES.size());
        for (S strategy : STRATEGIES) {
            log.info("处理策略:order:{},{}", strategy.order(), strategy);
        }
    }
}

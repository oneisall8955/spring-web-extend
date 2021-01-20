package com.oneisall.spring.web.extend.model;

import com.google.common.collect.Lists;
import com.oneisall.spring.web.extend.utils.CopyUtils;
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

    protected List<S> strategies;

    protected String factoryName;

    /** 是否生成新的对象 */
    public boolean ofNew() {
        return false;
    }

    protected S ofInstance(S source) {
        if (ofNew()) {
            return CopyUtils.copyObject(source);
        }
        return source;
    }

    public BaseStrategyFactory() {
        StrategyFactoryInfo factoryInfo = this.getClass().getAnnotation(StrategyFactoryInfo.class);
        factoryName = Optional.ofNullable(factoryInfo).map(item -> StringUtils.defaultString(item.value(), item.name())).orElse("ANONYMOUS");
    }

    public Optional<S> getInstance(T t) {
        for (S strategy : strategies) {
            if (strategy.match(t)) {
                return Optional.of(ofInstance(strategy));
            }
        }
        log.info("【{}】工厂根据{}找不到对应处理策略", factoryName, t);
        return Optional.empty();
    }

    public Optional<S> getInstance(Class<? extends S> clazz) {
        for (S strategy : strategies) {
            if (strategy.getClass() == clazz) {
                return Optional.of(ofInstance(strategy));
            }
        }
        log.warn("【{}】工厂找不到指定的{}对应处理策略，尝试创建", factoryName, clazz);
        S newInstance;
        try {
            newInstance = clazz.newInstance();
            strategies.add(newInstance);
            return Optional.of(ofInstance(newInstance));
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("【{}】工厂找不到指定的{}对应处理策略，尝试创建失败", factoryName, clazz, e);
        }
        log.warn("【{}】工厂找不到指定的{}对应处理策略，并且创建失败，返回empty optional", factoryName, clazz);
        return Optional.empty();
    }

    public List<S> getInstances(T t) {
        List<S> instances = new LinkedList<>();
        for (S strategy : strategies) {
            if (strategy.match(t)) {
                instances.add(CopyUtils.copyObject(strategy));
            }
        }
        return instances;
    }

    public List<S> getInstances() {
        if (ofNew()) {
            ArrayList<S> result = Lists.newArrayListWithCapacity(strategies.size());
            for (S strategy : strategies) {
                S copyObject = CopyUtils.copyObject(strategy);
                result.add(copyObject);
            }
            return result;
        }
        return Lists.newArrayList(strategies);
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
        Class<S> actualClass = (Class<S>) parameterizedType.getActualTypeArguments()[0];
        Map<String, S> beans = applicationContext.getBeansOfType(actualClass);
        strategies = Lists.newCopyOnWriteArrayList();
        strategies.addAll(beans.values());
        strategies.sort(Comparator.comparingInt(S::order).reversed());
        log.info("【{}】工厂根据找到{}对应处理策略", factoryName, strategies.size());
        for (S strategy : strategies) {
            log.info("处理策略:order:{},{}", strategy.order(), strategy);
        }
    }
}

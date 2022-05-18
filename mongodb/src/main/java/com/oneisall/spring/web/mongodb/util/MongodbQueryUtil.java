package com.oneisall.spring.web.mongodb.util;

import com.mongodb.ConnectionString;
import com.oneisall.spring.web.extend.exception.SystemExceptionEnum;
import com.oneisall.spring.web.extend.model.Result;
import com.oneisall.spring.web.extend.utils.CollectionMapUtil;
import com.oneisall.spring.web.mongodb.model.MongodbCondition;
import com.oneisall.spring.web.mongodb.model.MongodbQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * @author liuzhicong
 **/
@Slf4j
public class MongodbQueryUtil {

    public static Result<?> query(MongodbQuery myMongodbQuery) {

        MongoTemplate mongoTemplate;
        String uri = myMongodbQuery.getUri();
        try {
            ConnectionString connectionString = new ConnectionString(uri);
            SimpleMongoClientDbFactory mongoDbFactory = new SimpleMongoClientDbFactory(connectionString);
            mongoTemplate = new MongoTemplate(mongoDbFactory);
        } catch (Exception e) {
            log.error("创建mongodb连接失败，uri={}", uri, e);
            return Result.failed(SystemExceptionEnum.SYSTEM_ERROR);
        }

        Class<?> targetClass;
        try {
            targetClass = myMongodbQuery.findClass();
        } catch (Exception e) {
            log.error("类找不到，class={}", myMongodbQuery.getClazz(), e);
            return Result.failed(SystemExceptionEnum.SYSTEM_ERROR);
        }

        Pageable pageable;
        if (CollectionMapUtil.isNotEmpty(myMongodbQuery.getSortBys()) && myMongodbQuery.getSortDirection() != null) {
            Sort orders = Sort.by(myMongodbQuery.getSortDirection(), myMongodbQuery.getSortBys().toArray(new String[]{}));
            pageable = PageRequest.of(myMongodbQuery.getPageNumber() - 1, myMongodbQuery.getPageSize(), orders);
        } else {
            pageable = PageRequest.of(myMongodbQuery.getPageNumber() - 1, myMongodbQuery.getPageSize());
        }
        Criteria criteria = new Criteria();

        for (MongodbCondition myCondition : CollectionMapUtil.null2empty(myMongodbQuery.getConditions())) {
            String field = myCondition.getField();
            Field declaredField;
            try {
                declaredField = targetClass.getDeclaredField(field);
            } catch (NoSuchFieldException e) {
                log.error("字段找不到，filed={}", field, e);
                return Result.failed(SystemExceptionEnum.SYSTEM_ERROR);
            }
            myCondition.fillCriteria(criteria, declaredField);
        }

        Query query = new Query(criteria);
        long totalCount = mongoTemplate.count(query, targetClass);
        Page<?> pages;
        List<?> list;
        if (totalCount > 0) {
            list = mongoTemplate.find(query.with(pageable), targetClass);
            pages = new PageImpl<>(list, pageable, totalCount);
        } else {
            pages = new PageImpl<>(Collections.emptyList());
        }
        return Result.succeed(pages);
    }
}

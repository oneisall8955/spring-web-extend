package com.oneisall.spring.web.extend.vertical;

/**
 * @author liuzhicong
 **/
public interface VerticalConverter<T>{
    T convert(String source,VerticalAcrossMapping verticalAcrossMapping);
}

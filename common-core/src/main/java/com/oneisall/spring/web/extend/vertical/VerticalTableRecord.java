package com.oneisall.spring.web.extend.vertical;


/**
 * @author liuzhicong
 **/
public interface VerticalTableRecord {

    String primaryGroupKey();

    String secondaryGroupKey();

    String value();

    @SuppressWarnings("rawtypes")
    default Comparable mapOneSortValue() {
        throw new UnsupportedOperationException("Many-to-one is not allowed by default");
    }
}

package com.oneisall.spring.web.core.exmaple.vertical;

import com.oneisall.spring.web.extend.vertical.VerticalTableRecord;
import lombok.*;

import java.util.Date;

/**
 * @author liuzhicong
 **/
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductDataDO implements VerticalTableRecord {

    private Integer id;

    private String productNo;

    private Integer dataType;

    private String value;

    private Date createdAt;

    private Date updatedAt;

    @Override
    public String primaryGroupKey() {
        return productNo;
    }

    @Override
    public String secondaryGroupKey() {
        return dataType + "";
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public Comparable<?> mapOneSortValue() {
        return createdAt;
    }
}

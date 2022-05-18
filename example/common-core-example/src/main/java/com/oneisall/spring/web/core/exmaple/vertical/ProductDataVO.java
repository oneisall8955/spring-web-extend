package com.oneisall.spring.web.core.exmaple.vertical;

import com.oneisall.spring.web.extend.vertical.AcrossVoEntity;
import com.oneisall.spring.web.extend.vertical.VerticalAcrossMapping;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author liuzhicong
 **/
@Setter
@Getter
@ToString
public class ProductDataVO implements AcrossVoEntity {

    private String productNo;

    @VerticalAcrossMapping(value = "1", checkManyToOne = false)
    private String productName;

    @VerticalAcrossMapping("2")
    private List<String> attrNameList;

    @VerticalAcrossMapping("3")
    private Integer productStatus;

    @Override
    public void setPrimaryGroupKey(String primaryGroupKey) {
        productNo = primaryGroupKey;
    }
}

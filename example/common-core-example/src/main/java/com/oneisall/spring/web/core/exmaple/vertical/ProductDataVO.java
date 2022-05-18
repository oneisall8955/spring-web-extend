package com.oneisall.spring.web.core.exmaple.vertical;

import com.oneisall.spring.web.extend.vertical.AcrossVoEntity;
import com.oneisall.spring.web.extend.vertical.VerticalAcrossMapping;
import com.oneisall.spring.web.extend.vertical.converter.IntConverter;
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

    private String itemcode;

    @VerticalAcrossMapping(mappingSecondaryValue = "1", throwFoundManyButMappingOne = false)
    private String productName;

    @VerticalAcrossMapping(mappingSecondaryValue = "2")
    private List<String> attrNameList;

    @VerticalAcrossMapping(mappingSecondaryValue = "3", usingConverter = IntConverter.class)
    private Integer productStatus;

    @Override
    public void setPrimaryGroupKey(String primaryGroupKey) {
        itemcode = primaryGroupKey;
    }
}

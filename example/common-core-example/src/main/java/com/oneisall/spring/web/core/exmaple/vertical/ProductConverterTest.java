package com.oneisall.spring.web.core.exmaple.vertical;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liuzhicong
 **/
public class ProductConverterTest {
    public static void main(String[] args) throws ParseException {
        FastDateFormat instance = FastDateFormat.getInstance("yyyy-MM-dd");

        ArrayList<ProductDataDO> source = Lists.newArrayList();
        ProductDataDO e0 = new ProductDataDO(0, "1001", 1, "apple01", instance.parse("2022-01-02"), new Date());
        ProductDataDO e1 = new ProductDataDO(1, "1001", 1, "apple02", instance.parse("2022-01-03"), new Date());
        ProductDataDO e2 = new ProductDataDO(2, "1001", 2, "color", new Date(), new Date());
        ProductDataDO e3 = new ProductDataDO(3, "1001", 2, "size", new Date(), new Date());
        ProductDataDO e4 = new ProductDataDO(4, "1001", 3, "1", new Date(), new Date());

        ProductDataDO e5 = new ProductDataDO(4, "1002", 1, "ball", new Date(), new Date());

        source.add(e0);
        source.add(e1);
        source.add(e2);
        source.add(e3);
        source.add(e4);
        source.add(e5);
        ProductConverter productConverter = new ProductConverter();
        List<ProductDataVO> convert = productConverter.convert(source);
        System.out.println(convert);
    }
}

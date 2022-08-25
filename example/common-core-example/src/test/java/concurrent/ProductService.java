package concurrent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuzhicong
 **/
public class ProductService {

    private final Map<Long, ConProductDetail> productDetailMap = new HashMap<>();
    private final Map<Long, ConProduct> productMap = new HashMap<>();

    {
        ConProduct product1001 = new ConProduct();
        product1001.setProductId(1001L);
        product1001.setStock(2001L);
        productMap.put(product1001.getProductId(), product1001);

        ConProduct product1002 = new ConProduct();
        product1002.setProductId(1002L);
        product1002.setStock(2002L);
        productMap.put(product1002.getProductId(), product1002);

        ConProductDetail productDetail1001 = new ConProductDetail();
        productDetail1001.setProductId(1001L);
        productDetail1001.setProductName("雅迪 yadea 小金果48V12AH铅酸电动车电动自行车 男女通用代步车 白色");
        productDetail1001.setCategoryName("电动车");
        productDetail1001.setFirstImageUrl("https://img12.360buyimg.com/n1/jfs/t1/212739/24/157/201075/61666c09Edd9d2235/10cbc6fe14e453ea.jpg");
        productDetailMap.put(productDetail1001.getProductId(), productDetail1001);

        ConProductDetail productDetail1002 = new ConProductDetail();
        productDetail1002.setProductId(1002L);
        productDetail1002.setProductName("小米电视6 OLED 65英寸 4KHDR 超薄全面屏 MEMC运动补偿 3+32GB 远场语音 护眼教育电视机L65M7-Z2以旧换新");
        productDetail1002.setCategoryName("平板电视");
        productDetail1002.setFirstImageUrl("https://img10.360buyimg.com/n1/jfs/t1/105942/36/32037/95174/63064846E3effd776/6b86988b63562aad.jpg.avif");
        productDetailMap.put(productDetail1002.getProductId(), productDetail1001);

    }

    public ConProduct findProduct(Long productId) {
        return productMap.get(productId);
    }

    public ConProductDetail findDetail(Long productId) {
        return productDetailMap.get(productId);
    }

    public boolean update(ConProduct conProduct) {
        if (productMap.containsKey(conProduct.getProductId())) {
            productMap.put(conProduct.getProductId(), conProduct);
            return true;
        }
        return false;
    }

    public void increaseStock(ConProduct p) {
        ConProduct conProduct = productMap.get(p.getProductId());
        if (conProduct != null) {
            conProduct.setStock(conProduct.getStock() + 1);
        }
    }

    public void remove(Long productId) {
        productMap.remove(productId);
        productDetailMap.remove(productId);
    }
}

package concurrent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author liuzhicong
 **/
@Setter
@Getter
@ToString
public class ConProductDetail {
    private Long productId;
    private String productName;
    private String categoryName;
    private String firstImageUrl;
}

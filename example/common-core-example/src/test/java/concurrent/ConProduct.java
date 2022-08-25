package concurrent;

import com.oneisall.spring.web.extend.concurrent.ConQuestion;
import lombok.*;

/**
 * @author liuzhicong
 **/
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ConProduct implements ConQuestion {

    private Long productId;

    private Long stock;

    @Override
    public String questionKey() {
        return productId + "";
    }


}

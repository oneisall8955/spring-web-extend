package concurrent;

import com.oneisall.spring.web.extend.concurrent.ConTask;
import lombok.*;

/**
 * @author liuzhicong
 **/
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ConProduct implements ConTask {

    private Long productId;

    private Long stock;

    @Override
    public String taskKey() {
        return productId + "";
    }


}

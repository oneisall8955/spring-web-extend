import com.oneisall.spring.web.core.exmaple.facotry.Car;
import com.oneisall.spring.web.core.exmaple.facotry.CarFactory;
import com.oneisall.spring.web.core.exmaple.facotry.CommonCoreApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * TODO :please describe it in one sentence
 *
 * @author : oneisall
 * @version : v1 2021/1/20 02:23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {CommonCoreApplication.class})
public class MyTest {

    @Resource
    private CarFactory carFactory;

    @Test
    public void test() {
        Optional<Car> benz1 = carFactory.getInstance("benz");
        benz1.ifPresent(Car::run);
        Optional<Car> benz2 = carFactory.getInstance("benz");
        benz2.ifPresent(Car::run);
    }
}

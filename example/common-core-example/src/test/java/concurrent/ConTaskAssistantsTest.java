package concurrent;

import cn.hutool.core.collection.CollUtil;
import com.oneisall.spring.web.extend.concurrent.ConTaskAssistants;
import com.oneisall.spring.web.extend.concurrent.ConTaskExceptionEnum;
import com.oneisall.spring.web.extend.concurrent.DefaultConTask;
import com.oneisall.spring.web.extend.concurrent.DefaultVoidReturn;
import com.oneisall.spring.web.extend.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ConTaskAssistantsTest {

    ProductService productService = new ProductService();

    private static final ThreadPoolTaskExecutor executor;

    static {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cpuCoreNum * 2);
        executor.setMaxPoolSize(cpuCoreNum * 2);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("Test-Thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
    }

    /*
     *  测试构造函数，最通用的形式
     */
    @Test
    public void testUniversal() {
        ConProduct conProduct1001 = new ConProduct(1001L, 2001L);
        ConProduct conProduct1002 = new ConProduct(1002L, 2001L);
        ConProduct conProduct1003 = new ConProduct(1003L, 2001L);
        List<ConProduct> productList = CollUtil.newArrayList(conProduct1001, conProduct1002, conProduct1003);

        // ConProduct 实现了 ConTask 接口
        Function<ConProduct, Boolean> function = (ConProduct p) -> productService.update(p);
        // 第一个参数是 task 列表，第二个参数是根据 task 任务处理的方法，第三个参数是指定线程池
        ConTaskAssistants<Boolean> conAssistants1 = new ConTaskAssistants<>(productList, function, executor);
        // 执行任务
        conAssistants1.work();
        // 获取结果
        Assert.assertTrue(conAssistants1.getResult(conProduct1001.taskKey()));
        Assert.assertTrue(conAssistants1.getResult(conProduct1002.taskKey()));
        Assert.assertFalse(conAssistants1.getResult(conProduct1003.taskKey()));

        // 构造方法及静态创建 ConTaskAssistants 都会用重载方式提供默认的线程池
        ConTaskAssistants<Boolean> conAssistants2 = new ConTaskAssistants<>(productList, function);
        conAssistants2.work();
    }

    /**
     * 测试静态方法，tasks 是 <pre> {@code List<Integer>,List<String>}</pre> 此种形式
     */
    @Test
    public void testOfBaseType() {
        Function<ConProduct, ConProductDetail> function1 = (ConProduct p) -> productService.findDetail(p.getProductId());

        ConProduct conProduct1001 = new ConProduct(1001L, 2001L);
        ConProduct conProduct1002 = new ConProduct(1002L, 2001L);
        ConProduct conProduct1003 = new ConProduct(1003L, 2001L);
        List<ConProduct> productList = CollUtil.newArrayList(conProduct1001, conProduct1002, conProduct1003);

        // 构造方法创建 ConTaskAssistants，需要类实现 ConTask
        ConTaskAssistants<ConProductDetail> assistants1 = new ConTaskAssistants<>(productList, function1);
        assistants1.work();

        // ============================================

        // 静态方法创建 ConTaskAssistants，每一个 Long 会被包装成 DefaultConTask<Long>
        List<Long> productIds = productList.stream().map(ConProduct::getProductId).collect(Collectors.toList());
        // 找到答案问题的方法，入参其实是 DefaultConTask<Long>
        Function<DefaultConTask<Long>, ConProductDetail> function2 = (DefaultConTask<Long> defaultConTask)
                // 通过 defaultConTask.realTaskValue()获取原始的 Long 值
                -> productService.findDetail(defaultConTask.realTaskValue());
        ConTaskAssistants<ConProductDetail> assistants2 = ConTaskAssistants.ofBaseType(productIds, function2, executor);
        assistants2.work();

        // 重载方式提供默认的线程池
        ConTaskAssistants<ConProductDetail> assistants3 = ConTaskAssistants.ofBaseType(productIds, function2);
        assistants3.work();

        // assert
        Assert.assertEquals(assistants1.getResult(conProduct1001.taskKey()).getProductName(),
                assistants2.getResult(conProduct1001.taskKey()).getProductName());

        Assert.assertEquals(assistants1.getResult(conProduct1002.taskKey()).getProductName(),
                assistants2.getResult(conProduct1002.taskKey()).getProductName());

        Assert.assertNull(assistants1.getResult(conProduct1003.taskKey()));
        Assert.assertNull(assistants2.getResult(conProduct1003.taskKey()));
    }

    /**
     * 测试静态方法，无返回值的任务处理方式，包装一层返回默认的值
     */
    @Test
    public void testOfVoid() {
        ConProduct conProduct1001 = new ConProduct(1001L, 2001L);
        ConProduct conProduct1002 = new ConProduct(1002L, 2002L);
        ConProduct conProduct1003 = new ConProduct(1003L, 3003L);
        List<ConProduct> productList = CollUtil.newArrayList(conProduct1001, conProduct1002, conProduct1003);

        // 无返回的，转换成提供默认的任务结果 DefaultVoidReturn
        Consumer<ConProduct> consumer = (ConProduct p) -> productService.increaseStock(p);
        ConTaskAssistants<DefaultVoidReturn> conAssistants = ConTaskAssistants.ofVoid(productList, consumer);
        conAssistants.work();

        Assert.assertEquals(2002L, (long) productService.findProduct(conProduct1001.getProductId()).getStock());
        Assert.assertEquals(2003L, (long) productService.findProduct(conProduct1002.getProductId()).getStock());
        Assert.assertNull(productService.findProduct(conProduct1003.getProductId()));
    }

    /**
     * 测试静态方法，无返回值的任务处理方式，包装一层返回默认的值 并且入参是  <pre> {@code List<Integer>,List<String>}</pre> 此种形式
     */
    @Test
    public void testOfVoidBaseType() {

        List<Long> productIds = CollUtil.newArrayList(1001L, 1002L);

        Assert.assertNotNull(productService.findProduct(1001L));
        Assert.assertNotNull(productService.findProduct(1002L));

        Consumer<DefaultConTask<Long>> consumer = (DefaultConTask<Long> longDefaultConTask) -> productService.remove(longDefaultConTask.realTaskValue());
        ConTaskAssistants<DefaultVoidReturn> conAssistants = ConTaskAssistants.ofVoidBaseType(productIds, consumer);
        conAssistants.work();

        Assert.assertNull(productService.findProduct(1001L));
        Assert.assertNull(productService.findProduct(1002L));
    }


    /**
     * 任务有失败时，随机获取一个失败的任务
     * 并且任务是 <pre> {@code List<? extends ConTask>} </pre>此种基础类型的
     */
    @Test
    public void testCheckFail() {
        ConProduct conProduct1001 = new ConProduct(1001L, 2001L);
        ConProduct conProduct1002 = new ConProduct(1002L, 2001L);
        ConProduct conProduct1003 = new ConProduct(1003L, 2001L);
        List<ConProduct> productList = CollUtil.newArrayList(conProduct1001, conProduct1002, conProduct1003);

        // 模拟失败
        Function<ConProduct, Boolean> function = (ConProduct p) -> {
            if (productService.findProduct(p.getProductId()) == null) {
                throw new IllegalArgumentException("product not found : " + p.getProductId());
            }
            return productService.update(p);
        };

        ConTaskAssistants<Boolean> assistants = new ConTaskAssistants<>(productList, function);

        assistants.work();

        // 是否有任务失败了
        Assert.assertTrue(assistants.existFail());
        // 随机获取一个失败的
        Assert.assertNotNull(assistants.anyFailKey());
        // 找出所有的失败任务
        Assert.assertTrue(CollUtil.isNotEmpty(assistants.allFailKeys()));
        // 找出随机一个失败的
        ConProduct fConProduct = assistants.anyFailTask();
        Assert.assertEquals((long) fConProduct.getProductId(), (long) conProduct1003.getProductId());

        // 执行成功
        Assert.assertFalse(assistants.checkFailKey(conProduct1001.taskKey()));
        Assert.assertFalse(assistants.checkFailKey(conProduct1002.taskKey()));
        // 执行失败
        Assert.assertTrue(assistants.checkFailKey(conProduct1003.taskKey()));

        // 答案是空的
        Assert.assertNull(assistants.getResult(conProduct1003.taskKey()));

        // 类型转换异常情况1
        try {
            DefaultConTask<String> defaultConTask = assistants.anyFailTask();
        } catch (Exception e) {
            log.error("发生了类型转换异常", e);
        }

        // 类型转换异常情况2
        try {
            String conTask = assistants.anyFailDefaultConTask();
        } catch (Exception e) {
            log.error("发生了类型转换异常", e);
            Assert.assertTrue(e instanceof BusinessException
                    && ((BusinessException) e).getCode().equals(ConTaskExceptionEnum.TYPE_CAST_ERROR.getCode()));
        }
    }

    /**
     * 任务有失败时，随机获取一个失败的任务
     * 并且任务是 <pre> {@code List<Integer> }  </pre>此种基础类型的
     */
    @Test
    public void testAnyFailDefaultConTask() {

        List<Integer> integers = CollUtil.newArrayList(0, 1, 2);

        // 模拟失败
        Function<DefaultConTask<Integer>, Integer> function = (DefaultConTask<Integer> q) -> 10 / q.realTaskValue();

        ConTaskAssistants<Integer> assistants = ConTaskAssistants.ofBaseType(integers, function);

        assistants.work();

        // 对于入参是 List<Integer> 此类任务，获取失败的任务
        // 第一种方式，获取 ConTask 实现类 DefaultConTask ，再通过 DefaultConTask.realTaskValue() 获取
        DefaultConTask<Integer> defaultConTask = assistants.anyFailTask();
        Assert.assertEquals(0, (int) defaultConTask.realTaskValue());
        // 第二种方式，通过 anyFailDefaultConTask 直接获取
        Integer errQ = assistants.anyFailDefaultConTask();
        Assert.assertEquals(0, (int) errQ);

        // 类型转换异常
        try {
            ConProduct product = assistants.anyFailDefaultConTask();
        } catch (Exception e) {
            log.error("发生了类型转换异常", e);
            Assert.assertTrue(e instanceof ClassCastException);
        }
    }
}

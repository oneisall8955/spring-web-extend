package concurrent;

import com.google.common.collect.Lists;
import com.oneisall.spring.web.extend.concurrent.ConAssistants;
import com.oneisall.spring.web.extend.concurrent.DefaultConQuestion;
import com.oneisall.spring.web.extend.utils.CollectionMapUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author liuzhicong
 **/
public class ConAssistantsTest {

    ProductService productService = new ProductService();

    private static final ThreadPoolTaskExecutor executor;

    static {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cpuCoreNum * 2);
        executor.setMaxPoolSize(cpuCoreNum * 2);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("Test-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
    }

    /*
     *  测试构造函数，最普遍的形式
     */
    @Test
    public void testUniversal() {
        ConProduct conProduct1001 = new ConProduct(1001L, 2001L);
        ConProduct conProduct1002 = new ConProduct(1002L, 2001L);
        ConProduct conProduct1003 = new ConProduct(1003L, 2001L);
        List<ConProduct> productList = Lists.newArrayList(conProduct1001, conProduct1002, conProduct1003);

        // ConProduct 实现了 ConQuestion 接口
        Function<ConProduct, Boolean> function = (ConProduct p) -> productService.update(p);
        // 第一个参数是 question 列表，第二个参数是根据 question，找到 answer 的方法，第三个参数是指定线程池
        ConAssistants<Boolean> conAssistants1 = new ConAssistants<>(productList, function, executor);
        // 构造方法及静态创建 ConAssistants 都会用重载方式提供默认的线程池
        ConAssistants<Boolean> conAssistants2 = new ConAssistants<>(productList, function);
        conAssistants1.work();
        conAssistants2.work();

        Assert.assertTrue(conAssistants1.getAnswer(conProduct1001.questionKey()));
        Assert.assertTrue(conAssistants1.getAnswer(conProduct1002.questionKey()));
        Assert.assertFalse(conAssistants1.getAnswer(conProduct1003.questionKey()));
    }

    /*
     * 测试静态方法，questions 是 List<Integer>,List<String> 此种形式
     */
    @Test
    public void testOfBaseType() {
        Function<ConProduct, ConProductDetail> function1 = (ConProduct p) -> productService.findDetail(p.getProductId());

        ConProduct conProduct1001 = new ConProduct(1001L, 2001L);
        ConProduct conProduct1002 = new ConProduct(1002L, 2001L);
        ConProduct conProduct1003 = new ConProduct(1003L, 2001L);
        List<ConProduct> productList = Lists.newArrayList(conProduct1001, conProduct1002, conProduct1003);

        // 构造方法创建 ConAssistants
        ConAssistants<ConProductDetail> assistants1 = new ConAssistants<>(productList, function1);
        assistants1.work();

        // ============================================

        // 静态方法创建 ConAssistants，每一个 Long 会被包装成 DefaultConQuestion<Long>
        List<Long> productIds = productList.stream().map(ConProduct::getProductId).collect(Collectors.toList());
        // 找到答案问题的方法，入参其实是 DefaultConQuestion<Long>
        Function<DefaultConQuestion<Long>, ConProductDetail> function2 = (DefaultConQuestion<Long> defaultConQuestion)
                // 通过 defaultConQuestion.realQuestion()获取原始的 Long 值
                -> productService.findDetail(defaultConQuestion.realQuestion());
        ConAssistants<ConProductDetail> assistants2 = ConAssistants.ofBaseType(productIds, function2, executor);
        assistants2.work();

        // 重载方式提供默认的线程池
        ConAssistants<ConProductDetail> assistants3 = ConAssistants.ofBaseType(productIds, function2);
        assistants3.work();


        // assert
        Assert.assertEquals(assistants1.getAnswer(conProduct1001.questionKey()).getProductName(),
                assistants2.getAnswer(conProduct1001.questionKey()).getProductName());

        Assert.assertEquals(assistants1.getAnswer(conProduct1002.questionKey()).getProductName(),
                assistants2.getAnswer(conProduct1002.questionKey()).getProductName());

        Assert.assertNull(assistants1.getAnswer(conProduct1003.questionKey()));
        Assert.assertNull(assistants2.getAnswer(conProduct1003.questionKey()));
    }

    /*
     * 测试静态方法，无返回值的 answer，包装一层返回默认的值
     */
    @Test
    public void testOfVoid() {
        ConProduct conProduct1001 = new ConProduct(1001L, 2001L);
        ConProduct conProduct1002 = new ConProduct(1002L, 2002L);
        ConProduct conProduct1003 = new ConProduct(1003L, 3003L);
        List<ConProduct> productList = Lists.newArrayList(conProduct1001, conProduct1002, conProduct1003);

        // 无返回的，转换成提供默认的答案 VoidValue
        Consumer<ConProduct> consumer = (ConProduct p) -> productService.increaseStock(p);
        ConAssistants<ConAssistants.VoidValue> conAssistants = ConAssistants.ofVoid(productList, consumer);
        conAssistants.work();

        Assert.assertEquals(2002L, (long) productService.findProduct(conProduct1001.getProductId()).getStock());
        Assert.assertEquals(2003L, (long) productService.findProduct(conProduct1002.getProductId()).getStock());
        Assert.assertNull(productService.findProduct(conProduct1003.getProductId()));
    }

    /*
     * 测试静态方法，无返回值的 answer，包装一层返回默认的值 并且入参是  List<Integer>,List<String> 此种形式
     */
    @Test
    public void testOfVoidBaseType() {

        List<Long> productIds = Lists.newArrayList(1001L, 1002L);

        Assert.assertNotNull(productService.findProduct(1001L));
        Assert.assertNotNull(productService.findProduct(1002L));

        Consumer<DefaultConQuestion<Long>> consumer = (DefaultConQuestion<Long> longDefaultConQuestion) -> productService.remove(longDefaultConQuestion.realQuestion());
        ConAssistants<ConAssistants.VoidValue> conAssistants = ConAssistants.ofVoidBaseType(productIds, consumer);
        conAssistants.work();

        Assert.assertNull(productService.findProduct(1001L));
        Assert.assertNull(productService.findProduct(1002L));
    }


    @Test
    public void testCheckFail() {
        ConProduct conProduct1001 = new ConProduct(1001L, 2001L);
        ConProduct conProduct1002 = new ConProduct(1002L, 2001L);
        ConProduct conProduct1003 = new ConProduct(1003L, 2001L);
        List<ConProduct> productList = Lists.newArrayList(conProduct1001, conProduct1002, conProduct1003);

        // 模拟失败
        Function<ConProduct, Boolean> function = (ConProduct p) -> {
            if (productService.findProduct(p.getProductId()) == null) {
                throw new IllegalArgumentException("product not found : " + p.getProductId());
            }
            return productService.update(p);
        };

        ConAssistants<Boolean> assistants = new ConAssistants<>(productList, function);

        assistants.work();

        // 是否有任务失败了
        Assert.assertTrue(assistants.existFail());
        // 随机获取一个失败的
        Assert.assertNotNull(assistants.anyFailKey());
        // 找出所有的失败任务
        Assert.assertTrue(CollectionMapUtil.isNotEmpty(assistants.allFailKeys()));
        // 找出随机一个失败的
        ConProduct fConProduct = assistants.anyFailQuestion();
        Assert.assertEquals((long) fConProduct.getProductId(), (long) conProduct1003.getProductId());

        // 执行成功
        Assert.assertFalse(assistants.checkFailKey(conProduct1001.questionKey()));
        Assert.assertFalse(assistants.checkFailKey(conProduct1002.questionKey()));
        // 执行失败
        Assert.assertTrue(assistants.checkFailKey(conProduct1003.questionKey()));

        // 答案是空的
        Assert.assertNull(assistants.getAnswer(conProduct1003.questionKey()));
    }

    @Test
    public void testAnyFailDefaultConQuestion() {

        List<Integer> integers = Lists.newArrayList(0, 1, 2);

        // 模拟失败
        Function<DefaultConQuestion<Integer>, Integer> function = (DefaultConQuestion<Integer> q) -> 10 / q.realQuestion();

        ConAssistants<Integer> assistants = ConAssistants.ofBaseType(integers, function);

        assistants.work();

        DefaultConQuestion<Integer> defaultConQuestion = assistants.anyFailQuestion();
        Assert.assertEquals(0,(int) defaultConQuestion.realQuestion());

        Integer errQ = assistants.anyFailDefaultConQuestion();
        Assert.assertEquals(0,(int) errQ);
    }

}

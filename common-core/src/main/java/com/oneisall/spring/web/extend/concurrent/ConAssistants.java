package com.oneisall.spring.web.extend.concurrent;

import com.oneisall.spring.web.extend.exception.BusinessException;
import com.oneisall.spring.web.extend.utils.CollectionMapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 并发助手
 *
 * @author liuzhicong
 **/
@Slf4j
public class ConAssistants<V> {

    private static final Executor DEFAULT_EXECUTOR;

    static {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 默认IO密集，故用 x2
        executor.setCorePoolSize(cpuCoreNum * 2);
        executor.setMaxPoolSize(cpuCoreNum * 2);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("ConAssistants-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        DEFAULT_EXECUTOR = executor;
    }

    private final List<? extends ConQuestion> questions;

    private final Map<String, ConQuestion> questionMap;

    private final Function<ConQuestion, V> answerWay;

    private final Map<String, V> answers;

    private final Executor assistantExecutor;

    // 失败原因

    public enum FailReason {
        EXCEPTION, NULL
    }

    // 无返回值，提供默认的返回
    public enum VoidValue {
        VOID
    }

    // 预留排查

    private final Map<String, FailReason> failRecords = new ConcurrentHashMap<>();

    // 普通构造函数

    @SuppressWarnings("unchecked")
    public ConAssistants(List<? extends ConQuestion> questions, Function<? extends ConQuestion, V> answerWay, Executor assistantExecutor) {
        this.questions = CollectionMapUtil.null2empty(questions);
        this.answers = CollectionMapUtil.newConcurrentHashMapWithExpectedSize(this.questions.size());
        this.assistantExecutor = assistantExecutor == null ? DEFAULT_EXECUTOR : assistantExecutor;
        this.answerWay = (Function<ConQuestion, V>) answerWay;
        this.questionMap = questions.stream().collect(Collectors.toMap(ConQuestion::questionKey, Function.identity(), (o1, o2) -> o1));
    }

    public ConAssistants(List<? extends ConQuestion> questions, Function<? extends ConQuestion, V> answerWay) {
        this(questions, answerWay, null);
    }

    // 一些特性支持的静态构造方法

    // question是Integer,String,Long类型，包装成 DefaultConQuestion
    // 即 List<Integer>

    public static <Q, V> ConAssistants<V> ofBaseType(List<Q> questions, Function<? extends DefaultConQuestion<Q>, V> answerWay, Executor assistantExecutor) {
        List<DefaultConQuestion<Q>> questionList = CollectionMapUtil.null2empty(questions).stream()
                .map(DefaultConQuestion::new)
                .collect(Collectors.toList());

        return new ConAssistants<>(questionList, answerWay, assistantExecutor);
    }

    public static <Q, V> ConAssistants<V> ofBaseType(List<Q> questions, Function<? extends DefaultConQuestion<Q>, V> answerWay) {
        return ofBaseType(questions, answerWay, null);
    }

    // 支持无结果，void类型返回

    public static ConAssistants<VoidValue> ofVoid(List<? extends ConQuestion> questions, Consumer<? extends ConQuestion> voidAnswerWay, Executor assistantExecutor) {
        Function<ConQuestion, VoidValue> wrapperVoid = wrapperVoid(voidAnswerWay);
        return new ConAssistants<>(questions, wrapperVoid, assistantExecutor);
    }

    public static ConAssistants<VoidValue> ofVoid(List<? extends ConQuestion> questions, Consumer<? extends ConQuestion> voidAnswerWay) {
        return ofVoid(questions, voidAnswerWay, null);
    }

    // List<Integer,String,Long...> 并且支持无结果，void类型返回

    public static <Q> ConAssistants<VoidValue> ofVoidBaseType(List<Q> questions, Consumer<? extends DefaultConQuestion<Q>> voidAnswerWay, Executor assistantExecutor) {
        Function<DefaultConQuestion<Q>, VoidValue> wrapperVoid = wrapperBaseTypeVoid(voidAnswerWay);
        return ofBaseType(questions, wrapperVoid, assistantExecutor);
    }

    public static <Q> ConAssistants<VoidValue> ofVoidBaseType(List<Q> questions, Consumer<? extends DefaultConQuestion<Q>> voidAnswerWay) {
        return ofVoidBaseType(questions, voidAnswerWay, null);
    }


    /**
     * consumer 转换 function ，默认返回 VoidValue
     */
    private static Function<ConQuestion, VoidValue> wrapperVoid(Consumer<? extends ConQuestion> voidAnswerWay) {

        @SuppressWarnings("unchecked")
        Consumer<ConQuestion> answerWay = (Consumer<ConQuestion>) voidAnswerWay;

        return conQuestion -> {
            answerWay.accept(conQuestion);
            return VoidValue.VOID;
        };
    }

    /**
     * String,Integer 等无返回的 consumer 转换 function ，默认返回 VoidValue
     */
    private static <Q> Function<DefaultConQuestion<Q>, VoidValue> wrapperBaseTypeVoid(Consumer<? extends DefaultConQuestion<Q>> voidAnswerWay) {

        @SuppressWarnings("unchecked")
        Consumer<DefaultConQuestion<Q>> answerWay = (Consumer<DefaultConQuestion<Q>>) voidAnswerWay;

        return conQuestion -> {
            answerWay.accept(conQuestion);
            return VoidValue.VOID;
        };
    }

    /**
     * 开始并发执行，获取答案
     */
    public void work() {

        if (CollectionMapUtil.isEmpty(questions)) {
            return;
        }

        List<CompletableFuture<Void>> futuresList = new ArrayList<>(questions.size());
        for (ConQuestion question : questions) {
            String questionKey = question.questionKey();
            CompletableFuture<Void> cf = CompletableFuture
                    .runAsync(() -> {
                                V answer = answerWay.apply(question);
                                if (answer != null) {
                                    answers.put(questionKey, answer);
                                } else {
                                    failRecords.put(questionKey, FailReason.NULL);
                                    log.error("The assistant get the null value , questionKey={}", questionKey);
                                }
                            },
                            assistantExecutor)
                    .exceptionally(e -> {
                        Throwable cause = e.getCause() == null ? e : e.getCause();
                        log.error("An cause occurred while the assistant was working , questionKey={}", questionKey, cause);
                        failRecords.put(questionKey, FailReason.EXCEPTION);
                        return null;
                    });
            futuresList.add(cf);
        }
        CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0])).join();
    }

    public V getAnswer(String key) {
        return answers.get(key);
    }

    public boolean existFail() {
        return failRecords.size() != 0;
    }

    public boolean checkFailKey(String questionKey) {
        return failRecords.containsKey(questionKey) || getAnswer(questionKey) == null;
    }

    public String anyFailKey() {
        return failRecords.entrySet().stream().findAny().map(Map.Entry::getKey).orElse(null);
    }

    public Set<String> allFailKeys() {
        return failRecords.keySet();
    }

    @SuppressWarnings("unchecked")
    public <C extends ConQuestion> C anyFailQuestion() {
        ConQuestion conQuestion = questionMap.get(anyFailKey());
        if (conQuestion == null) {
            return null;
        }
        return (C) conQuestion;
    }

    @SuppressWarnings("unchecked")
    public <Q> Q anyFailDefaultConQuestion() {
        ConQuestion conQuestion = questionMap.get(anyFailKey());
        if (conQuestion == null) {
            return null;
        }

        if (!(conQuestion instanceof DefaultConQuestion)) {
            throw new BusinessException(ConAssistantExceptionEnum.TYPE_CAST_ERROR);
        }

        DefaultConQuestion<Q> defaultConQuestion = (DefaultConQuestion<Q>) conQuestion;
        return defaultConQuestion.realQuestion();
    }
}

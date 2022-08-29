package com.oneisall.spring.web.extend.concurrent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.oneisall.spring.web.extend.exception.BusinessException;
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
 * 任务助手
 *
 * @author liuzhicong
 **/
@Slf4j
public class ConTaskAssistants<V> {

    /**
     * 默认线程池，使用 spring 的 ThreadPoolTaskExecutor
     */
    private static final Executor DEFAULT_EXECUTOR;

    static {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 默认IO密集，故用 x2
        executor.setCorePoolSize(cpuCoreNum * 2);
        executor.setMaxPoolSize(cpuCoreNum * 2);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("ConTask-Thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        DEFAULT_EXECUTOR = executor;
    }

    /** 一类任务列表 */
    private final List<? extends ConTask> conTasks;

    /**
     * 任务列表转map
     */
    private final Map<String, ConTask> conTaskMap;

    /**
     * 处理任务的统一方式，工作方式
     */
    private final Function<ConTask, V> workerFunction;

    /**
     * 任务结果集
     */
    private final Map<String, V> taskResults;

    /**
     * 工作线程池
     */
    private final Executor workerExecutor;

    /**
     * 预留排查的失败列表及原因
     */
    private final Map<String, ConTaskFailReason> failRecords = new ConcurrentHashMap<>();

    /**
     * @param conTasks       同类任务列表
     * @param workerFunction 处理任务的工作方式
     * @param workerExecutor 工作线程池
     */
    @SuppressWarnings("unchecked")
    public ConTaskAssistants(List<? extends ConTask> conTasks, Function<? extends ConTask, V> workerFunction, Executor workerExecutor) {
        this.conTasks = CollUtil.emptyIfNull(conTasks);
        this.taskResults = MapUtil.newConcurrentHashMap(this.conTasks.size());
        this.workerExecutor = workerExecutor == null ? DEFAULT_EXECUTOR : workerExecutor;
        this.workerFunction = (Function<ConTask, V>) workerFunction;
        this.conTaskMap = conTasks.stream().collect(Collectors.toMap(ConTask::taskKey, Function.identity(), (o1, o2) -> o1));
    }

    /**
     * 方法重载
     */
    public ConTaskAssistants(List<? extends ConTask> conTasks, Function<? extends ConTask, V> workerFunction) {
        this(conTasks, workerFunction, null);
    }

    /**
     * @param baseTypeTasks  任务是 Integer,String,Long 类型，即 List<Integer> 此种入参
     * @param workerFunction 处理任务的方式，Integer 任务转成 DefaultConTask<Integer> 作为function第一个参数
     * @param workerExecutor 工作线程池
     * @param <Q>            Integer,String,Long等基础类型
     * @param <V>            结果
     */
    public static <Q, V> ConTaskAssistants<V> ofBaseType(List<Q> baseTypeTasks, Function<? extends DefaultConTask<Q>, V> workerFunction, Executor workerExecutor) {
        List<DefaultConTask<Q>> conTaskList = CollUtil.emptyIfNull(baseTypeTasks).stream()
                .map(DefaultConTask::new)
                .collect(Collectors.toList());

        return new ConTaskAssistants<>(conTaskList, workerFunction, workerExecutor);
    }

    /**
     * 方法重载
     */
    public static <Q, V> ConTaskAssistants<V> ofBaseType(List<Q> baseTypeTasks, Function<? extends DefaultConTask<Q>, V> workerFunction) {
        return ofBaseType(baseTypeTasks, workerFunction, null);
    }

    /**
     * 支持无结果，void 类型返回
     *
     * @param voidTasks          任务列表，但是此种任务的结果可以忽略
     * @param voidWorkerConsumer 处理任务的方式，无结果，内部将被转成 Function<ConTask, DefaultVoidReturn>
     * @param workerExecutor     工作线程池
     */
    public static ConTaskAssistants<DefaultVoidReturn> ofVoid(List<? extends ConTask> voidTasks, Consumer<? extends ConTask> voidWorkerConsumer, Executor workerExecutor) {
        Function<ConTask, DefaultVoidReturn> wrapperVoid = wrapperVoid(voidWorkerConsumer);
        return new ConTaskAssistants<>(voidTasks, wrapperVoid, workerExecutor);
    }

    /**
     * 方法重载
     */
    public static ConTaskAssistants<DefaultVoidReturn> ofVoid(List<? extends ConTask> voidTasks, Consumer<? extends ConTask> voidWorkerConsumer) {
        return ofVoid(voidTasks, voidWorkerConsumer, null);
    }


    /**
     * 支持无结果，void类型返回，且任务是 Integer,String,Long 类型
     *
     * @param baseTypeVoidTasks          任务是 Integer,String,Long 类型，即 List<Integer> 此种入参，且此种任务的结果可以忽略
     * @param baseTypeVoidWorkerConsumer 处理任务的方式，无结果，内部将被转成 Function<DefaultConTask<Q>, DefaultVoidReturn>
     * @param assistantExecutor          工作线程池
     */
    public static <Q> ConTaskAssistants<DefaultVoidReturn> ofVoidBaseType(List<Q> baseTypeVoidTasks, Consumer<? extends DefaultConTask<Q>> baseTypeVoidWorkerConsumer, Executor assistantExecutor) {
        Function<DefaultConTask<Q>, DefaultVoidReturn> wrapperVoid = wrapperBaseTypeVoid(baseTypeVoidWorkerConsumer);
        return ofBaseType(baseTypeVoidTasks, wrapperVoid, assistantExecutor);
    }

    /**
     * 方法重载
     */
    public static <Q> ConTaskAssistants<DefaultVoidReturn> ofVoidBaseType(List<Q> baseTypeVoidTasks, Consumer<? extends DefaultConTask<Q>> baseTypeVoidWorkerConsumer) {
        return ofVoidBaseType(baseTypeVoidTasks, baseTypeVoidWorkerConsumer, null);
    }


    /**
     * consumer 转换 function ，默认返回 DefaultVoidReturn
     */
    private static Function<ConTask, DefaultVoidReturn> wrapperVoid(Consumer<? extends ConTask> voidWorkerConsumer) {

        @SuppressWarnings("unchecked")
        Consumer<ConTask> castConsumer = (Consumer<ConTask>) voidWorkerConsumer;

        return conTask -> {
            castConsumer.accept(conTask);
            return DefaultVoidReturn.VOID;
        };
    }

    /**
     * String,Integer 等无返回的 consumer 转换 function ，默认返回 DefaultVoidReturn
     */
    private static <Q> Function<DefaultConTask<Q>, DefaultVoidReturn> wrapperBaseTypeVoid(Consumer<? extends DefaultConTask<Q>> voidWorkerConsumer) {

        @SuppressWarnings("unchecked")
        Consumer<DefaultConTask<Q>> castConsumer = (Consumer<DefaultConTask<Q>>) voidWorkerConsumer;

        return conTask -> {
            castConsumer.accept(conTask);
            return DefaultVoidReturn.VOID;
        };
    }

    /**
     * 开始并发执行，获取答案
     */
    public void work() {

        if (CollUtil.isEmpty(conTasks)) {
            return;
        }

        List<CompletableFuture<Void>> futuresList = new ArrayList<>(conTasks.size());
        for (ConTask conTask : conTasks) {
            String taskKey = conTask.taskKey();
            CompletableFuture<Void> cf = CompletableFuture
                    .runAsync(() -> {
                                V taskResult = workerFunction.apply(conTask);
                                if (taskResult != null) {
                                    taskResults.put(taskKey, taskResult);
                                } else {
                                    failRecords.put(taskKey, ConTaskFailReason.NULL);
                                    log.error("The assistant get the null value , taskKey={}", taskKey);
                                }
                            },
                            workerExecutor)
                    .exceptionally(e -> {
                        Throwable cause = e.getCause() == null ? e : e.getCause();
                        log.error("An cause occurred while the assistant was working , taskKey={}", taskKey, cause);
                        failRecords.put(taskKey, ConTaskFailReason.EXCEPTION);
                        return null;
                    });
            futuresList.add(cf);
        }
        CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0])).join();
    }

    public V getResult(String key) {
        return taskResults.get(key);
    }

    public boolean existFail() {
        return failRecords.size() != 0;
    }

    public boolean checkFailKey(String taskKey) {
        return failRecords.containsKey(taskKey) || getResult(taskKey) == null;
    }

    public String anyFailKey() {
        return failRecords.entrySet().stream().findAny().map(Map.Entry::getKey).orElse(null);
    }

    public Set<String> allFailKeys() {
        return failRecords.keySet();
    }

    @SuppressWarnings("unchecked")
    public <C extends ConTask> C anyFailTask() {
        ConTask conTask = conTaskMap.get(anyFailKey());
        if (conTask == null) {
            return null;
        }
        return (C) conTask;
    }

    @SuppressWarnings("unchecked")
    public <Q> Q anyFailDefaultConTask() {
        ConTask conTask = conTaskMap.get(anyFailKey());
        if (conTask == null) {
            return null;
        }

        if (!(conTask instanceof DefaultConTask)) {
            throw new BusinessException(ConTaskExceptionEnum.TYPE_CAST_ERROR);
        }

        DefaultConTask<Q> castDefaultConTask = (DefaultConTask<Q>) conTask;
        return castDefaultConTask.realTaskValue();
    }
}

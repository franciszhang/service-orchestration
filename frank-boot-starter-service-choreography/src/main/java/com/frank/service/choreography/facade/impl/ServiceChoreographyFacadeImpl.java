package com.frank.service.choreography.facade.impl;

import com.alibaba.fastjson.JSON;
import com.frank.service.choreography.engine.executor.ExecutorFactory;
import com.frank.service.choreography.engine.processor.DataProcessor;
import com.frank.service.choreography.engine.processor.ParserProcessor;
import com.frank.service.choreography.facade.ServiceChoreographyFacade;
import com.frank.service.choreography.engine.pojo.Expression;
import com.frank.service.choreography.engine.pojo.DslData;
import com.frank.service.choreography.engine.pojo.Task;
import com.frank.service.choreography.engine.util.ThreadFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author francis
 * @version 2022-03-22
 */
@Slf4j
@Service
public class ServiceChoreographyFacadeImpl implements ServiceChoreographyFacade {
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 50, 1, TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(1000),
            new ThreadFactoryImpl("serviceChoreographyInvokePool"), (r, e) -> {
        log.warn("attention! choreography pool is full");
        if (!e.isShutdown()) {
            r.run();
        }
    });

    @Override
    public Map<String, Object> process(String dslJsonStr, String paramJsonStr) {
        DslData dsl = ParserProcessor.parser(dslJsonStr, paramJsonStr);
        List<Expression> expressions = DataProcessor.getExpressions(dsl);

        long l = System.currentTimeMillis();
        Map<String, Object> resultMap = doProcess(dsl, expressions);
        long l1 = System.currentTimeMillis();

        log.info("serviceChoreography-cost[{}]ms", (l1 - l));
        return resultMap;
    }

    private Map<String, Object> doProcess(DslData dsl, List<Expression> expressions) {
        List<Task> tasks = new ArrayList<>(dsl.getTasks());
        List<Task> asyncTasks = dsl.getTasks().stream().filter(task -> "async".equals(task.getExecuteMode())).collect(Collectors.toList());
        tasks.removeAll(asyncTasks);

        asyncDoProcessTasks(asyncTasks, expressions);
        syncDoProcessTasks(tasks, expressions);

        DataProcessor.setDslOutputVal(dsl, expressions);
        return dsl.getOutputs();
    }

    private void asyncDoProcessTasks(List<Task> asyncTasks, List<Expression> expressions) {
        List<CompletableFuture<Task>> collect = asyncTasks.stream().map(task -> CompletableFuture.supplyAsync(() -> {
            doProcessTask(task, expressions);
            return task;
        }, executor).exceptionally(e -> {
            log.error("task-error,task[{}]", task, e);
            throw new RuntimeException(e);
        })).collect(Collectors.toList());
        CompletableFuture.allOf(collect.toArray(new CompletableFuture[]{})).join();
    }

    private void syncDoProcessTasks(List<Task> syncTasks, List<Expression> expressions) {
        for (Task task : syncTasks) {
            doProcessTask(task, expressions);
        }
    }

    private void doProcessTask(Task task, List<Expression> expressions) {
        DataProcessor.setDslInputVal(task, expressions);
        log.info("doProcessTask-request:[{}]", JSON.toJSONString(task));

        String result = ExecutorFactory.getExecutor(task.getTaskType()).invoke(task);
        log.info("doProcessTask-response:[{}]", result);

        DataProcessor.setExpressionVal(expressions, task.getAlias(), result);
    }

}

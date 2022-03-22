package com.yunzhi.xiaoyuanhao.service.arrange.engine.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author francis
 * @version 2022-03-22
 */
@Component
public class ExecutorFactory {
    @Autowired
    private List<Executor> executors;

    private static final Map<String, Executor> type2executorMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (Executor executor : executors) {
            type2executorMap.put(executor.getType(), executor);
        }
    }

    public static Executor getExecutor(String type) {
        Executor executor = type2executorMap.get(type);
        if (executor == null) {
            throw new RuntimeException("not found current [" + type + "] type executor!");
        }
        return executor;
    }
}
